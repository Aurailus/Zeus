package Zeus.game;

import org.joml.Vector3i;

import java.util.HashMap;
import java.util.Map;


public class BlockManager {
    final int REGION_SIZE = 16;
    final int CHUNK_SIZE = 16;

    final Map<Vector3i, BlockRegion> regions;

    public BlockManager() {
        regions = new HashMap<>();
    }

    //
    // Helper functions
    //

    //Perform operations on coordinates if negative to translate them into 0 indexed array (it's black magic)

    //example input

    // x = -28
    // y = 0
    // z = 5

    //expected output

    // rX = -2
    // rY = 0
    // rZ = 0

    // cX = 4
    // cY = 0
    // cZ = 5


    public int chunkCoordToLocal(int num) {
        return (num >= 0) ? (num % REGION_SIZE) : ((REGION_SIZE - 1) - Math.abs(num + 1) % REGION_SIZE);
    }

    public int blockCoordToLocal(int num) {
        return (num >= 0) ? (num % CHUNK_SIZE) : ((CHUNK_SIZE - 1) - Math.abs(num + 1) % CHUNK_SIZE);
    }

    //
    // Block Region functions
    //

    public BlockRegion getRegion(int x, int y, int z) {
        return getRegion(new Vector3i(x, y, z));
    }

    public BlockRegion getRegion(Vector3i pos) {
        return regions.get(pos);
    }

    public boolean regionExists(int x, int y, int z) {
        return regionExists(new Vector3i(x, y, z));
    }

    public boolean regionExists(Vector3i pos) {
        return (regions.get(pos) != null);
    }

    public BlockRegion setRegion(BlockRegion region, int x, int y, int z) {
        return setRegion(region, new Vector3i(x, y, z));
    }

    public BlockRegion setRegion(BlockRegion region, Vector3i pos) {
        regions.put(pos, region);
        return region;
    }

    //
    // Chunk Functions
    //

    public BlockChunk getChunk(int x, int y, int z) {
        return getChunk(new Vector3i(x, y, z));
    }

    public BlockChunk getChunk(Vector3i pos) {
        var regionPos = new Vector3i((int)Math.floor((float)pos.x / REGION_SIZE), (int)Math.floor((float)pos.y / REGION_SIZE), (int)Math.floor((float)pos.z / REGION_SIZE));

        var region = getRegion(regionPos);
        if (region == null) return null;

        var chunkPos  = new Vector3i(chunkCoordToLocal(pos.x), chunkCoordToLocal(pos.y), chunkCoordToLocal(pos.z));
        return region.getChunk(chunkPos);
    }

    public BlockChunk setChunk(BlockChunk chunk, int x, int y, int z) {
        return setChunk(chunk, new Vector3i(x, y, z));
    }

    public BlockChunk setChunk(BlockChunk chunk, Vector3i pos) {
        var regionPos = new Vector3i((int)Math.floor((float)pos.x / REGION_SIZE), (int)Math.floor((float)pos.y / REGION_SIZE), (int)Math.floor((float)pos.z / REGION_SIZE));

        var region = getRegion(regionPos);
        if (region == null) region = setRegion(new BlockRegion(), regionPos);

        var chunkPos  = new Vector3i(chunkCoordToLocal(pos.x), chunkCoordToLocal(pos.y), chunkCoordToLocal(pos.z));
        region.setChunk(chunk, chunkPos);
        return chunk;
    }

    //
    // Block Functions
    //

    public int getBlock(int x, int y, int z) {
        return getBlock(new Vector3i(x, y, z));
    }

    public int getBlock(Vector3i pos) {
        var regionPos = new Vector3i(
                (int)Math.floor((float)pos.x / REGION_SIZE / CHUNK_SIZE),
                (int)Math.floor((float)pos.y / REGION_SIZE / CHUNK_SIZE),
                (int)Math.floor((float)pos.z / REGION_SIZE / CHUNK_SIZE));

        var region = getRegion(regionPos);
        if (region == null) return 0;

        var chunkPos  = new Vector3i(
                chunkCoordToLocal((int)Math.floor((float)pos.x / REGION_SIZE)),
                chunkCoordToLocal((int)Math.floor((float)pos.y / REGION_SIZE)),
                chunkCoordToLocal((int)Math.floor((float)pos.z / REGION_SIZE)));

        var chunk = region.getChunk(chunkPos);
        if (chunk == null) return 0;

        var blockPos = new Vector3i(blockCoordToLocal(pos.x), blockCoordToLocal(pos.y), blockCoordToLocal(pos.z));

        return chunk.getBlock(blockPos);
    }

    public int setBlock(int block, int x, int y, int z) {
        return setBlock(block, new Vector3i(x, y, z));
    }

    public int setBlock(int block, Vector3i pos) {
        var regionPos = new Vector3i(
                (int)Math.floor((float)pos.x / REGION_SIZE / CHUNK_SIZE),
                (int)Math.floor((float)pos.y / REGION_SIZE / CHUNK_SIZE),
                (int)Math.floor((float)pos.z / REGION_SIZE / CHUNK_SIZE));

        var region = getRegion(regionPos);
        if (region == null) return 0;

        var chunkPos  = new Vector3i(
                chunkCoordToLocal((int)Math.floor((float)pos.x / REGION_SIZE)),
                chunkCoordToLocal((int)Math.floor((float)pos.y / REGION_SIZE)),
                chunkCoordToLocal((int)Math.floor((float)pos.z / REGION_SIZE)));

        var chunk = region.getChunk(chunkPos);
        if (chunk == null) return 0;

        var blockPos = new Vector3i(blockCoordToLocal(pos.x), blockCoordToLocal(pos.y), blockCoordToLocal(pos.z));

        return chunk.setBlock(block, blockPos);
    }

    class BlockRegion {
        private BlockChunk[] chunks;

        public BlockRegion() {
            chunks = new BlockChunk[(int)Math.pow(REGION_SIZE, 3)];
        }

        public BlockChunk getChunk(Vector3i pos) {
            return getChunk(pos.x, pos.y, pos.z);
        }

        public BlockChunk getChunk(int x, int y, int z) {
            return chunks[x + REGION_SIZE * (y + REGION_SIZE * z)];
        }

        public BlockChunk setChunk(BlockChunk chunk, Vector3i pos) {
            return setChunk(chunk, pos.x, pos.y, pos.z);
        }

        public BlockChunk setChunk(BlockChunk chunk, int x, int y, int z) {
            chunks[x + REGION_SIZE * (y + REGION_SIZE * z)] = chunk;
            return chunk;
        }
    }

    class BlockChunk {
        private int[] blocks;

        public BlockChunk() {
            blocks = new int[(int)Math.pow(CHUNK_SIZE, 3)];
            for (var i = 0; i < blocks.length; i++) blocks[i] = 0;
        }

        public int getBlock(Vector3i pos) {
            return getBlock(pos.x, pos.y, pos.z);
        }

        public int getBlock(int x, int y, int z) {
            return blocks[x + CHUNK_SIZE * (y + CHUNK_SIZE * z)];
        }

        public int setBlock(int block, Vector3i pos) {
            return setBlock(block, pos.x, pos.y, pos.z);
        }

        public int setBlock(int block, int x, int y, int z) {
            blocks[x + CHUNK_SIZE * (y + CHUNK_SIZE * z)] = block;
            return block;
        }
    }
}
