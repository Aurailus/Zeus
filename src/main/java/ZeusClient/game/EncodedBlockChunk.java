package ZeusClient.game;

import ZeusClient.engine.helpers.RLE;

import java.util.ArrayList;

public class EncodedBlockChunk {
    private byte[] blocks;
    private ArrayList<byte[]> sidesOpaque;

    public EncodedBlockChunk(BlockChunk blockChunk) {
        this.blocks = RLE.encode(blockChunk.blocks);
        this.sidesOpaque = new ArrayList<>();
        this.sidesOpaque.add(RLE.encode(blockChunk.sidesOpaque.get(0)));
        this.sidesOpaque.add(RLE.encode(blockChunk.sidesOpaque.get(1)));
        this.sidesOpaque.add(RLE.encode(blockChunk.sidesOpaque.get(2)));
        this.sidesOpaque.add(RLE.encode(blockChunk.sidesOpaque.get(3)));
        this.sidesOpaque.add(RLE.encode(blockChunk.sidesOpaque.get(4)));
        this.sidesOpaque.add(RLE.encode(blockChunk.sidesOpaque.get(5)));
    }

    public EncodedBlockChunk(byte[] blocks, ArrayList<byte[]> sidesOpaque) {
        this.blocks = blocks;
        this.sidesOpaque = sidesOpaque;
    }

    public EncodedBlockChunk(short[] blocks,
        boolean[] xPosOpaque, boolean[] xNegOpaque,
        boolean[] yPosOpaque, boolean[] yNegOpaque,
        boolean[] zPosOpaque, boolean[] zNegOpaque) {

        this.blocks = RLE.encode(blocks);
        this.sidesOpaque = new ArrayList<>();
        this.sidesOpaque.add(RLE.encode(xPosOpaque));
        this.sidesOpaque.add(RLE.encode(xNegOpaque));
        this.sidesOpaque.add(RLE.encode(yPosOpaque));
        this.sidesOpaque.add(RLE.encode(yNegOpaque));
        this.sidesOpaque.add(RLE.encode(zPosOpaque));
        this.sidesOpaque.add(RLE.encode(zNegOpaque));
    }

    public BlockChunk decode() {
        return new BlockChunk(blocks, sidesOpaque);
    }

    public void encode(BlockChunk blockChunk) {
        this.blocks = RLE.encode(blockChunk.blocks);
        this.sidesOpaque.set(0, RLE.encode(blockChunk.sidesOpaque.get(0)));
        this.sidesOpaque.set(1, RLE.encode(blockChunk.sidesOpaque.get(1)));
        this.sidesOpaque.set(2, RLE.encode(blockChunk.sidesOpaque.get(2)));
        this.sidesOpaque.set(3, RLE.encode(blockChunk.sidesOpaque.get(3)));
        this.sidesOpaque.set(4, RLE.encode(blockChunk.sidesOpaque.get(4)));
        this.sidesOpaque.set(6, RLE.encode(blockChunk.sidesOpaque.get(5)));
    }
}