package client.game.network;

import client.game.ChunkLoader;
import helpers.VecUtils;
import org.joml.Vector3i;
import helpers.PacketType;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;

public class ConnMan {
    private Pacman pacman;
    private ChunkLoader worldbridge;

    public ConnMan(String host, int port, ChunkLoader worldbridge) throws Exception {
        this.worldbridge = worldbridge;
        pacman = new Pacman(new Socket(host, port));
        pacman.start();
    }

    public void update() {
        pacman.getPackets((PacketData p) -> {
            switch (p.type) {
                case DEBUG:
                    System.out.println(new String(p.data, StandardCharsets.ISO_8859_1));
                    break;
                case BLOCK_CHUNK:
                    handleReceivedChunk(p);
                    break;
                default:
                    System.out.println("Recieved packet of type " + p.type + " and we're not prepared to deal with it!");
            }
        });
    }

    private void handleReceivedChunk(PacketData p) {

        var str = new String(p.data, StandardCharsets.ISO_8859_1);
        var index = str.indexOf("|");
        var pos = VecUtils.stringToVector(str.substring(0, index));
        if (pos == null) return;

        worldbridge.newChunk(pos, str.substring(index + 1).getBytes(StandardCharsets.ISO_8859_1));
    }

//    public void requestChunk(Vector3i pos, BiConsumer<Vector3i, byte[]> consumer) {
//        requestChunk(pos.x, pos.y, pos.z, consumer);
//    }

//    public void requestChunk(int x, int y, int z, BiConsumer<Vector3i, byte[]> consumer) {
//        var pos = new Vector3i(x, y, z);
//        pacman.sendPacket(PacketType.REQUEST_CHUNK, VecUtils.vectorToString(pos));
//    }

    public void sendPosition(Vector3i pos) {
        pacman.sendPacket(PacketType.PLAYER_POSITION, VecUtils.vectorToString(pos));
    }

    public void kill() {
        pacman.kill();
    }
}
