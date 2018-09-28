package client.game;

import helpers.RLE;

public class EncodedBlockChunk {
    private byte[] blocks;

    public EncodedBlockChunk(BlockChunk blockChunk) {
        this.blocks = RLE.encode(blockChunk.blocks);
    }

    public EncodedBlockChunk(byte[] blocks) {
        this.blocks = blocks;
    }

    public EncodedBlockChunk(short[] blocks) {

        this.blocks = RLE.encode(blocks);
    }

    public BlockChunk decode() {
        return new BlockChunk(RLE.decodeShorts(blocks));
    }

    public void encode(BlockChunk blockChunk) {
        this.blocks = RLE.encode(blockChunk.blocks);
    }
}