package server.baseApi;

import helpers.ArrayTrans3D;
import helpers.OpenSimplexNoise;
import org.joml.Vector3i;
import server.api.IBlockDecors;
import server.server.world.Chunk;
import server.server.world.World;

import java.util.HashMap;

import static helpers.ArrayTrans3D.CHUNK_SIZE;

public class BaseDecorations implements IBlockDecors {
//    private long seed;
//    private World world;

    private static final float GRASS_PRECISION = 10f;
    private static final float TREE_PRECISION = 0.5f;

    private OpenSimplexNoise decorNoise;
    private OpenSimplexNoise treeNoise;

    public BaseDecorations(long seed, World world) {
//        this.seed = seed;
//        this.world = world;

        decorNoise = new OpenSimplexNoise(seed/2);
        treeNoise = new OpenSimplexNoise(seed/2);
    }

    @Override
    public int getDecorBlock(int x, int z, int depth) {
        if (depth != -1) return -1;

        int grass = (int)Math.round(Math.min(4, Math.max(-1, decorNoise.eval(x / GRASS_PRECISION, z / GRASS_PRECISION) * 5 + Math.random() * 2)));
        if (grass >= 0) {
            return (short)(5 + grass);
        }

        return 1;
    }

    @Override
    public void genStructs(Vector3i globalPos, Vector3i chunkPos, HashMap<Vector3i, Chunk> chunks, int depth) {
        if (depth == -1) genTree(globalPos, chunks);
    }

    private void genTree(Vector3i pos, HashMap<Vector3i, Chunk> chunks) {
        if (treeAtPosition(pos.x, pos.z)) {

            for (var i = 0; i < 10; i++) {
                placeBlock(new Vector3i(pos.x, pos.y + i, pos.z), chunks, 12);
            }
            placeBlock(new Vector3i(pos.x + 1, pos.y, pos.z), chunks, 12);
            placeBlock(new Vector3i(pos.x - 1, pos.y, pos.z), chunks, 12);
            placeBlock(new Vector3i(pos.x, pos.y, pos.z + 1), chunks, 12);
            placeBlock(new Vector3i(pos.x, pos.y, pos.z - 1), chunks, 12);
            placeBlock(new Vector3i(pos.x - 1, pos.y + 1, pos.z), chunks, 12);
            placeBlock(new Vector3i(pos.x, pos.y + 1, pos.z + 1), chunks, 12);

            for (var i = -3; i < 3; i++) {
                for (var j = -3; j < 3; j++) {
                    placeBlock(new Vector3i(pos.x + i, pos.y + 10, pos.z + j), chunks, 13);
                    placeBlock(new Vector3i(pos.x + i, pos.y + 12, pos.z + j), chunks, 13);
                }
            }
            for (var i = -4; i < 4; i++) {
                for (var j = -4; j < 4; j++) {
                    placeBlock(new Vector3i(pos.x + i, pos.y + 11, pos.z + j), chunks, 13);
                }
            }

        }
    }

    private void placeBlock(Vector3i pos, HashMap<Vector3i, Chunk> chunks, int block) {
        placeBlock(pos, chunks, (short)block); //Just to make things easier
    }

    private void placeBlock(Vector3i pos, HashMap<Vector3i, Chunk> chunks, short block) {
        Vector3i chunkPos = globalPosToChunk(pos);
        Chunk chunk = chunks.get(chunkPos);
        if (chunk == null) {
            chunk = new Chunk(new short[4096], false, chunkPos);
            chunks.put(chunkPos, chunk);
        }

        ArrayTrans3D.set(chunk.blocks, block, globalPosToLocal(pos));
    }

    private boolean treeAtPosition(int x, int z) {
        return treeNoise.eval(x / TREE_PRECISION, z / TREE_PRECISION) > 0.75f;
    }

    private Vector3i globalPosToChunk(Vector3i pos) {
        return new Vector3i((int)Math.floor((float)pos.x / CHUNK_SIZE), (int)Math.floor((float)pos.y / CHUNK_SIZE), (int)Math.floor((float)pos.z / CHUNK_SIZE));
    }

    private Vector3i globalPosToLocal(Vector3i pos) {
        Vector3i returnPos = new Vector3i();
        returnPos.x = (pos.x >= 0) ? (pos.x % CHUNK_SIZE) : ((CHUNK_SIZE - 1) - Math.abs(pos.x + 1) % CHUNK_SIZE);
        returnPos.y = (pos.y >= 0) ? (pos.y % CHUNK_SIZE) : ((CHUNK_SIZE - 1) - Math.abs(pos.y + 1) % CHUNK_SIZE);
        returnPos.z = (pos.z >= 0) ? (pos.z % CHUNK_SIZE) : ((CHUNK_SIZE - 1) - Math.abs(pos.z + 1) % CHUNK_SIZE);
        return returnPos;
    }
}
