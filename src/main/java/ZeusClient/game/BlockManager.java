package ZeusClient.game;

import org.joml.Vector3i;

import java.util.*;


public class BlockManager {
    static final int CHUNK_SIZE = 16;
    static final int REGION_SIZE = 16;

    private final static int QUEUE_SIZE = 4096;
    private HashMap<Vector3i, BlockChunk> blockChunks;
    private ArrayList<BlockChunk> blockChunkQueue;

    private final long seed = Math.round(Math.random()*100000);

    public BlockManager() {
        blockChunkQueue = new ArrayList<>(QUEUE_SIZE + 1);
        blockChunks = new HashMap<>();
    }

    static private int chunkCoordToLocal(int num) {
        return (num >= 0) ? (num % REGION_SIZE) : ((REGION_SIZE - 1) - Math.abs(num + 1) % REGION_SIZE);
    }

    static private int blockCoordToLocal(int num) {
        return (num >= 0) ? (num % CHUNK_SIZE) : ((CHUNK_SIZE - 1) - Math.abs(num + 1) % CHUNK_SIZE);
    }

    private BlockChunk loadChunk(Vector3i pos) {
        try {
            var regionPos = new Vector3i((int)Math.floor((float)pos.x / REGION_SIZE), (int)Math.floor((float)pos.y / REGION_SIZE), (int)Math.floor((float)pos.z / REGION_SIZE));
            var file = new RegFileManip(regionPos);
            if (!file.regionExists()) return null;
            file.beginAccess();

            var chunkPos = new Vector3i(chunkCoordToLocal(pos.x), chunkCoordToLocal(pos.y), chunkCoordToLocal(pos.z));
            short[] blocks = file.readChunk(chunkPos);

            file.endAccess();

            if (blocks != null) return new BlockChunk(this, pos).load(blocks);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public BlockChunk getChunk(int x, int y, int z) {
        return getChunk(new Vector3i(x, y, z));
    }

    public BlockChunk getChunk(Vector3i pos) {
        BlockChunk chunk;
        synchronized(this) {
            chunk = blockChunks.get(pos);
        }

        if (chunk != null) return chunk;
        chunk = loadChunk(pos);
        if (chunk != null) {
            synchronized (this) {
                setChunk(chunk, pos);
                return chunk;
            }
        }
        synchronized (this) {
            if (blockChunks.get(pos) == null) {
                chunk = new BlockChunk(this, pos);
                chunk.generate(seed);
                setChunk(chunk, pos);
            }
        }
        return chunk;
    }

    public BlockChunk setChunk(BlockChunk chunk, int x, int y, int z) {
        return setChunk(chunk, new Vector3i(x, y, z));
    }

    public synchronized BlockChunk setChunk(BlockChunk chunk, Vector3i pos) {
        blockChunks.put(pos, chunk);
        blockChunkQueue.add(chunk);
        if (blockChunkQueue.size() > QUEUE_SIZE) {
            BlockChunk delChunk = blockChunkQueue.get(0);
            blockChunks.remove(delChunk.position);
            blockChunkQueue.remove(0);
        }

        return chunk;
    }

    public BlockChunk setChunk(short[] data, int x, int y, int z) {
        return setChunk(data, x, y, z);
    }

    public synchronized BlockChunk setChunk(short[] data, Vector3i pos) {
        BlockChunk chunk = new BlockChunk(this, pos);
        chunk.setBlocks(data);
        setChunk(chunk, pos);
        return chunk;
    }

    //
    // Block Functions
    //

    public int getBlock(int x, int y, int z) {
        return getBlock(new Vector3i(x, y, z));
    }

    public int getBlock(Vector3i pos) {
        var chunkPos = new Vector3i((int)Math.floor((float)pos.x / CHUNK_SIZE), (int)Math.floor((float)pos.y / CHUNK_SIZE), (int)Math.floor((float)pos.z / CHUNK_SIZE));

        var chunk = getChunk(chunkPos);

        var blockPos = new Vector3i(blockCoordToLocal(pos.x), blockCoordToLocal(pos.y), blockCoordToLocal(pos.z));

        return chunk.getBlock(blockPos);
    }

    int setBlock(int block, int x, int y, int z) {
        return setBlock(block, new Vector3i(x, y, z));
    }

    int setBlock(int block, Vector3i pos) {
        var chunk = getChunk(pos);
        if (chunk == null) return 0; //TODO: Load from file here

        var blockPos = new Vector3i(blockCoordToLocal(pos.x), blockCoordToLocal(pos.y), blockCoordToLocal(pos.z));

        return chunk.setBlock(block, blockPos);
    }

    private class BlockGenThread extends Thread implements Runnable {
        BlockManager blockMan;
        Vector3i pos;
        Vector3i range;

        private BlockGenThread(BlockManager blockMan, Vector3i pos, Vector3i range) {
            this.blockMan = blockMan;
            this.pos = pos;
            this.range = range;
        }

        @Override
        public void run() {
            for (var i = 0; i < range.x; i++) {
                for (var j = 0; j < range.y; j++) {
                    for (var k = 0; k < range.z; k++) {
                        var cPos = new Vector3i(pos.x + i, pos.y + j, pos.z + k);
                        var chunk = setChunk(new BlockChunk(this.blockMan, cPos), cPos);
                        chunk.generate(seed);
                    }
                }
            }
        }
    }

//    void generateWorld(Vector3i pos, Vector3i range) {
////        if (MULTITHREADING_ENABLED) {
////            Thread[] threads = new Thread[range.y];
////            for (var i = 0; i < threads.length; i++) {
////                threads[i] = new BlockGenThread(this, new Vector3i(pos.x, pos.y + i, pos.z), new Vector3i(range.x, 1, range.z));
////                threads[i].start();
////            }
////            try {
////                for (Thread thread : threads) {
////                    thread.join();
////                }
////            } catch (Exception e) {
////                System.out.println("Interrupted exception occured!");
////                e.printStackTrace();
////            }
////        }
////        else {
//            for (var i = 0; i < range.x; i++) {
//                for (var j = 0; j < range.y; j++) {
//                    for (var k = 0; k < range.z; k++) {
//                        var cPos = new Vector3i(pos.x + i, pos.y + j, pos.z + k);
//                        getChunk(cPos);
////                        var chunk = new BlockChunk(this, cPos);
////                        chunk.generate();
////                        setChunk(chunk, cPos);
//                    }
//                }
//            }
////        }
//    }
}
