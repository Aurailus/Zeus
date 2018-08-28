package ZeusClient.game.network;

import ZeusClient.engine.ChunkSerializer;
import ZeusClient.engine.VecUtils;
import org.joml.Vector3i;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.function.Consumer;

public class ConnMan {
    private Pacman pacman;
    private HashMap<Vector3i, Consumer<short[]>> pendingChunkCallbacks;

    public ConnMan(String host, int port) throws Exception {
        pendingChunkCallbacks = new HashMap<>();
        pacman = new Pacman(new Socket(host, port));
        pacman.start();
    }

    public void update() {
        pacman.getPackets((PacketData p) -> {
            switch (p.type) {
                case DEBUG:
                    System.out.println(p.data);
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
        String[] segments = p.data.split("\\|");
        if (segments.length != 2) return;
        var pos = VecUtils.stringToVector(segments[0]);
        if (pos == null) return;
        var cons = pendingChunkCallbacks.get(pos);
        if (cons == null) return;
        var chunk = ChunkSerializer.decodeChunk(segments[1].getBytes(StandardCharsets.ISO_8859_1));
        cons.accept(chunk);
    }

    public void requestChunk(Vector3i pos, Consumer<short[]> consumer) {
        pendingChunkCallbacks.put(pos, consumer);
        pacman.sendPacket(PacketType.REQUEST_CHUNK, VecUtils.vectorToString(pos));
    }

    public void requestChunk(int x, int y, int z, Consumer<short[]> consumer) {
        requestChunk(new Vector3i(x, y, z), consumer);
    }

    public void kill() {
        pacman.kill();
        pendingChunkCallbacks.clear();
    }
}
