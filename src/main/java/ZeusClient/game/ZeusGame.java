package ZeusClient.game;

import ZeusClient.engine.*;
import ZeusClient.engine.graphics.Material;
import ZeusClient.engine.graphics.Mesh;
import ZeusClient.engine.graphics.Renderer;
import ZeusClient.engine.graphics.light.DirectionalLight;
import ZeusClient.engine.graphics.light.SceneLight;
import ZeusClient.game.network.ConnMan;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.function.BiConsumer;

public class ZeusGame implements GameLogic {

    private final Renderer renderer;
    public Scene scene;
    private Hud hud;
    Player player;
    private ConnMan connMan;
    ArrayList<MeshChunk> chunks = new ArrayList<>();

    public ZeusGame() {
        renderer = new Renderer();
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        player = new Player(0, 5, 0);
        scene = new Scene();
        hud = new Hud("DEMO");
        connMan = new ConnMan("localhost", 30005);


        var SIZE = 8;
        for (var i = -SIZE; i < SIZE; i++) {
            for (var j = -SIZE/ 2; j < SIZE/2; j++) {
                for (var k = -SIZE; k < SIZE; k++) {
                    loadChunk(i, j, k);
                }
            }
        }

//        Mesh mesh = OBJLoader.loadMesh("/models/bunny.obj");
//        mesh.setMaterial(new Material(new Vector4f(0.7f, 0.7f, 0.7f, 1.0f), 1f));
//        var COUNT = 360;
//        RenderObj[] obs = new RenderObj[COUNT];
//        for (var i = 0; i < COUNT; i++) {
//            RenderObj bunny = new RenderObj(mesh);
//            bunny.setPosition(i, 0, 0);
//            obs[i] = bunny;
//        }
//        scene.setRenderObjects(obs);

        scene.setVisibleChunks(chunks);

        setupLights();
    }



    private void loadChunk(int x, int y, int z) {
        connMan.requestChunk(new Vector3i(x, y, z), (pos, blockChunk) -> {
            long start = System.currentTimeMillis();

            MeshChunk chunk = new MeshChunk(new Vector3i(pos.x, pos.y, pos.z));
            chunk.createMesh(blockChunk);
            if (chunk.getMesh() != null) chunks.add(chunk);

            System.out.println("Average chunk gen time: " + (System.currentTimeMillis() - start) / 100f + "ms");
        });
    }

    private void setupLights() {
        SceneLight sceneLight = new SceneLight();
        scene.setSceneLight(sceneLight);

        //Ambient Light
        sceneLight.setAmbientLight(new Vector3f(0.5f, 0.5f, 0.5f));
        sceneLight.setSkyBoxLight(new Vector3f(1.0f, 1.0f, 1.0f));

        //Directional Light
        float lightIntensity = 1.0f;
        Vector3f lightPosition = new Vector3f(1, 0.8f, 0.6f);
        sceneLight.setDirectionalLight(new DirectionalLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity));
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        player.input(window, mouseInput);
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        connMan.update();

        player.update(interval, mouseInput);

        hud.rotateCompass(player.getCamera().getRotation().y);
    }

    @Override
    public void render(Window window) {
        hud.updateSize(window);
        renderer.render(window, player.getCamera(), scene, hud);
    }

    @Override
    public void cleanup() {
        connMan.kill();
        renderer.cleanup();
        scene.cleanup();
        hud.cleanup();
    }
}
