package ZeusClient.game;

import ZeusClient.engine.*;
import ZeusClient.engine.graphics.Renderer;
import ZeusClient.engine.graphics.light.DirectionalLight;
import ZeusClient.engine.graphics.light.SceneLight;
import ZeusClient.engine.helpers.TextureAtlas;
import ZeusClient.game.blockmodels.*;
import ZeusClient.game.network.ConnMan;
import org.joml.Vector3f;

import java.io.IOException;

public class Game implements GameLogic {

    private final Renderer renderer;
    private Scene scene;
    private Hud hud;

    private Player player;

    public static ConnMan connection;
    public static ChunkAtlas world;
    public static BlockAtlas definitions;
    public static TextureAtlas assets;

    private int tick = 0;

    public Game() {
        renderer = new Renderer();
    }

    @Override
    public void init(Window window) throws Exception {

        renderer.init(window);

        assets = new TextureAtlas(2048);
        world = new ChunkAtlas();
        definitions = new BlockAtlas();

        assets.loadTexturesFolder();
        try {
            assets.encode();
        } catch (IOException e) {
            e.printStackTrace();
        }

        connection = new ConnMan("localhost", 30005);

        scene = new Scene();
        hud = new Hud("DEMO");

        player = new Player(-5, 8, 6);

        definitions.create("default:grass", new BM_CubeTexLifted("default_grass_top", "default_grass_side", "default_grass_float", "default_dirt"), true, true, true);
        definitions.create("default:dirt", new BM_CubeTexOne("default_dirt"), true, true, true);
        definitions.create("default:stone", new BM_CubeTexOne("default_stone"), true, true, true);
        definitions.create("default:tallgrass_0", new BM_PlantLike("default_tallgrass_1"), false, true, false);
        definitions.create("default:tallgrass_1", new BM_PlantLike("default_tallgrass_2"), false, true, false);
        definitions.create("default:tallgrass_2", new BM_PlantLike("default_tallgrass_3"), false, true, false);
        definitions.create("default:tallgrass_3", new BM_PlantLike("default_tallgrass_4"), false, true, false);
        definitions.create("default:tallgrass_4", new BM_PlantLike("default_tallgrass_5"), false, true, false);
        definitions.create("default:stone_brick", new BM_CubeTexOne("default_stone_bricks"), true, true, true);
        definitions.create("default:stone_block", new BM_CubeTexOne("default_stone_block"), true, true, true);
        definitions.create("default:log", new BM_CubeTexFour("default_log_top", "default_log_side", "default_log_bottom"), true, true, true);
        definitions.create("default:leaves", new BM_CubeTexPoof("default_leaves", "default_leaves_puff"), false, true, true);

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


        scene.setVisibleChunks(world.getVisibleChunks());

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
        tick++;

        connection.update();

        player.update(interval, mouseInput);

        if (tick % 30 == 0) world.loadChunksAroundPos(player.getPosition(), 10);
        world.update();

        hud.rotateCompass(player.getCamera().getRotation().y);

    }

    @Override
    public void render(Window window) {
        hud.updateSize(window);
        renderer.render(window, player.getCamera(), scene, hud);
    }

    @Override
    public void cleanup() {
        world.cleanup();
        connection.kill();
        renderer.cleanup();
        scene.cleanup();
        hud.cleanup();
    }
}
