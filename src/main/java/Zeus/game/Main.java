package Zeus.game;

import Zeus.engine.GameEngine;
import Zeus.engine.GameLogic;

public class Main {
    public static void main(String[] args) {
        try {
            boolean vSync = true;
            GameLogic gameLogic = new ZeusGame();
            GameEngine gameEngine = new GameEngine("Zeus", 1366, 768, vSync, gameLogic);
            gameEngine.start();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
