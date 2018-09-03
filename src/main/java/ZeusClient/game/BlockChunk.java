package ZeusClient.game;

import ZeusClient.engine.helpers.ArrayTrans3D;
import ZeusClient.engine.helpers.RLE;
import org.joml.Vector3i;

import java.util.ArrayList;

import static ZeusClient.engine.helpers.ArrayTrans3D.CHUNK_SIZE;

public class BlockChunk {
    short[] blocks;
    boolean[] visible;
    ArrayList<boolean[]> sidesOpaque;

    public BlockChunk(byte[] blocks, ArrayList<byte[]> sidesOpaque) {

        this.blocks = RLE.decodeShorts(blocks);
        this.sidesOpaque = new ArrayList<>();
        for (var i = 0; i < 6; i++) {
            this.sidesOpaque.set(i, RLE.decodeBools(sidesOpaque.get(i)));
        }
        calcVisible();
    }

    public short getBlock(Vector3i pos) {
        return getBlock(pos.x, pos.y, pos.z);
    }

    public short getBlock(int x, int y, int z) {
        return ArrayTrans3D.get(this.blocks, x, y, z);
    }

    public boolean getVisible(Vector3i pos) {
        return getVisible(pos.x, pos.y, pos.z);
    }

    public boolean getVisible(int x, int y, int z) {
        if (visible == null) calcVisible();
        return ArrayTrans3D.get(this.visible, x, y, z);
    }

    private void calcVisible() {
        visible = new boolean[blocks.length];
        Vector3i pos = new Vector3i();

        for (var i = 0; i < CHUNK_SIZE; i++) {
            for (var j = 0; j < CHUNK_SIZE; j++) {
                for (var k = 0; k < CHUNK_SIZE; k++) {

                    pos.set(i, j, k);
                    var block = ArrayTrans3D.get(blocks, pos);
                    var adjacent = getAdjacent(pos);

                    ArrayTrans3D.set(visible, true, pos);
                    for (var l = 0; l < 6; l++) {
                        if (adjacent[i] == 0) {
                            ArrayTrans3D.set(visible, true, pos);
                            break;
                        }
                    }

                }
            }
        }
    }

    private short[] getAdjacent(Vector3i pos) {
        short[] adjacent = new short[6];

        pos.x += 1;
        adjacent[0] = (pos.x < 16) ? ArrayTrans3D.get(blocks, pos) : 1;

        pos.x -= 2;
        adjacent[1] = (pos.x >= 0) ? ArrayTrans3D.get(blocks, pos) : 1;

        pos.x += 1;
        pos.y += 1;
        adjacent[2] = (pos.x < 16) ? ArrayTrans3D.get(blocks, pos) : 1;

        pos.y -= 2;
        adjacent[3] = (pos.x >= 0) ? ArrayTrans3D.get(blocks, pos) : 1;

        pos.y += 1;
        pos.z += 1;
        adjacent[4] = (pos.x < 16) ? ArrayTrans3D.get(blocks, pos) : 1;

        pos.z -= 2;
        adjacent[5] = (pos.x >= 0) ? ArrayTrans3D.get(blocks, pos) : 1;
        pos.z += 1;

        return adjacent;
    }
}
