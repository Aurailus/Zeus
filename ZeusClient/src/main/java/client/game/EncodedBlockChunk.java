package client.game;

import helpers.RLE;

import java.util.ArrayList;

public class EncodedBlockChunk {
    private byte[] blocks;
    private ArrayList<byte[]> sides;

    public EncodedBlockChunk(BlockChunk blockChunk) {
        this.blocks = RLE.encode(blockChunk.blocks);
        this.sides = new ArrayList<>();
        this.sides.add(RLE.encode(blockChunk.sides.get(0)));
        this.sides.add(RLE.encode(blockChunk.sides.get(1)));
        this.sides.add(RLE.encode(blockChunk.sides.get(2)));
        this.sides.add(RLE.encode(blockChunk.sides.get(3)));
        this.sides.add(RLE.encode(blockChunk.sides.get(4)));
        this.sides.add(RLE.encode(blockChunk.sides.get(5)));
    }

    public EncodedBlockChunk(byte[] blocks, ArrayList<byte[]> sides) {
        this.blocks = blocks;
        this.sides = sides;
    }

    public EncodedBlockChunk(short[] blocks,
        boolean[] xPosOpaque, boolean[] xNegOpaque,
        boolean[] yPosOpaque, boolean[] yNegOpaque,
        boolean[] zPosOpaque, boolean[] zNegOpaque) {

        this.blocks = RLE.encode(blocks);
        this.sides = new ArrayList<>();
        this.sides.add(RLE.encode(xPosOpaque));
        this.sides.add(RLE.encode(xNegOpaque));
        this.sides.add(RLE.encode(yPosOpaque));
        this.sides.add(RLE.encode(yNegOpaque));
        this.sides.add(RLE.encode(zPosOpaque));
        this.sides.add(RLE.encode(zNegOpaque));
    }

    public BlockChunk decode() {
        return new BlockChunk(blocks, sides);
    }

    public void encode(BlockChunk blockChunk) {
        this.blocks = RLE.encode(blockChunk.blocks);
        this.sides.set(0, RLE.encode(blockChunk.sides.get(0)));
        this.sides.set(1, RLE.encode(blockChunk.sides.get(1)));
        this.sides.set(2, RLE.encode(blockChunk.sides.get(2)));
        this.sides.set(3, RLE.encode(blockChunk.sides.get(3)));
        this.sides.set(4, RLE.encode(blockChunk.sides.get(4)));
        this.sides.set(6, RLE.encode(blockChunk.sides.get(5)));
    }
}