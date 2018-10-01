package server.server;

import server.api.IBlockDecors;
import server.api.IMapHeightmap;
import helpers.ArrayTrans3D;
import org.joml.Vector3i;
import server.baseApi.BaseDecorations;
import server.baseApi.BaseHeightmap;
import server.server.world.Chunk;
import server.server.world.World;

import static helpers.ArrayTrans3D.CHUNK_SIZE;

public class MapGen {
    private long seed;

    private IMapHeightmap heightmap;
    private IBlockDecors blockdecors;

    public MapGen(World world) {
        seed = 0;
        this.heightmap = new BaseHeightmap(seed);
        this.blockdecors = new BaseDecorations(seed, world);
    }

    public Chunk generate(Vector3i chunkPos) {
        Chunk c = new Chunk(new short[4096], true, chunkPos);
        Vector3i globalPos = new Vector3i(chunkPos);

        int[][] heightMap = heightmap.getChunkHeightmap(chunkPos.x, chunkPos.z);

        for (int i = 0; i < 4096; i++) {
            if (c.blocks[i] == 0) {
                Vector3i localPos = ArrayTrans3D.indToVec(i);

                int depth = heightMap[localPos.x][localPos.z] - chunkPos.y * CHUNK_SIZE - localPos.y;

                c.blocks[i] = getBlockFromDepth(depth);

                globalPos.set(chunkPos.x * CHUNK_SIZE + localPos.x, chunkPos.y * CHUNK_SIZE + localPos.y, chunkPos.z * CHUNK_SIZE + localPos.z);

                int decor = blockdecors.getDecorBlock(globalPos.x, globalPos.z, depth);
                if (decor != -1) c.blocks[i] = (short) decor;

                if (depth == -1) blockdecors.genTree(globalPos, c);
            }
        }

        return c;
    }

    private short getBlockFromDepth(int depth) {
        if (depth > 2) return 4;
        if (depth > 0) return 3;
        if (depth == 0) return 2;

        return 1;
    }
}
