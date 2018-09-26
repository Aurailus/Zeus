package server.server.world;

import org.joml.Vector3i;
import server.server.MapGen;

import java.util.HashMap;

public class World {
    HashMap<Vector3i, Chunk> chunks;
    MapGen mapGen;

    public World() {
        chunks = new HashMap<>();
        mapGen = new MapGen(this);
    }

    public Chunk getChunk(Vector3i pos) {
        var chunk = chunks.get(pos);

        if (chunk == null || !chunk.generated) {
            chunk = mapGen.generate(pos, chunk);
            chunks.put(pos, chunk);
        }

        return chunk;
    }

    public Chunk getChunkRaw(Vector3i pos) {
        return chunks.get(pos);
    }
}
