package ZeusClient.game;

import ZeusClient.engine.graphics.Material;
import ZeusClient.engine.graphics.Texture;
import ZeusClient.engine.helpers.ChunkSerializer;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.*;

import static ZeusClient.engine.helpers.ArrayTrans3D.CHUNK_SIZE;

public class ChunkAtlas {

    private ArrayList<MeshChunk> meshChunks;
    private HashSet<Vector3i> loadingChunks;

    private ThreadPoolExecutor meshGenPool;
    private ArrayList<Future> meshGenFutures;


    private ArrayList<BlockChunk> activeChunks;
    private HashMap<Vector3i, BlockChunk> activeChunkMap;


    private ArrayList<EncodedBlockChunk> cachedChunks;
    private HashMap<Vector3i, EncodedBlockChunk> cachedChunkMap;

    public ChunkAtlas() {

        try {
            var atlas = new FileInputStream(new File("atlas_0.png"));
            MeshChunk.meshMaterial = new Material(new Texture(atlas));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        meshChunks = new ArrayList<>();
        loadingChunks = new HashSet<>();

        meshGenPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(16);
        meshGenPool.setMaximumPoolSize(48);
        meshGenPool.setKeepAliveTime(32, TimeUnit.SECONDS);

        meshGenFutures = new ArrayList<>();

        activeChunks = new ArrayList<>();
        activeChunkMap = new HashMap<>();

        cachedChunks = new ArrayList<>();
        cachedChunkMap = new HashMap<>();
    }

    public ArrayList<MeshChunk> getVisibleChunks() {
        return meshChunks;
    }

    public void update() {
        try {
            loadMeshes();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void loadMeshes() throws ExecutionException, InterruptedException {
        int maxTime = 4;
        long start = System.currentTimeMillis();

        Iterator<Future> it = meshGenFutures.iterator();

        while (it.hasNext() && System.currentTimeMillis() - start < maxTime) {
            Future f = it.next();
            if (f.isDone()) {
                GenChunkTask.ThreadRet ret = (GenChunkTask.ThreadRet) f.get();
                if (ret != null) {
                    it.remove();

                    activeChunkMap.put(ret.position, ret.blockChunk);
                    loadingChunks.remove(ret.position);

                    if (ret.meshChunk != null && ret.meshChunk.getMesh() != null) {
                        ret.meshChunk.getMesh().init();

                        if (ret.meshChunk.getMesh().getVertexCount() != 0) {
                            meshChunks.add(ret.meshChunk);
                        }
                    }
                }
            }
        }
    }

    public synchronized void loadChunksAroundPos(Vector3f pos, int range) {
        var cOffset = coordsToChunk(pos);

        for (var i = -range; i < range; i++) {
            for (var j = -range; j < range; j++) {
                for (var k = -range; k < range; k++) {
                    loadChunk(i + cOffset.x, j + cOffset.y, k + cOffset.z);
                }
            }
        }
    }

    public synchronized void loadChunk(int x, int y, int z) {
        Vector3i reqPos = new Vector3i(x, y, z);

        if (!loadingChunks.contains(reqPos) && !activeChunkMap.containsKey(reqPos)) {
            loadingChunks.add(reqPos);
            Game.connection.requestChunk(reqPos, (pos, chunk) -> {
                var task = new GenChunkTask(pos, chunk);
                meshGenFutures.add(meshGenPool.submit(task));
            });
        }
    }

    public synchronized BlockChunk getChunk(Vector3i pos) {
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
        byte[] chunk;

        public GenChunkTask(Vector3i pos, byte[] chunk) {
            this.pos = pos;
            this.chunk = chunk;
        }

        private class ThreadRet {
            public BlockChunk blockChunk;
            public MeshChunk meshChunk;
            public Vector3i position;
        }

        @Override
        public ThreadRet call() {
            BlockChunk blockChunk = ChunkSerializer.decodeChunk(chunk);
            MeshChunk meshChunk = new MeshChunk(pos);
            meshChunk.createMesh(blockChunk);

            ThreadRet r = new ThreadRet();
            r.blockChunk = blockChunk;
            r.meshChunk = meshChunk;
            r.position = pos;

            return r;
        }
    }

    public void cleanup() {
        meshGenPool.shutdown();
    }
}
