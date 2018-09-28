package client.game;

import helpers.ArrayTrans3D;
import helpers.RLE;
import org.joml.Vector3i;
import java.util.ArrayList;

import static helpers.ArrayTrans3D.CHUNK_SIZE;

public class BlockChunk {
    short[] blocks;

    public BlockChunk(short[] blocks) {
        this.blocks = blocks;
    }

    public short getBlock(Vector3i pos) {
        return getBlock(pos.x, pos.y, pos.z);
    }

    public short getBlock(int x, int y, int z) {
        return ArrayTrans3D.get(this.blocks, x, y, z);
    }

    public boolean[] calcVisible(BlockChunk[] adjacentChunks) {
        boolean[] visible = new boolean[blocks.length];
        Vector3i pos = new Vector3i();

        for (var i = 0; i < CHUNK_SIZE; i++) {
            for (var j = 0; j < CHUNK_SIZE; j++) {
                for (var k = 0; k < CHUNK_SIZE; k++) {

                    pos.set(i, j, k);
                    ArrayTrans3D.set(visible, false, pos);

                    if (Game.definitions.getDef(getBlock(pos)).getVisible()) {
                        var adjacent = getAdjacentOpaque(pos, adjacentChunks);
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

        return visible;
    }

    public boolean[] getAdjacentOpaque(Vector3i pos, BlockChunk[] adjacentChunks) {
        boolean[] adjacent = new boolean[6];

        Vector3i checkPos = new Vector3i(pos).add(1, 0, 0);

        adjacent[0] = getOpaqueIncludeEdges(checkPos, adjacentChunks);

        checkPos = new Vector3i(pos).add(-1, 0, 0);

        adjacent[1] = getOpaqueIncludeEdges(checkPos, adjacentChunks);

        checkPos = new Vector3i(pos).add(0, 1, 0);

        adjacent[2] = getOpaqueIncludeEdges(checkPos, adjacentChunks);

        checkPos = new Vector3i(pos).add(0, -1, 0);

        adjacent[3] = getOpaqueIncludeEdges(checkPos, adjacentChunks);

        checkPos = new Vector3i(pos).add(0, 0, 1);

        adjacent[4] = getOpaqueIncludeEdges(checkPos, adjacentChunks);

        checkPos = new Vector3i(pos).add(0, 0, -1);

        adjacent[5] = getOpaqueIncludeEdges(checkPos, adjacentChunks);

        return adjacent;
    }

    private boolean getOpaqueIncludeEdges(Vector3i pos, BlockChunk[] adjacentChunks) {
        if (pos.x >= 16 || pos.x < 0 || pos.y >= 16 || pos.y < 0 || pos.z >= 16 || pos.z < 0) {
            try {
                return Game.definitions.getDef(getEdgeBlock(pos, adjacentChunks)).getCulls();
            }
            catch(Exception e) {
                e.printStackTrace();
                return true;
            }
        }
        return Game.definitions.getDef(getBlock(pos)).getCulls();
    }

    private short getEdgeBlock(Vector3i pos, BlockChunk[] adjacentChunks) throws Exception {
        if (pos.x == 16) {
            if (adjacentChunks[0] == null) return 0;
            return adjacentChunks[0].getBlock(0, pos.y, pos.z);
        }
        if (pos.x == -1) {
            if (adjacentChunks[1] == null) return 0;
            return adjacentChunks[1].getBlock(15, pos.y, pos.z);
        }

        if (pos.y == 16) {
            if (adjacentChunks[2] == null) return 0;
            return adjacentChunks[2].getBlock(pos.x, 0, pos.z);
        }
        if (pos.y == -1) {
            if (adjacentChunks[3] == null) return 0;
            return adjacentChunks[3].getBlock(pos.x, 15, pos.z);
        }

        if (pos.z == 16) {
            if (adjacentChunks[4] == null) return 0;
            return adjacentChunks[4].getBlock(pos.x, pos.y, 0);
        }
        if (pos.z == -1) {
            if (adjacentChunks[5] == null) return 0;
            return adjacentChunks[5].getBlock(pos.x, pos.y, 15);
        }

        throw new Exception("BAD VALUE");
    }
}