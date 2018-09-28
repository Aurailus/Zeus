package client.game;

import helpers.ArrayTrans3D;
import helpers.RLE;
import org.joml.Vector3i;
import java.util.ArrayList;

import static helpers.ArrayTrans3D.CHUNK_SIZE;

public class BlockChunk {
    short[] blocks;
    private boolean visibleDirty;
    private boolean[] visible;
    ArrayList<boolean[]> sidesOpaque;

    public BlockChunk(short[] blocks, ArrayList<boolean[]> sidesOpaque) {
        this.blocks = blocks;
        this.sidesOpaque = sidesOpaque;
        this.visibleDirty = true;
    }

    public BlockChunk(short[] blocks) {
        this.blocks = blocks;
        this.sidesOpaque = new ArrayList<>();
        this.visibleDirty = true;

        //Add dummy opaque values
        boolean[] opaque = new boolean[256];
        for (var i = 0; i < opaque.length; i++) opaque[i] = true;
        for (var i = 0; i < 6; i++) this.sidesOpaque.add(opaque);
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
        if (visibleDirty) calcVisible();
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
                        var adjacent = getAdjacentOpaque(pos);
                        for (boolean opaque : adjacent) {
                            if (!opaque) {
                                ArrayTrans3D.set(visible, true, pos);
                                break;
                            }
                        }
                    }
                }
            }
        }

        visibleDirty = false;
    }

    public boolean[] getAdjacentOpaque(Vector3i pos) {
        boolean[] adjacent = new boolean[6];
        Vector3i checkPos = new Vector3i(pos);

        checkPos.set(pos).add(1, 0, 0);

        adjacent[0] = getOpaqueIncludeEdges(checkPos);

        checkPos.set(pos).add(-1, 0, 0);

        adjacent[1] = getOpaqueIncludeEdges(checkPos);

        checkPos.set(pos).add(0, 1, 0);

        adjacent[2] = getOpaqueIncludeEdges(checkPos);

        checkPos.set(pos).add(0, -1, 0);

        adjacent[3] = getOpaqueIncludeEdges(checkPos);

        checkPos.set(pos).add(0, 0, 1);

        adjacent[4] = getOpaqueIncludeEdges(checkPos);

        checkPos.set(pos).add(0, 0, -1);

        adjacent[5] = getOpaqueIncludeEdges(checkPos);

        return adjacent;
    }

    private boolean getOpaqueIncludeEdges(Vector3i pos) {
        if (pos.x >= 16 || pos.x < 0 || pos.y >= 16 || pos.y < 0 || pos.z >= 16 || pos.z < 0) {
            try {
                return getEdgeOpaque(pos);
            }
            catch(Exception e) {
                e.printStackTrace();
                return true;
            }
        }
        return Game.definitions.getDef(getBlock(pos)).getCulls();
    }

    private boolean getEdgeOpaque(Vector3i pos) throws Exception {
        if (pos.x == 16) return sidesOpaque.get(0)[pos.y * 16 + pos.z];
        if (pos.x == -1) return sidesOpaque.get(1)[pos.y * 16 + pos.z];

        if (pos.y == 16) return sidesOpaque.get(2)[pos.x * 16 + pos.z];
        if (pos.y == -1) return sidesOpaque.get(3)[pos.x * 16 + pos.z];

        if (pos.z == 16) return sidesOpaque.get(4)[pos.y * 16 + pos.x];
        if (pos.z == -1) return sidesOpaque.get(5)[pos.y * 16 + pos.x];

        throw new Exception("BAD VALUE");
    }

    public void setSideOpaque(int ind, boolean[] opaque) {
        sidesOpaque.set(ind, opaque);
        visibleDirty = true;
    }

    public void setSideOpaque(int ind, short[] blocks) {
        var opaque = new boolean[256];
        for (var i = 0; i < blocks.length; i++) {
            opaque[i] = Game.definitions.getDef(blocks[i]).getCulls();
        }

        setSideOpaque(ind, opaque);
    }
}