package server.server.world;

import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.function.Consumer;

public class GenJob {
    private ArrayList<Vector3i> pending;
    public ArrayList<Vector3i> positions;
    public Consumer<Chunk[]> callback;

    public GenJob(ArrayList<Vector3i> positions, Consumer<Chunk[]> callback) {
        this.positions = positions;
        this.pending = new ArrayList<>();
        pending.addAll(positions);
        this.callback = callback;
    }

    //Boolean == ready
    public boolean acceptChunk(Vector3i pos) {
        pending.remove(pos);
        return pending.size() == 0;
    }

    public boolean isReady() {
        return pending.size() == 0;
    }
}
