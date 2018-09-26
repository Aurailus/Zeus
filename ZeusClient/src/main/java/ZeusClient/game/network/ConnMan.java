package ZeusClient.game.network;

import ZeusClient.engine.helpers.ChunkSerializer;
import ZeusClient.engine.helpers.VecUtils;
import ZeusClient.game.BlockChunk;
import org.joml.Vector3i;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.function.BiConsumer;

public class ConnMan {
    private Pacman pacman;
    private HashMap<Vector3i, BiConsumer<Vector3i, byte[]>> pendingChunkCallbacks;

    public ConnMan(String host, int port) throws Exception {
        pendingChunkCallbacks = new HashMap<>();
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
                    System.out.println("Recieved packet of type " + p.type + "and we're not prepared to deal with it!");
            }
        });
    }

    private void handleReceivedChunk(PacketData p) {
        var str = new String(p.data, StandardCharsets.ISO_8859_1);
        var index = str.indexOf("|");
        var pos = VecUtils.stringToVector(str.substring(0, index));
        if (pos == null) return;
        var cons = pendingChunkCallbacks.get(pos);
        if (cons == null) return;

        cons.accept(pos, str.substring(index + 1).getBytes(StandardCharsets.ISO_8859_1));
    }



    public void requestChunk(Vector3i pos, BiConsumer<Vector3i, byte[]> consumer) {
        requestChunk(pos.x, pos.y, pos.z, consumer);
    }

    public void requestChunk(int x, int y, int z, BiConsumer<Vector3i, byte[]> consumer) {
        var pos = new Vector3i(x, y, z);
        if (pendingChunkCallbacks.containsKey(pos)) return;
        pendingChunkCallbacks.put(pos, consumer);
        pacman.sendPacket(PacketType.REQUEST_CHUNK, VecUtils.vectorToString(pos));
    }

    public void kill() {
        pacman.kill();
        pendingChunkCallbacks.clear();
    }
}
