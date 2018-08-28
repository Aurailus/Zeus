package ZeusClient.game;

import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.Vector;

public class ChunkHandler {
    private MeshManager meshManager;

    private ArrayList<Vector3i> chunksToLoad;
    private ArrayList<Vector3i> chunksLoading;

    private final Object threadLock = new Object();

    final static private int MAX_THREADS = 128;
    private Vector<Thread> threads;


    public ChunkHandler(MeshManager meshManager) {
        this.meshManager = meshManager;

        chunksToLoad = new ArrayList<>();
        chunksLoading = new ArrayList<>();

        threads = new Vector<>(MAX_THREADS);
    }

    public boolean loadingChunk(Vector3i chunk) {
        synchronized (threadLock) {
            return chunksLoading.contains(chunk) || chunksToLoad.contains(chunk);
        }
    }

    private void addChunk(Vector3i chunk) {
        synchronized (threadLock) {
            chunksToLoad.add(chunk);
        }
    }

    public void loadChunk(Vector3i chunk) {
        if (!loadingChunk(chunk)) addChunk(chunk);
    }

    public void update(int maxTime) {
        long millis = System.currentTimeMillis();
        synchronized (threadLock) {
            for (var i = 0; i < MAX_THREADS - threads.size(); i++) {

                if (chunksToLoad.size() > 0) {
                    var chunkPos = chunksToLoad.get(0);
                    var thread = new ChunkHandlerThread(this, chunkPos);
                    thread.start();
                    threads.add(thread);
                    chunksLoading.add(chunkPos);
                    chunksToLoad.remove(0);
                }
            }
        }
//        System.out.println("Update took " + (System.currentTimeMillis() - millis) + "ms");
    }

    public void render() {
    }

    private class ChunkHandlerThread extends Thread implements Runnable {
        Vector3i pos;
        ChunkHandler parent;

        public ChunkHandlerThread(ChunkHandler parent, Vector3i chunk) {
            this.pos = chunk;
            this.parent = parent;
        }

        @Override
        public void run() {
//            var regionPos = new Vector3i((int)Math.floor((float)pos.x / REGION_SIZE), (int)Math.floor((float)pos.y / REGION_SIZE), (int)Math.floor((float)pos.z / REGION_SIZE));
//            var chunkPos  = new Vector3i(chunkCoordToLocal(pos.x), chunkCoordToLocal(pos.y), chunkCoordToLocal(pos.z));

//            var chunk = new MeshChunk(meshManager, regionPos, chunkPos);
            var chunk = parent.meshManager.createChunk(pos);
            chunk.generateMesh();
            synchronized (parent.threadLock) {
                threads.remove(this);
            }
        }
    }
}
