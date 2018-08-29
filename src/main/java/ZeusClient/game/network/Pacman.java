package ZeusClient.game.network;

import java.io.*;
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

    private Socket socket;
    private boolean closed = false;

    private DataInputStream in;
    private DataOutputStream out;

    private ArrayList<byte[]> pendingOutPackets;
    private BlockingQueue<byte[]> pendingInPackets;

    private ArrayList<PacketData> clientPackets;

    public Pacman(Socket socket) {
        this.socket = socket;

        pendingOutPackets = new ArrayList<>();
        pendingInPackets = new LinkedBlockingQueue<>();
        clientPackets = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            //Background Packet Resolver
            Thread thread = new Thread(() -> {
                try {
                    while (!closed) {
                        int length = in.readInt();
                        byte[] data = new byte[length];
                        in.readFully(data, 0, length);
                        pendingInPackets.put(data);
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
        byte[] packet = (closed || pendingInPackets.isEmpty()) ? null : pendingInPackets.poll(1L, TimeUnit.MILLISECONDS);
        if (packet == null) return false;

        PacketData p = new PacketData();

        p.type = PacketType.values()[Bytes.bytesToInt(Arrays.copyOfRange(packet, 0, 4))];
        p.time = Bytes.bytesToLong(Arrays.copyOfRange(packet, 4, 12));
        p.data = Arrays.copyOfRange(packet, 12, packet.length);

        clientPackets.add(p);

        return true;
    }

    private void sendPendingOutPackets() throws IOException {
        int size = 0;
        byte[] packet;

        synchronized (this) {
            while (pendingOutPackets.size() > 0) {
                packet = pendingOutPackets.get(0);
                size += packet.length;

                out.writeInt(packet.length);
                out.write(packet);

                pendingOutPackets.remove(0);

                if (size >= MAX_OUT_SIZE) break;
            }
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

        synchronized (this) {
            pendingOutPackets.add(out);
        }
    }

    public void kill() {
        closed = true;
    }
}
