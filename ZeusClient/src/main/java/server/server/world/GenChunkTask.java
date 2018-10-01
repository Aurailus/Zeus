package server.server.world;

import org.joml.Vector3i;

import java.util.HashMap;
import java.util.concurrent.Callable;

public class GenChunkTask implements Callable<HashMap<Vector3i, Chunk>> {
    private Vector3i position;
    private MapGen gen;

    public GenChunkTask(Vector3i position, MapGen gen) {
        this.position = position;
        this.gen = gen;

    }

    @Override
    public HashMap<Vector3i, Chunk> call() throws Exception {

        HashMap<Vector3i, Chunk> chunksMade = new HashMap<>();

        gen.generate(position, chunksMade);

        return chunksMade;
    }
}
