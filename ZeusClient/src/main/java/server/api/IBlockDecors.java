package server.api;

import org.joml.Vector3i;
import server.server.world.Chunk;

import java.util.HashMap;

public interface IBlockDecors {
    int getDecorBlock(int x, int z, int depth);
    void genStructs(Vector3i globalPos, Vector3i chunkPos, HashMap<Vector3i, Chunk> chunks, int depth);
}
