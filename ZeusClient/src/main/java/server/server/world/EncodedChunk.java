package server.server.world;

import helpers.RLE;

public class EncodedChunk {
    private byte[] blocks;
    private boolean generated = false;

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
        return new Chunk(blocks, generated);
    }

    public void encode(Chunk chunk) {
        this.blocks = RLE.encode(chunk.blocks);
        this.generated = chunk.generated;
    }
}