package client.game;

import client.game.network.ConnMan;
import org.joml.Vector3i;

public class ChunkLoader {
    private World world;
    private Player player;
    private ConnMan connection;

    private int tick = 0;

    public ChunkLoader(World world, Player player) {
        this.world = world;
        this.player = player;
    }

    public void setConnection(ConnMan connection) {
        this.connection = connection;
    }

    public void update() {
        tick++;

        var floatPos = player.getPosition();
        if (tick % 30 == 0) connection.sendPosition(new Vector3i(Math.round(floatPos.x), Math.round(floatPos.y), Math.round(floatPos.z)));
    }

    public void newChunk(Vector3i pos, byte[] data) {
        System.out.println("Got chunk for " + pos);

//        if (world.hasChunk(pos)) {
//            world.updateChunk(pos, data);
//        }
//        else {
//            world.addChunk(pos, data);
//        }
        world.addChunk(pos, data);
    }
}
