package server.api;

import org.joml.Vector3i;
import server.server.world.Chunk;

public interface IBlockDecors {
    int getDecorBlock(int x, int z, int depth);

    void genTree(Vector3i pos, Chunk chunk);
}
