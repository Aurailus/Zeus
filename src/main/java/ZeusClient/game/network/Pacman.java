package ZeusClient.game.network;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Pacman extends Thread implements Runnable {
    private final int MAX_OUT_SIZE = 4096;
    private final int OUT_INTERVAL = 32;

    private final byte[] LINE_DELIM = "\n".getBytes();

    private Socket socket;
    private boolean closed = false;
    private Thread thread; //Background Packet Resolver

    private BufferedReader in;
    private BufferedOutputStream out;

    private long updateInterval = 32;
    private long lastUpdate;

    private ArrayList<byte[]> pendingOutPackets;
    private BlockingQueue<String> pendingInPackets;

    private ArrayList<PacketData> clientPackets;

    public Pacman(Socket socket) {
        this.socket = socket;
        lastUpdate = System.currentTimeMillis();

        pendingOutPackets = new ArrayList<>();
        pendingInPackets = new LinkedBlockingQueue<>();
        clientPackets = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedOutputStream(socket.getOutputStream());

            thread = new Thread(() -> {
                try {
                    while (!closed) {
                        var line = in.readLine();
                        pendingInPackets.put(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    closed = true;
                }
            }, "BackgroundPacketResolver");
            thread.setDaemon(true);
            thread.start();

            loop();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loop() throws Exception {
        while (!closed) {
            update();
            Thread.sleep(OUT_INTERVAL);
        }

        socket.close();
    }

    private void update() throws Exception {
        synchronized (this) {
            while (decodePacket()) {}
        }
        sendPendingOutPackets();
    }

    public synchronized void getPackets(Consumer<PacketData> consumer) {
        for (PacketData packet : clientPackets) {
            consumer.accept(packet);
        }
        clientPackets.clear();
    }

    private boolean decodePacket() throws InterruptedException {
        String packet = (closed || pendingInPackets.isEmpty()) ? null : pendingInPackets.poll(1L, TimeUnit.MILLISECONDS);
        if (packet == null) return false;

        PacketData p = new PacketData();

        byte[] bytes = packet.getBytes();
        p.type = PacketType.values()[Bytes.bytesToInt(Arrays.copyOfRange(bytes, 0, 4))];
        p.time = Bytes.bytesToLong(Arrays.copyOfRange(bytes, 4, 12));
        p.data = new String(Arrays.copyOfRange(bytes, 12, bytes.length));

        clientPackets.add(p);

        return true;
    }

    private void sendPendingOutPackets() throws IOException {
        int size = 0;
        byte[] packet;

        while (pendingOutPackets.size() > 0 && (packet = pendingOutPackets.get(0)) != null && (size += packet.length) < MAX_OUT_SIZE) {
            out.write(packet);
            out.write(LINE_DELIM);
            pendingOutPackets.remove(0);
        }
        out.flush();
    }

    public void sendPacket(PacketType p, String data) {
        sendPacket(p, data.getBytes());
    }

    public void sendPacket(PacketType p, byte[] data) {
        byte[] out = new byte[data.length + 12];
        System.arraycopy(Bytes.intToBytes(p.ordinal()), 0, out, 0, 4);
        System.arraycopy(Bytes.longToBytes(0), 0, out, 4, 8);
        System.arraycopy(data, 0, out, 12, data.length);

        pendingOutPackets.add(out);
    }

    public void kill() {
        closed = true;
    }
}
