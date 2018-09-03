package ZeusClient.game;

import ZeusClient.engine.GameEngine;
import ZeusClient.engine.GameLogic;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        try {
            boolean vSync = true;
            GameLogic gameLogic = new ZeusGame();
            GameEngine gameEngine = new GameEngine("ZeusClient", 1366, 768, vSync, gameLogic);
            gameEngine.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
