package client.game;

import helpers.ArrayTrans3D;
import helpers.RLE;
import org.joml.Vector3i;
import java.util.ArrayList;

import static helpers.ArrayTrans3D.CHUNK_SIZE;

public class BlockChunk {
    short[] blocks;
    private boolean[] visible;
    ArrayList<short[]> sides;

    public BlockChunk(short[] blocks, ArrayList<short[]> sides) {
        this.blocks = blocks;
        this.sides = sides;
    }

    public BlockChunk(byte[] blocks, ArrayList<byte[]> sidesOpaque) {
        this.blocks = RLE.decodeShorts(blocks);
        this.sides = new ArrayList<>();
        for (var i = 0; i < 6; i++) {
            this.sides.add(RLE.decodeShorts(sidesOpaque.get(i)));
        }
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

    public boolean[] getVisibleArray() {
        if (visible == null) calcVisible();
        return visible;
    }

    private void calcVisible() {
        visible = new boolean[blocks.length];
        Vector3i pos = new Vector3i();

        for (var i = 0; i < CHUNK_SIZE; i++) {
            for (var j = 0; j < CHUNK_SIZE; j++) {
                for (var k = 0; k < CHUNK_SIZE; k++) {

                    pos.set(i, j, k);
                    ArrayTrans3D.set(visible, false, pos);

                    if (Game.definitions.getDef(getBlock(pos)).getVisible()) {
                        var adjacent = getAdjacent(pos);
                        for (short anAdjacent : adjacent) {
                            if (!Game.definitions.getDef(anAdjacent).getCulls()) {
                                ArrayTrans3D.set(visible, true, pos);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public short[] getAdjacent(Vector3i pos) {
        short[] adjacent = new short[6];
        Vector3i checkPos = new Vector3i(pos);

        checkPos.set(pos).add(1, 0, 0);

        adjacent[0] = getBlockIncludeEdges(checkPos);

        checkPos.set(pos).add(-1, 0, 0);

        adjacent[1] = getBlockIncludeEdges(checkPos);

        checkPos.set(pos).add(0, 1, 0);

        adjacent[2] = getBlockIncludeEdges(checkPos);

        checkPos.set(pos).add(0, -1, 0);

        adjacent[3] = getBlockIncludeEdges(checkPos);

        checkPos.set(pos).add(0, 0, 1);

        adjacent[4] = getBlockIncludeEdges(checkPos);

        checkPos.set(pos).add(0, 0, -1);

        adjacent[5] = getBlockIncludeEdges(checkPos);

        return adjacent;
    }

    private short getBlockIncludeEdges(Vector3i pos) {
        if (pos.x >= 16 || pos.x < 0 || pos.y >= 16 || pos.y < 0 || pos.z >= 16 || pos.z < 0) {
            try {
                return getEdgeBlock(pos);
            }
            catch(Exception e) {
                e.printStackTrace();
                return -1;
            }
        }
        return getBlock(pos);
    }

    private short getEdgeBlock(Vector3i pos) throws Exception {
        if (pos.x == 16) return sides.get(0)[pos.y * 16 + pos.z];
        if (pos.x == -1) return sides.get(1)[pos.y * 16 + pos.z];

        if (pos.y == 16) return sides.get(2)[pos.x * 16 + pos.z];
        if (pos.y == -1) return sides.get(3)[pos.x * 16 + pos.z];

        if (pos.z == 16) return sides.get(4)[pos.y * 16 + pos.x];
        if (pos.z == -1) return sides.get(5)[pos.y * 16 + pos.x];

        throw new Exception("BAD VALUE");
    }
}
