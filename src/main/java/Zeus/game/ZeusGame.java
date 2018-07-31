package Zeus.game;

import Zeus.engine.*;
import Zeus.engine.graphics.*;
import Zeus.engine.graphics.light.DirectionalLight;
import Zeus.engine.graphics.light.SceneLight;
import Zeus.game.objects.SkyBox;
import Zeus.game.objects.WorldChunk;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;

import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class ZeusGame implements GameLogic {
    private static final float MOUSE_SENSITIVITY = 0.2f;
    private static final float CAMERA_POS_STEP = 0.2f;
    private static final float CAMERA_DRAG = 0.2f;

    private boolean[] keysDown = {false, false, false, false, false, false};
    private Vector3f velocity;
    private final Renderer renderer;
    private final Camera camera;
    public Scene scene;
    private Hud hud;
    private float lightAngle;

    public ZeusGame() {
        renderer = new Renderer();
        camera = new Camera();
        lightAngle = -90;
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        scene = new Scene();

        float skyBoxScale = 20000f;

        SkyBox skyBox = new SkyBox("/models/skybox.obj", "/textures/skybox.png");
        skyBox.setScale(skyBoxScale);

        scene.setSkyBox(skyBox);

        Mesh mesh = OBJLoader.loadMesh("/models/bunny.obj");
        mesh.setMaterial(new Material(new Vector4f(0.7f, 0.7f, 0.7f, 1.0f), 1f));
        RenderObj bunny = new RenderObj(mesh);

        Texture worldTex = new Texture("/textures/grassblock.png");
        final int SIZE = 24;

        var count = 0;
        for (var i = 0; i < SIZE; i++) {
            for (var j = 0; j < SIZE; j++) {
                for (var k = -7; k < SIZE-7; k++) { //Lower rendered chunks so that more stuff is visible for testing
                    new WorldChunk(worldTex, this, i, k, j);
//                    System.out.println(++count);
                }
            }
        }

        scene.setRenderObjects(new RenderObj[] {bunny});

        setupLights();

        hud = new Hud("DEMO");

        camera.getPosition().x = 0.0f;
        camera.getPosition().y = 0.0f;
        camera.getPosition().z = 7.0f;

        velocity = new Vector3f();
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
        keysDown[0] = window.isKeyPressed(GLFW_KEY_W);
        keysDown[1] = window.isKeyPressed(GLFW_KEY_D);
        keysDown[2] = window.isKeyPressed(GLFW_KEY_S);
        keysDown[3] = window.isKeyPressed(GLFW_KEY_A);
        keysDown[4] = window.isKeyPressed(GLFW_KEY_SPACE);
        keysDown[5] = window.isKeyPressed(GLFW_KEY_LEFT_SHIFT);
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        if (keysDown[2] && velocity.z < 1) velocity.z = Math.min(velocity.z + CAMERA_DRAG, 1);
        else if (!keysDown[2] && velocity.z > 0) velocity.z = Math.max(velocity.z - CAMERA_DRAG, 0);

        if (keysDown[0] && velocity.z > -1) velocity.z = Math.max(velocity.z - CAMERA_DRAG, -1);
        else if (!keysDown[0] && velocity.z < 0) velocity.z = Math.min(velocity.z + CAMERA_DRAG, 0);

        if (keysDown[1] && velocity.x < 1) velocity.x = Math.min(velocity.x + CAMERA_DRAG, 1);
        else if (!keysDown[1] && velocity.x > 0) velocity.x = Math.max(velocity.x - CAMERA_DRAG, 0);

        if (keysDown[3] && velocity.x > -1) velocity.x = Math.max(velocity.x - CAMERA_DRAG, -1);
        else if (!keysDown[3] && velocity.x < 0) velocity.x = Math.min(velocity.x + CAMERA_DRAG, 0);

        if (keysDown[4] && velocity.y < 1) velocity.y = Math.min(velocity.y + CAMERA_DRAG, 1);
        else if (!keysDown[4] && velocity.y > 0) velocity.y = Math.max(velocity.y - CAMERA_DRAG, 0);

        if (keysDown[5] && velocity.y > -1) velocity.y = Math.max(velocity.y - CAMERA_DRAG, -1);
        else if (!keysDown[5] && velocity.y < 0) velocity.y = Math.min(velocity.y + CAMERA_DRAG, 0);

        camera.movePosition(velocity.x * CAMERA_POS_STEP, velocity.y * CAMERA_POS_STEP, velocity.z * CAMERA_POS_STEP);

        // Update camera based on mouse
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplayVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);

            // Update HUD compass
            hud.rotateCompass(camera.getRotation().y);
        }

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
        renderer.render(window, camera, scene, hud);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        scene.cleanup();
        hud.cleanup();
    }
}
