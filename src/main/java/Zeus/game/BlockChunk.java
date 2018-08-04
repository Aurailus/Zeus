package Zeus.game;

import org.joml.SimplexNoise;
import org.joml.Vector3i;

public class BlockChunk {
    public static final int CHUNK_SIZE = MeshManager.CHUNK_SIZE;

    private static final int NOISE_HORIZONTAL_PRECISION = 300;
    private static final int NOISE_VERTICAL_PRECISION = 200;

    private BlockManager blockManager;
    private int[] blocks;
    public final Vector3i position;

    public BlockChunk(BlockManager blockManager, Vector3i regionPos, int cX, int cY, int cZ) {
        this(blockManager, regionPos, new Vector3i(cX, cY, cZ));
    }

    public BlockChunk(BlockManager blockManager, Vector3i regionPos, Vector3i chunkPos) {
        this.blockManager = blockManager;
        this.position = new Vector3i(regionPos.x * 16 + chunkPos.x, regionPos.y * 16 + chunkPos.y, regionPos.z * 16 + chunkPos.z);
        blocks = new int[(int)Math.pow(CHUNK_SIZE, 3)];
        for (var i = 0; i < blocks.length; i++) blocks[i] = 0;
    }

    public void generate() {
        for (var i = 0; i < CHUNK_SIZE; i++) {
            for (var j = 0; j < CHUNK_SIZE; j++) {
                for (var k = 0; k < CHUNK_SIZE; k++) {
                    int fill = 0;

                    double noiseVal = SimplexNoise.noise(((float)i + position.x * CHUNK_SIZE) / NOISE_HORIZONTAL_PRECISION,
                            ((float)k + position.z * CHUNK_SIZE) / NOISE_HORIZONTAL_PRECISION);
                    noiseVal = 1-(noiseVal*NOISE_VERTICAL_PRECISION + (position.y * CHUNK_SIZE + j));
                    fill = (int)Math.min(Math.max(Math.round(noiseVal),0),1);

                    setBlock(fill, i, j, k);
                }
            }
        }
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

    public int getExternalRelativeBlock(int x, int y, int z) {
        return blockManager.getBlock(position.x * CHUNK_SIZE + x, position.y * CHUNK_SIZE + y, position.z * CHUNK_SIZE + z);
    }
}