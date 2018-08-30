package ZeusClient.game;

import org.joml.Vector3i;

public class BlockChunk {
    private static final int CHUNK_SIZE = MeshManager.CHUNK_SIZE;

    private static final int NOISE_HORIZONTAL_PRECISION = 80;
    private static final int NOISE_VERTICAL_PRECISION = 10;

    BlockManager blockManager;
    private short[] blocks;
    public final Vector3i position;

    public BlockChunk(BlockManager blockManager, int cX, int cY, int cZ) {
        this(blockManager, new Vector3i(cX, cY, cZ));
    }

    public BlockChunk(BlockManager blockManager, Vector3i pos) {
        this.blockManager = blockManager;
        this.position = pos;
        blocks = new short[(int)Math.pow(CHUNK_SIZE, 3)];
        for (var i = 0; i < blocks.length; i++) blocks[i] = 0;
    }

    public BlockChunk load(short[] data) {
        this.blocks = data;
        return this;
    }

    public void generate(long seed) {
//        OpenSimplexNoise noise = new OpenSimplexNoise(seed);
        for (var i = 0; i < CHUNK_SIZE; i++) {
            for (var j = 0; j < CHUNK_SIZE; j++) {
                for (var k = 0; k < CHUNK_SIZE; k++) {
                    int fill = 0;

//                    fill = (int)Math.round(Math.random());

//                    double noiseVal = noise.eval(((float)i + position.x * CHUNK_SIZE) / NOISE_HORIZONTAL_PRECISION,
//                            ((float)k + position.z * CHUNK_SIZE) / NOISE_HORIZONTAL_PRECISION);
//                    noiseVal = 1-((noiseVal-5)*NOISE_VERTICAL_PRECISION + (position.y * CHUNK_SIZE + j));
//                    fill = (int)Math.min(Math.max(Math.round(noiseVal),0),1);

                    fill = 1;

                    setBlock(fill, i, j, k);
                }
            }
        }

        //TODO: Find where this code came from
//        try {
//            new RegFileManip(new Vector3i(0, 0, 0)).encodeChunk(blocks);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public short getBlock(Vector3i pos) {
        return getBlock(pos.x, pos.y, pos.z);
    }

    public short getBlock(int x, int y, int z) {
        return blocks[x + CHUNK_SIZE * (y + CHUNK_SIZE * z)];
    }

    public int setBlock(int block, Vector3i pos) {
        return setBlock(block, pos.x, pos.y, pos.z);
    }

    public int setBlock(int block, int x, int y, int z) {
        blocks[x + CHUNK_SIZE * (y + CHUNK_SIZE * z)] = (short)block;
        return block;
    }

    public Vector3i getWorldCoords(int x, int y, int z) {
        return getWorldCoords(new Vector3i(x, y, z));
    }

    public Vector3i getWorldCoords(Vector3i pos) {
        return new Vector3i(position.x * CHUNK_SIZE + pos.x, position.y * CHUNK_SIZE + pos.y, position.z * CHUNK_SIZE + pos.z);
    }

    public short[] getBlocks() {
        return blocks;
    }

    public void setBlocks(short[] blocks) {
        this.blocks = blocks;
    }

    public Vector3i getPosition() {
        return position;
    }
}