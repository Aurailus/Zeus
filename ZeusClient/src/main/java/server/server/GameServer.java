package server.server;

import server.networking.ClientThread;

import java.net.ServerSocket;
import java.net.Socket;

class GameServer {
    private int port;
    private boolean running;

    GameServer(int port) {
        this.port = port;
        running = true;
    }

    void start() throws Exception {
        ServerSocket socket = new ServerSocket(this.port);

        while (running) {
            Socket clientSocket = socket.accept();
            ClientThread t = new ClientThread(clientSocket);
            t.start();
        }
    }

    void end() {
        running = false;
    }
}
