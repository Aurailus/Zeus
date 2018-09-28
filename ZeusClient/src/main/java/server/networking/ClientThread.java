package server.networking;

import helpers.ChunkSerializer;
import helpers.PacketData;
import helpers.PacketType;
import helpers.VecUtils;
import org.joml.Vector3i;
import server.server.world.World;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ClientThread extends Thread implements Runnable {
    private Socket socket;
    private Pacman pacman;
    private boolean alive;
    private World world;

    private Vector3i position;

    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    private ThreadPoolExecutor mapGenPool;

    private void init() {
        pacman = new Pacman(socket);
        world = new World();

        position = null;

        pacman.start();
        alive = true;

        mapGenPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(32);
        mapGenPool.setMaximumPoolSize(96);
        mapGenPool.setKeepAliveTime(32, TimeUnit.SECONDS);
    }

    private void update() {
        pacman.getPackets((PacketData in) -> {
            switch (in.type) {
                case DEBUG:
                    System.out.println(new String(in.data, StandardCharsets.ISO_8859_1));
                    break;
                case REQUEST_CHUNK:
                    deferredRenderChunk(in);
                    break;
                case PLAYER_POSITION:
                    playerPositionPacket(in);
                    break;
                default:
                    System.out.println("Recieved packet of type " + in.type + " and we're not prepared to deal with it!");
            }
        });
//        pacman.sendPacket(PacketType.DEBUG, "server to client: Hi! Time is " + System.currentTimeMillis());
    }

    private void playerPositionPacket(PacketData in) {
        Vector3i pos = VecUtils.stringToVector(new String(in.data, StandardCharsets.ISO_8859_1));
        Vector3i chunkPos = new Vector3i(Math.round((float)pos.x / 16), Math.round((float)pos.y / 16), Math.round((float)pos.z / 16));

        if (chunkPos != position) {
            updatePlayer(position, chunkPos);
            position = chunkPos;
        }
    }

    private void updatePlayer(Vector3i oldPosition, Vector3i newPosition) {
        ArrayList<Vector3i> chunks = getChunksInRange(newPosition, 8);

        if (oldPosition != null) {
            ArrayList<Vector3i> oldChunks = getChunksInRange(oldPosition, 8);
            chunks.removeAll(oldChunks);
        }

        for (Vector3i c : chunks) {
            requestChunk(c);
        }
    }

    private void requestChunk(Vector3i position) {
        if (position == null) return;

        mapGenPool.submit(() -> {
            StringBuilder s = new StringBuilder();
            s.append(VecUtils.vectorToString(position));
            s.append("|");

            System.out.println("Generating chunk at position " + position);

            var bytes = ChunkSerializer.encodeChunk(world.getChunk(position).getBlockArray());
            if (bytes == null) return;

            s.append(new String(bytes, StandardCharsets.ISO_8859_1));

            pacman.sendPacket(PacketType.BLOCK_CHUNK, s.toString());
        });
    }

    private void deferredRenderChunk(PacketData in) {
        Vector3i position = VecUtils.stringToVector(new String(in.data, StandardCharsets.ISO_8859_1));
        requestChunk(position);
    }

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

//    private ArrayList<short[]> generateSides(Vector3i pos) {
//        ArrayList<short[]> sides = new ArrayList<>();
//        Vector3i surrogate = new Vector3i();
//
//        var empty = new short[256];
//        Chunk chunk;
//
//
//        chunk = world.getChunkRaw(surrogate.set(pos).add(1, 0, 0));
//        if (chunk == null) sides.add(empty);
//        else {
//            var array = new short[256];
//            for (var i = 0; i < 256; i++) {
//                array[i] = chunk.getBlock(0, i / 16, i % 16);
//            }
//            sides.add(array);
//        }
//
//        chunk = world.getChunkRaw(surrogate.set(pos).add(-1, 0, 0));
//        if (chunk == null) sides.add(empty);
//        else {
//            var array = new short[256];
//            for (var i = 0; i < 256; i++) {
//                array[i] = chunk.getBlock(15, i / 16, i % 16);
//            }
//            sides.add(array);
//        }
//
//        chunk = world.getChunkRaw(surrogate.set(pos).add(0, 1, 0));
//        if (chunk == null) sides.add(empty);
//        else {
//            var array = new short[256];
//            for (var i = 0; i < 256; i++) {
//                array[i] = chunk.getBlock(i / 16, 0, i % 16);
//            }
//            sides.add(array);
//        }
//
//        chunk = world.getChunkRaw(surrogate.set(pos).add(0, -1, 0));
//        if (chunk == null) sides.add(empty);
//        else {
//            var array = new short[256];
//            for (var i = 0; i < 256; i++) {
//                array[i] = chunk.getBlock(i / 16, 15, i % 16);
//            }
//            sides.add(array);
//        }
//
//        chunk = world.getChunkRaw(surrogate.set(pos).add(0, 0, 1));
//        if (chunk == null) sides.add(empty);
//        else {
//            var array = new short[256];
//            for (var i = 0; i < 256; i++) {
//                array[i] = chunk.getBlock(i % 16, i / 16, 0);
//            }
//            sides.add(array);
//        }
//
//        chunk = world.getChunkRaw(surrogate.set(pos).add(0, 0, -1));
//        if (chunk == null) sides.add(empty);
//        else {
//            var array = new short[256];
//            for (var i = 0; i < 256; i++) {
//                array[i] = chunk.getBlock(i % 16, i / 16, 15);
//            }
//            sides.add(array);
//        }
//
//        return sides;
//    }

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
