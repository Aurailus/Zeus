package Zeus.game;

import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.HashMap;


public class BlockManager {
    static final int CHUNK_SIZE = 16;
    private static final boolean MULTITHREADING_ENABLED = ZeusGame.MULTITHREADING_ENABLED;
    ZeusGame game;

    private final static int QUEUE_SIZE = 512;
    private ArrayList<BlockChunk> blockChunkQueue;
    private HashMap<Vector3i, BlockChunk> blockChunks;

    public BlockManager(ZeusGame game) {
        this.game = game;
        blockChunkQueue = new ArrayList<>();
        blockChunks = new HashMap<>();
    }

    static private int blockCoordToLocal(int num) {
        return (num >= 0) ? (num % CHUNK_SIZE) : ((CHUNK_SIZE - 1) - Math.abs(num + 1) % CHUNK_SIZE);
    }

    public BlockChunk getChunk(int x, int y, int z) {
        return getChunk(new Vector3i(x, y, z));
    }

    public BlockChunk getChunk(Vector3i pos) {
        return blockChunks.get(pos);
    }

    public BlockChunk setChunk(BlockChunk chunk, int x, int y, int z) {
        return setChunk(chunk, new Vector3i(x, y, z));
    }

    public BlockChunk setChunk(BlockChunk chunk, Vector3i pos) {
        blockChunks.put(pos, chunk);
        return chunk;
    }

    //
    // Block Functions
    //

    public int getBlock(int x, int y, int z) {
        return getBlock(new Vector3i(x, y, z));
    }

    int getBlock(Vector3i pos) {
        var chunk = getChunk(pos);
        if (chunk == null) return 0; //TODO: Load from file here

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
                        chunk.generate();
                    }
                }
            }
        }
    }

    void generateWorld(Vector3i pos, Vector3i range) {
        if (MULTITHREADING_ENABLED) {
            Thread[] threads = new Thread[range.y];
            for (var i = 0; i < threads.length; i++) {
                threads[i] = new BlockGenThread(this, new Vector3i(pos.x, pos.y + i, pos.z), new Vector3i(range.x, 1, range.z));
                threads[i].start();
            }
            try {
                for (Thread thread : threads) {
                    thread.join();
                }
            } catch (Exception e) {
                System.out.println("Interrupted exception occured!");
                e.printStackTrace();
            }
        }
        else {
            for (var i = 0; i < range.x; i++) {
                for (var j = 0; j < range.y; j++) {
                    for (var k = 0; k < range.z; k++) {
                        var cPos = new Vector3i(pos.x + i, pos.y + j, pos.z + k);
                        var chunk = new BlockChunk(this, cPos);
                        chunk.generate();
                        setChunk(chunk, cPos);
                    }
                }
            }
        }
    }
}
