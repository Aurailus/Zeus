package client.game;

import client.engine.GameEngine;
import client.engine.GameLogic;

public class Main {
    public static void main(String[] args) {
        try {
            boolean vSync = true;
            GameLogic gameLogic = new Game();
            GameEngine gameEngine = new GameEngine("client", 1366, 768, vSync, gameLogic);
            gameEngine.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
