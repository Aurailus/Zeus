package ZeusClient.game;

import ZeusClient.engine.GameEngine;
import ZeusClient.engine.GameLogic;

public class Main {
    public static void main(String[] args) {
        try {
            boolean vSync = true;
            GameLogic gameLogic = new Game();
            GameEngine gameEngine = new GameEngine("ZeusClient", 1366, 768, vSync, gameLogic);
            gameEngine.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
