package Zeus.game;

import org.joml.SimplexNoise;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class BlockManager {
    static final int REGION_SIZE = 16;
    static final int CHUNK_SIZE = 16;
    ZeusGame game;

    private final Map<Vector3i, BlockRegion> regions;

    public BlockManager(ZeusGame game) {
        this.game = game;
        regions = new HashMap<>();
    }

    public void createRegion(Vector3i pos) {
        if (regionExists(pos)) {
            System.out.println("Region already exists at " + pos + "!");
            return;
        }

        var region = new BlockRegion(this, pos);
        setRegion(region, pos);
        region.populate();
    }

    //
    // Helper Functions
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


    static private int chunkCoordToLocal(int num) {
        return (num >= 0) ? (num % REGION_SIZE) : ((REGION_SIZE - 1) - Math.abs(num + 1) % REGION_SIZE);
    }

    static private int blockCoordToLocal(int num) {
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
        if (region == null) region = setRegion(new BlockRegion(this, regionPos), regionPos);

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
        public final Vector3i position;
        private BlockManager blockManager;

        public BlockRegion(BlockManager blockManager, int x, int y, int z) {
            this(blockManager, new Vector3i(x, y, z));
        }

        public BlockRegion(BlockManager blockManager, Vector3i pos) {
            this.position = pos;
            this.blockManager = blockManager;
            chunks = new BlockChunk[(int)Math.pow(REGION_SIZE, 3)];
        }

        public void populate() {
            for (var i = 0; i < REGION_SIZE; i++) {
                for (var j = 0; j < REGION_SIZE; j++) {
                    for (var k = 0; k < REGION_SIZE; k++) {
                        var chunk = new BlockChunk(blockManager, position, i, j, k);
                        chunk.generate();
                        setChunk(chunk, i, j, k);
                    }
                }
            }
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
}
