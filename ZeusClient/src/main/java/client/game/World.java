package client.game;

import client.engine.Utils;
import client.engine.graphics.Material;
import client.engine.graphics.Texture;
import helpers.ChunkSerializer;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.*;

import static helpers.ArrayTrans3D.CHUNK_SIZE;

public class World {

    private ArrayList<MeshChunk> meshChunks;
    private HashMap<Vector3i, MeshChunk> meshChunkMap;

    private ThreadPoolExecutor meshGenPool;
    private ArrayList<Future> meshGenFutures;
    private ArrayList<Vector3i> adjacentChunkUpdates;

    private ArrayList<BlockChunk> activeChunks;
    private HashMap<Vector3i, BlockChunk> activeChunkMap;


    private ArrayList<EncodedBlockChunk> cachedChunks;
    private HashMap<Vector3i, EncodedBlockChunk> cachedChunkMap;

    private int mapGenTick = 0;

    public World() {

        try {
            var atlas = new FileInputStream(new File("atlas_0.png"));
            MeshChunk.meshMaterial = new Material(new Texture(atlas));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        meshChunks = new ArrayList<>();
        meshChunkMap = new HashMap<>();

        meshGenPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(32);
        meshGenPool.setMaximumPoolSize(64);
        meshGenPool.setKeepAliveTime(32, TimeUnit.SECONDS);

        meshGenFutures = new ArrayList<>();
        adjacentChunkUpdates = new ArrayList<>();

        activeChunks = new ArrayList<>();
        activeChunkMap = new HashMap<>();

        cachedChunks = new ArrayList<>();
        cachedChunkMap = new HashMap<>();
    }

    ArrayList<MeshChunk> getVisibleChunks() {
        return meshChunks;
    }

    public void update() {
        try {
            loadMeshes();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void loadMeshes() throws ExecutionException, InterruptedException {
        int maxNanos = 6_000_000;
        long start = System.nanoTime();

        int count = meshGenFutures.size();
        int processed = 0;
        boolean hadToStop = true;

        Iterator<Future> it = meshGenFutures.iterator();

        while (it.hasNext() && System.nanoTime() - start < maxNanos) {
            Future f = it.next();
            if (f.isDone()) {
                processed ++;

                GenChunkTask.ThreadRet ret = (GenChunkTask.ThreadRet) f.get();
                if (ret != null) {
                    it.remove();

                    if (activeChunkMap.get(ret.position) == null) adjacentChunkUpdates.add(ret.position);
                    activeChunkMap.put(ret.position, ret.blockChunk);

                    var eChunk = meshChunkMap.get(ret.position);
                    if (eChunk != null) {
                        meshChunkMap.remove(ret.position);
                        meshChunks.remove(eChunk);
                    }

                    if (ret.meshChunk != null && ret.meshChunk.getMesh() != null) {
                        ret.meshChunk.getMesh().init();

                        if (ret.meshChunk.getMesh().getVertexCount() != 0) {
                            meshChunkMap.put(ret.position, ret.meshChunk);
                            meshChunks.add(ret.meshChunk);
                        }
                    }
                }
            }
        }
        if (!it.hasNext()) hadToStop = false;

        if (count > 0) System.out.println(
                "Built " + Utils.pad(((float)processed/(float)count*100f), 8) +
                "% of " + Utils.pad(count, 6) + " chunks. " +
                "Tick: " + Utils.pad(mapGenTick++, 8) +
                (hadToStop ? ". Had to stop." : "."));

        adjacentChunkUpdates.forEach(this::updateChunksAround);
        adjacentChunkUpdates.clear();
    }

    private synchronized void updateChunksAround(Vector3i pos) {
        Vector3i modPos;

        modPos = new Vector3i(pos).add(1, 0, 0);
        BlockChunk chunk = activeChunkMap.get(modPos);
        if (chunk != null) {
            updateChunk(modPos, chunk);
        }

        modPos = new Vector3i(pos).add(-1, 0, 0);
        chunk = activeChunkMap.get(modPos);
        if (chunk != null) {
            updateChunk(modPos, chunk);
        }

        modPos = new Vector3i(pos).add(0, 0, 1);
        chunk = activeChunkMap.get(modPos);
        if (chunk != null) {
            updateChunk(modPos, chunk);
        }

        modPos = new Vector3i(pos).add(0, 0, -1);
        chunk = activeChunkMap.get(modPos);
        if (chunk != null) {
            updateChunk(modPos, chunk);
        }

        modPos = new Vector3i(pos).add(0, 1, 0);
        chunk = activeChunkMap.get(modPos);
        if (chunk != null) {
            updateChunk(modPos, chunk);
        }

        modPos = new Vector3i(pos).add(0, -1, 0);
        chunk = activeChunkMap.get(modPos);
        if (chunk != null) {
            updateChunk(modPos, chunk);
        }
    }

    synchronized boolean hasChunk(Vector3i pos) {
        return activeChunkMap.containsKey(pos) || cachedChunkMap.containsKey(pos);
    }

    synchronized void addChunk(Vector3i pos, byte[] data) {
        var task = new GenChunkTask(pos, data);
        meshGenFutures.add(meshGenPool.submit(task));
    }

    synchronized void updateChunk(Vector3i pos, BlockChunk chunk) {
        var task = new GenChunkTask(pos, chunk);
        meshGenFutures.add(meshGenPool.submit(task));
    }

    synchronized BlockChunk getChunk(Vector3i pos) {
        return activeChunkMap.get(pos);
    }

    public int getBlock(int x, int y, int z) {
        return getBlock(new Vector3i(x, y, z));
    }

    public int getBlock(Vector3i pos) {
        var chunkPos = coordsToChunk(new Vector3f(pos));
        var chunk = getChunk(chunkPos);
        if (chunk == null) return -1;
        var blockPos = new Vector3i(coordToLocal(pos.x), coordToLocal(pos.y), coordToLocal(pos.z));
        return chunk.getBlock(blockPos);
    }

    private Vector3i coordsToChunk(Vector3f pos) {
        return new Vector3i((int)Math.floor(pos.x / CHUNK_SIZE), (int)Math.floor(pos.y / CHUNK_SIZE), (int)Math.floor(pos.z / CHUNK_SIZE));
    }

    private int coordToLocal(int num) {
        return (num >= 0) ? (num % CHUNK_SIZE) : ((CHUNK_SIZE - 1) - Math.abs(num + 1) % CHUNK_SIZE);
    }

    private class GenChunkTask implements Callable<GenChunkTask.ThreadRet> {
        Vector3i pos;
        byte[] chunkData;
        BlockChunk chunk;

        GenChunkTask(Vector3i pos, byte[] chunkData) {
            this.pos = pos;
            this.chunkData = chunkData;
        }

        GenChunkTask(Vector3i pos, BlockChunk chunk) {
            this.pos = pos;
            this.chunk = chunk;
        }

        private class ThreadRet {
            BlockChunk blockChunk;
            MeshChunk meshChunk;
            Vector3i position;
        }

        @Override
        public ThreadRet call() {
            if (chunk == null) chunk = ChunkSerializer.decodeChunk(chunkData);

            MeshChunk meshChunk = new MeshChunk(pos, Game.world);
            meshChunk.createMesh(chunk);

            ThreadRet r = new ThreadRet();
            r.blockChunk = chunk;
            r.meshChunk = meshChunk;
            r.position = pos;

            return r;
        }
    }

    public void cleanup() {
        meshGenPool.shutdown();
    }
}
