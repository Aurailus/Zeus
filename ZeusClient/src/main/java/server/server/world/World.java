package server.server.world;

import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class World {
    private HashMap<Vector3i, Chunk> chunks;
    private MapGen mapGen;

    private ThreadPoolExecutor mapGenPool;

    private ArrayList<Future<HashMap<Vector3i, Chunk>>> genFutures;
    private ArrayList<GenJob> genJobs;

    public World() {
        chunks = new HashMap<>();
        mapGen = new MapGen(this);

        genFutures = new ArrayList<>();
        genJobs = new ArrayList<>();

        mapGenPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(64);
        mapGenPool.setMaximumPoolSize(128);
        mapGenPool.setKeepAliveTime(32, TimeUnit.SECONDS);
    }

    //
    // Main Update methods
    // Update GenChunkTask futures, if done merge chunks
    //
    public void update() throws ExecutionException, InterruptedException {

        Iterator<Future<HashMap<Vector3i, Chunk>>> i = genFutures.iterator();
        while (i.hasNext()) {

            Future<HashMap<Vector3i, Chunk>> future = i.next();

            if (future.isDone()) {
                mergeChunks(future.get());
                i.remove();
            }

        }
    }

    //
    // Merge Chunks from GenChunkTask into main chunk HashMap, overriding properly,
    // Then remove pending chunks from Jobs and call callbacks if applicable
    //
    private void mergeChunks(HashMap<Vector3i, Chunk> newChunks) {

        for (Chunk newChunk : newChunks.values()) {
            Chunk existingChunk = getChunk(newChunk.getPos());
            if (existingChunk == null) chunks.put(newChunk.pos, newChunk);
            else {
                try {
                    existingChunk.mergeChunk(newChunk);
                    //TODO: When this happens, some chunks already in the client could be changed, need to push updates!
                }
                catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }

        for (Vector3i p : newChunks.keySet()) {

            Iterator<GenJob> i = genJobs.iterator();
            while (i.hasNext()) {
                GenJob j = i.next();
                boolean ready = j.acceptChunk(p);
                if (ready) {
                    jobCallback(j);
                    i.remove();
                }
            }
        }
    }

    //
    // Call Job Callback (Return on ClientThread)
    //
    private void jobCallback(GenJob j) {
        Chunk[] vals = new Chunk[j.positions.size()];
        for (var i = 0; i < j.positions.size(); i++) {
            vals[i] = getChunk(j.positions.get(i));
        }
        j.callback.accept(vals);
    }

    private Chunk getChunk(Vector3i pos) {
        return chunks.get(pos);
    }

    //
    // Get chunks requested by positions array. The resulting chunks will either be returned immediately
    // by the consumer, or later when they are done generating and the genJob completion task fires in
    // the update function.
    //
    public void getChunks(ArrayList<Vector3i> positions, Consumer<Chunk[]> chunks) {
        if (positions.size() > 0) {
            GenJob j = new GenJob(positions, chunks);
            for (Vector3i p : positions) {
                if (getChunk(p) != null && getChunk(p).generated) j.acceptChunk(p);

                else {
                    GenChunkTask t = new GenChunkTask(p, mapGen);
                    Future<HashMap<Vector3i, Chunk>> f = mapGenPool.submit(t);
                    genFutures.add(f);
                }
            }
            if (j.isReady()) {
                jobCallback(j);
            } else {
                genJobs.add(j);
            }
        }
        else System.err.println("Empty job requested.");
    }
}
