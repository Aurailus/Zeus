package server.server;

import server.api.IBlockDecors;
import server.api.IMapHeightmap;
import helpers.ArrayTrans3D;
import helpers.OpenSimplexNoise;
import org.joml.Vector3i;
import server.baseApi.BaseHeightmap;
import server.server.world.Chunk;
import server.server.world.World;

import static helpers.ArrayTrans3D.CHUNK_SIZE;

public class MapGen {
    private long seed;

    private IMapHeightmap heightmap;
//    private IBlockDecors blockdecors;

    private OpenSimplexNoise grassNoise;
    private OpenSimplexNoise treeNoise;

    public MapGen(World world) {
        seed = 0;
        this.heightmap = new BaseHeightmap(0);
//        this.blockdecors = blockdecors;

        grassNoise = new OpenSimplexNoise(seed);
        treeNoise = new OpenSimplexNoise(seed/2);
    }

    public Chunk generate(Vector3i pos, Chunk ungenerated) {
        short[] blocks = (ungenerated != null) ? ungenerated.getBlockArray() : new short[4096];

        int[][] heightMap = heightmap.getChunkHeightmap(pos.x, pos.z);

        for (int i = 0; i < 4096; i++) {
            if (blocks[i] == 0) {

                Vector3i iPos = ArrayTrans3D.indToVec(i);

                int depth = heightMap[iPos.x][iPos.z] - pos.y * CHUNK_SIZE - iPos.y;

                blocks[i] = getBlockFromDepth(depth);

//                if (depth < 0) {
//                    chunk[i] = getBlockDecoration(depth, pos.x * CHUNK_SIZE + iPos.x, pos.z * CHUNK_SIZE + iPos.z);
//                }
//
//                if (treeAtPosition(pos.x * CHUNK_SIZE + iPos.x, pos.z * CHUNK_SIZE + iPos.z)) {
//                    generateTree(chunk, iPos.x, iPos.y, iPos.z);
//                }
            }
        }

        if (ungenerated == null) ungenerated = new Chunk(blocks, true);
        else {
            ungenerated.setBlockArray(blocks);
            ungenerated.setGenerated(true);
        }

        return ungenerated;
    }

//    public short[] generateChunk(Vector3i pos) {
//        var chunk = new short[4096];
//
//        int[][] heightMap = heightmap.getChunkHeightmap(pos.x, pos.z);
//
//        for (int i = 0; i < 4096; i++) {
//            Vector3i iPos = ArrayTrans3D.indToVec(i);
//            int depth = heightMap[iPos.x][iPos.z] - pos.y * CHUNK_SIZE - iPos.y;
//
//            chunk[i] = getBlockFromDepth(depth);
//
//            if (depth < 0) {
//                chunk[i] = getBlockDecoration(depth, pos.x * CHUNK_SIZE + iPos.x, pos.z * CHUNK_SIZE + iPos.z);
//            }
//
//            if (treeAtPosition(pos.x * CHUNK_SIZE + iPos.x, pos.z * CHUNK_SIZE + iPos.z)) {
//                generateTree(chunk, iPos.x, iPos.y, iPos.z);
//            }
//        }
//
//        return chunk;
//    }
//
//    private void tryPlaceBlock(short[] blocks, short block, int x, int y, int z) {
//        if (x < 0 || x > 15 || y < 0 || y > 15 || z < 0 || z > 15) return;
//
//        ArrayTrans3D.set(blocks, block, x, y, z);
//    }
//
//    private void generateTree(short[] blocks, int x, int y, int z) {
//        for (var i = 0; i < 10; i++) {
//            tryPlaceBlock(blocks, (short)11, x, y + i, z);
//        }
//    }
//
//    private boolean treeAtPosition(int x, int z) {
//        float TREE_PRECISION = 0.5f;
//        return treeNoise.eval(x / TREE_PRECISION, z / TREE_PRECISION) > 0.75f;
//    }
//
//    private short getBlockDecoration(int depth, int x, int z) {
//        float GRASS_PRECISION = 10f;
//
//        if (depth == -1) {
//            int grass = (int)Math.round(Math.min(4, Math.max(-1, grassNoise.eval(x / GRASS_PRECISION, z / GRASS_PRECISION) * 5 + Math.random() * 2)));
//            if (grass >= 0) {
//                return (short)(4 + grass);
//            }
//        }
//        return 0;
//    }
//
    private short getBlockFromDepth(int depth) {
        if (depth > 2) return 4;
        if (depth > 0) return 3;
        if (depth == 0) return 2;

        return 1;
    }
}
