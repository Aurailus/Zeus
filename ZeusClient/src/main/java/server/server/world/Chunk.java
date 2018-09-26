package server.server.world;

import helpers.ArrayTrans3D;
import helpers.RLE;
import org.joml.Vector3i;

public class Chunk {
    short[] blocks;
    boolean generated = false;

    public Chunk(short[] blocks) {
        this.blocks = blocks;
    }

    public Chunk(short[] blocks, boolean generated) {
        this.blocks = blocks;
        this.generated = generated;
    }

    public Chunk(byte[] blocks) {
        this.blocks = RLE.decodeShorts(blocks);
    }

    public Chunk(byte[] blocks, boolean generated) {
        this.blocks = RLE.decodeShorts(blocks);
        this.generated = generated;
    }

    public short getBlock(Vector3i pos) {
        return getBlock(pos.x, pos.y, pos.z);
    }

    public short getBlock(int x, int y, int z) {
        return ArrayTrans3D.get(this.blocks, x, y, z);
    }

    public short[] getBlockArray() {
        return blocks;
    }

    public void setBlockArray(short[] blocks) {
        this.blocks = blocks;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }
}
