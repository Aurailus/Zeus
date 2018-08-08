package Zeus.game;

import Zeus.engine.*;
import Zeus.engine.graphics.*;
import Zeus.engine.graphics.light.DirectionalLight;
import Zeus.engine.graphics.light.SceneLight;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;

public class ZeusGame implements GameLogic {
    static final boolean MULTITHREADING_ENABLED = true;

    private final Renderer renderer;
    public Scene scene;
    private Hud hud;
    Player player;
    private RegionManager worldRegions;


    private double interval;

    private float lightAngle;

    public ZeusGame() {
        renderer = new Renderer();
        lightAngle = -90;
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        scene = new Scene();
        player = new Player(0, 0, 1);



        scene.setVisibleChunks(worldRegions.visibleChunks());

        hud = new Hud("DEMO");

        Mesh mesh = OBJLoader.loadMesh("/models/bunny.obj");
        mesh.setMaterial(new Material(new Vector4f(0.7f, 0.7f, 0.7f, 1.0f), 1f));
        RenderObj bunny = new RenderObj(mesh);
        scene.setRenderObjects(new RenderObj[] {bunny});

        setupLights();
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
        this.interval = interval;

        player.update(interval, mouseInput);

        hud.rotateCompass(player.getCamera().getRotation().y);

        // Update directional light direction, intensity and colour
//        SceneLight sceneLight = scene.getSceneLight();
//        DirectionalLight directionalLight = sceneLight.getDirectionalLight();
//        lightAngle += 0.5f;
//        if (lightAngle > 90) {
//            directionalLight.setIntensity(0);
//            if (lightAngle >= 360) {
//                lightAngle = -90;
//            }
//            sceneLight.getSkyBoxLight().set(0.3f, 0.3f, 0.3f);
//        } else if (lightAngle <= -80 || lightAngle >= 80) {
//            float factor = 1 - (Math.abs(lightAngle) - 80) / 10.0f;
//            sceneLight.getSkyBoxLight().set(factor, factor, factor);
//            directionalLight.setIntensity(factor);
//            directionalLight.getColor().y = Math.max(factor, 0.9f);
//            directionalLight.getColor().z = Math.max(factor, 0.5f);
//        } else {
//            sceneLight.getSkyBoxLight().set(1.0f, 1.0f, 1.0f);
//            directionalLight.setIntensity(1);
//            directionalLight.getColor().x = 1;
//            directionalLight.getColor().y = 1;
//            directionalLight.getColor().z = 1;
//        }
//        double angRad = Math.toRadians(lightAngle);
//        directionalLight.getDirection().x = (float) Math.sin(angRad);
//        directionalLight.getDirection().y = (float) Math.cos(angRad);
    }

    @Override
    public void render(Window window) {
        hud.updateSize(window);
        renderer.render(window, player.getCamera(), scene, hud);
        window.updateTitle("Zeus - " + interval + " FPS");
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        scene.cleanup();
        hud.cleanup();
    }
}
