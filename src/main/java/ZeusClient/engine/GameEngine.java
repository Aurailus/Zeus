package ZeusClient.engine;

import ZeusClient.engine.helpers.Timer;

public class GameEngine implements Runnable {
    public static final int TARGET_FPS = 60;
    public static final int TARGET_UPS = 60;

    private final Window window;
    private final Thread gameLoopThread;
    private final Timer timer;
    private final GameLogic gameInterface;
    private final MouseInput mouseInput;

    private int ticksTillWindowUpdate;
    private double elapsedFrames = 0;

    public GameEngine(String windowTitle, boolean vSync, GameLogic gameInterface) throws Exception {
        this(windowTitle, 0, 0, vSync, gameInterface); //0 for width and height will maximize windows
    }

    public GameEngine(String windowTitle, int width, int height, boolean vSync, GameLogic gameInterface) throws Exception {
        gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
        window = new Window(windowTitle, width, height, vSync);
        mouseInput = new MouseInput();
        this.gameInterface = gameInterface;
        timer = new Timer();
        ticksTillWindowUpdate = 0;
        elapsedFrames = 1;
    }

    public void start() {
        String osName = System.getProperty("os.name");
        if (osName.contains("Mac")) {
            //Mac doesn't like Multithreading in GLFW, so dont do it
            gameLoopThread.run();
        }
        else {
            gameLoopThread.start();
        }
    }

    @Override
    public void run() {
        try {
            init();
            loop();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            cleanup();
        }
    }

    protected void init() throws Exception {
        window.init();
        timer.init();
        mouseInput.init(window);
        gameInterface.init(window);
    }

    protected void loop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f/TARGET_UPS;

        boolean running = true;
        while (running && !window.windowShouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            if (ticksTillWindowUpdate == 0) {
                window.updateTitle("ZeusClient - " + 1/(elapsedFrames/30) + " FPS");
                ticksTillWindowUpdate = 30;
                elapsedFrames = 0;
            }
            else {
                ticksTillWindowUpdate--;
                elapsedFrames += elapsedTime;
            }

            input();

            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
            }

            render();

            if (!window.isVsync()) {
                sync();
            }
        }
    }

    protected void cleanup() {
        gameInterface.cleanup();
    }

    //Non-VSync FPS limiting function
    private void sync() {
        float loopSlot = 1f/TARGET_FPS;
        double endTime = timer.getLastLoopTime() + loopSlot;
        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void input() {
        mouseInput.input(window);
        gameInterface.input(window, mouseInput);
    }

    protected void update(float interval) {
        gameInterface.update(interval, mouseInput);
    }

    protected void render() {
        gameInterface.render(window);
        window.update();
    }
}
