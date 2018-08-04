package Zeus.game;

import org.joml.SimplexNoise;
import org.joml.Vector3i;

public class BlockChunk {
    public static final int CHUNK_SIZE = MeshManager.CHUNK_SIZE;

    private static final int NOISE_HORIZONTAL_PRECISION = 30;
    private static final int NOISE_VERTICAL_PRECISION = 30;
    private static final float NOISE_Y_MOD = 0.1f;

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
                            ((float)j + position.y * CHUNK_SIZE) / NOISE_VERTICAL_PRECISION - 300,
                            ((float)k + position.z * CHUNK_SIZE) / NOISE_HORIZONTAL_PRECISION);
                    if (noiseVal - NOISE_Y_MOD * (j + (position.y-1) * CHUNK_SIZE) > 0) fill = 1;

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