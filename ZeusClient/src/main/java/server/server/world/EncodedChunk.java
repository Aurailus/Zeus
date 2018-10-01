package server.server.world;

import helpers.RLE;
import org.joml.Vector3i;

public class EncodedChunk {
    private byte[] blocks;
    private boolean generated = false;
    private Vector3i pos;

    public EncodedChunk(Chunk blockChunk) {
        this.blocks = RLE.encode(blockChunk.blocks);
    }

    public EncodedChunk(byte[] blocks) {
        this.blocks = blocks;
    }

    public EncodedChunk(short[] blocks) {
        this.blocks = RLE.encode(blocks);
    }

    public Chunk decode() {
        return new Chunk(blocks, generated, pos);
    }

    public void encode(Chunk chunk) {
        this.blocks = RLE.encode(chunk.blocks);
        this.generated = chunk.generated;
        this.pos = chunk.getPos();
    }
}