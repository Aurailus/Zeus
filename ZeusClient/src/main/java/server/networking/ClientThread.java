package server.networking;

import helpers.ChunkSerializer;
import helpers.PacketData;
import helpers.PacketType;
import helpers.VecUtils;
import org.joml.Vector3i;
import server.server.world.Chunk;
import server.server.world.World;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ClientThread extends Thread implements Runnable {
    private Socket socket;
    private Pacman pacman;
    private boolean alive;
    private World world;

    private Vector3i position;

    @SuppressWarnings("FieldCanBeLocal")
    private final int RANGE = 12;

    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    private void init() {
        pacman = new Pacman(socket);
        world = new World();

        position = null;

        pacman.start();
        alive = true;

        updatePlayer(position, new Vector3i(0,0,0));
    }

    private void update() {
        pacman.getPackets((PacketData in) -> {
            switch (in.type) {
                case DEBUG:
                    System.out.println(new String(in.data, StandardCharsets.ISO_8859_1));
                    break;
                case PLAYER_POSITION:
                    playerPositionPacket(in);
                    break;
                default:
                    System.out.println("Recieved packet of type " + in.type + " and we're not prepared to deal with it!");
            }
        });

        try {
            world.update();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

//        pacman.sendPacket(PacketType.DEBUG, "server to client: Hi! Time is " + System.currentTimeMillis());
    }

    private void playerPositionPacket(PacketData in) {
        Vector3i pos = VecUtils.stringToVector(new String(in.data, StandardCharsets.ISO_8859_1));
        if (pos == null) return;

        Vector3i chunkPos = new Vector3i(Math.round((float)pos.x / 16), Math.round((float)pos.y / 16), Math.round((float)pos.z / 16));

        if (chunkPos != position) {
            updatePlayer(position, chunkPos);
            position = chunkPos;
        }
    }

    private void updatePlayer(Vector3i oldPosition, Vector3i newPosition) {
        ArrayList<Vector3i> positions = getChunksInRange(newPosition, RANGE);

        if (oldPosition != null) {
            ArrayList<Vector3i> oldChunks = getChunksInRange(oldPosition, RANGE);
            positions.removeAll(oldChunks);
        }

        if (positions.size() > 0) {
            world.getChunks(positions, this::sendChunkArray);
        }
    }

    private void sendChunkArray(Chunk[] chunks) {
        System.out.println(System.currentTimeMillis() + " | Job done!");

        for (Chunk c : chunks) {
            StringBuilder s = new StringBuilder();
            s.append(VecUtils.vectorToString(c.getPos()));
            s.append("|");

            var bytes = ChunkSerializer.encodeChunk(c.getBlockArray());
            if (bytes == null) return;

            s.append(new String(bytes, StandardCharsets.ISO_8859_1));

            pacman.sendPacket(PacketType.BLOCK_CHUNK, s.toString());
        }
    }

    @SuppressWarnings("SameParameterValue")
    private ArrayList<Vector3i> getChunksInRange(Vector3i chunkPos, int range) {
        ArrayList<Vector3i> chunks = new ArrayList<>();

        for (var i = -range; i < range; i++) {
            for (var j = -range; j < range; j++) {
                for (var k = -range; k < range; k++) {
                    chunks.add(new Vector3i(chunkPos.x + i, chunkPos.y + j, chunkPos.z + k));
                }
            }
        }

        return chunks;
    }

    @Override
    public void run() {
        init();

        while (alive) {
            update();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
