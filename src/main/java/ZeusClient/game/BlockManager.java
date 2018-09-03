//package ZeusClient.game;
//
//import org.joml.Vector3i;
//
//import java.util.*;
//
//
//public class BlockManager {
//    static final int CHUNK_SIZE = 16;
//    static final int REGION_SIZE = 16;
//
//    private final static int QUEUE_SIZE = 4096;
//    private HashMap<Vector3i, EncodedBlockChunk> blockChunks;
//    private ArrayList<EncodedBlockChunk> blockChunkQueue;
//
//    private final long seed = Math.round(Math.random()*100000);
//
//    public BlockManager() {
//        blockChunkQueue = new ArrayList<>(QUEUE_SIZE + 1);
//        blockChunks = new HashMap<>();
//    }
//
//    static private int chunkCoordToLocal(int num) {
//        return (num >= 0) ? (num % REGION_SIZE) : ((REGION_SIZE - 1) - Math.abs(num + 1) % REGION_SIZE);
//    }
//
//    static private int blockCoordToLocal(int num) {
//        return (num >= 0) ? (num % CHUNK_SIZE) : ((CHUNK_SIZE - 1) - Math.abs(num + 1) % CHUNK_SIZE);
//    }
//
//    private EncodedBlockChunk loadChunk(Vector3i pos) {
//        try {
//            var regionPos = new Vector3i((int)Math.floor((float)pos.x / REGION_SIZE), (int)Math.floor((float)pos.y / REGION_SIZE), (int)Math.floor((float)pos.z / REGION_SIZE));
//            var file = new RegFileManip(regionPos);
//            if (!file.regionExists()) return null;
//            file.beginAccess();
//
//            var chunkPos = new Vector3i(chunkCoordToLocal(pos.x), chunkCoordToLocal(pos.y), chunkCoordToLocal(pos.z));
//            short[] blocks = file.readChunk(chunkPos);
//
//            file.endAccess();
//
//            if (blocks != null) return new EncodedBlockChunk(this, pos).load(blocks);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public EncodedBlockChunk getChunk(int x, int y, int z) {
//        return getChunk(new Vector3i(x, y, z));
//    }
//
//    public EncodedBlockChunk getChunk(Vector3i pos) {
//        EncodedBlockChunk chunk;
//        synchronized(this) {
//            chunk = blockChunks.get(pos);
//        }
//        return chunk;
//    }
//
//    public EncodedBlockChunk setChunk(EncodedBlockChunk chunk, int x, int y, int z) {
//        return setChunk(chunk, new Vector3i(x, y, z));
//    }
//
//    public synchronized EncodedBlockChunk setChunk(EncodedBlockChunk chunk, Vector3i pos) {
//        blockChunks.put(pos, chunk);
//        blockChunkQueue.add(chunk);
//        if (blockChunkQueue.size() > QUEUE_SIZE) {
//            EncodedBlockChunk delChunk = blockChunkQueue.get(0);
//            blockChunks.remove(delChunk.position);
//            blockChunkQueue.remove(0);
//        }
//
//        return chunk;
//    }
//
//    public EncodedBlockChunk setChunk(short[] data, int x, int y, int z) {
//        return setChunk(data, x, y, z);
//    }
//
//    public synchronized EncodedBlockChunk setChunk(short[] data, Vector3i pos) {
//        EncodedBlockChunk chunk = new EncodedBlockChunk(this, pos);
//        chunk.setBlocks(data);
//        setChunk(chunk, pos);
//        return chunk;
//    }
//
//    //
//    // Block Functions
//    //
//
//    public int getBlock(int x, int y, int z) {
//        return getBlock(new Vector3i(x, y, z));
//    }
//
//    public int getBlock(Vector3i pos) {
//        var chunkPos = new Vector3i((int)Math.floor((float)pos.x / CHUNK_SIZE), (int)Math.floor((float)pos.y / CHUNK_SIZE), (int)Math.floor((float)pos.z / CHUNK_SIZE));
//        var chunk = getChunk(chunkPos);
//        if (chunk == null) return -1;
//        var blockPos = new Vector3i(blockCoordToLocal(pos.x), blockCoordToLocal(pos.y), blockCoordToLocal(pos.z));
//        return chunk.getBlock(blockPos);
//    }
//
//    int setBlock(int block, int x, int y, int z) {
//        return setBlock(block, new Vector3i(x, y, z));
//    }
//
//    int setBlock(int block, Vector3i pos) {
//        var chunk = getChunk(pos);
//        if (chunk == null) return 0; //TODO: Load from file here
//
//        var blockPos = new Vector3i(blockCoordToLocal(pos.x), blockCoordToLocal(pos.y), blockCoordToLocal(pos.z));
//
//        return chunk.setBlock(block, blockPos);
//    }
//}
