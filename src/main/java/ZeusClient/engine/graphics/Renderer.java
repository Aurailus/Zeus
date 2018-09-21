package ZeusClient.engine.graphics;

import ZeusClient.engine.HudInterface;
import ZeusClient.engine.RenderObj;
import ZeusClient.engine.Scene;
import ZeusClient.engine.helpers.Utils;
import ZeusClient.engine.Window;
import ZeusClient.engine.graphics.light.DirectionalLight;
import ZeusClient.engine.graphics.light.PointLight;
import ZeusClient.engine.graphics.light.SceneLight;
import ZeusClient.engine.graphics.light.SpotLight;
import ZeusClient.game.MeshChunk;
import ZeusClient.game.objects.SkyBox;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    private static final float FOV = (float) Math.toRadians(60.0f);

    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 2000000f;

    private static final int MAX_POINT_LIGHTS = 5;
    private static final int MAX_SPOT_LIGHTS = 5;

    private static int[] repeatScale = {1, 2, 4, 8, 16};

    private final Transformation transformation;

    private ShaderProgram sceneShaderProgram;
    private ShaderProgram hudShaderProgram;
    private ShaderProgram skyBoxShaderProgram;
    private ShaderProgram terrainShaderProgram;

    private final float specularPower;

    public Renderer() {
        transformation = new Transformation();
        specularPower = 10f;
    }

    public void init(Window window) throws Exception {
        setupSkyBoxShader();
        setupSceneShader();
        setupHudShader();
        setupTerrainShader();
    }

    private void setupSkyBoxShader() throws Exception {
        skyBoxShaderProgram = new ShaderProgram();
        skyBoxShaderProgram.createVertexShader(Utils.loadResource("/shaders/sb_vertex.vs"));
        skyBoxShaderProgram.createFragmentShader(Utils.loadResource("/shaders/sb_fragment.fs"));
        skyBoxShaderProgram.link();

        //Create uniforms for Projection Matrix
        skyBoxShaderProgram.createUniform("projectionMatrix");
        skyBoxShaderProgram.createUniform("modelViewMatrix");
        skyBoxShaderProgram.createUniform("texture_sampler");
        skyBoxShaderProgram.createUniform("ambientLight");
    }

    private void setupSceneShader() throws Exception {
        sceneShaderProgram = new ShaderProgram();
        sceneShaderProgram.createVertexShader(Utils.loadResource("/shaders/scene_vertex.vs"));
        sceneShaderProgram.createFragmentShader(Utils.loadResource("/shaders/scene_fragment.fs"));
        sceneShaderProgram.link();

        //Create uniforms for modelView and projection Matrices and Texture
        sceneShaderProgram.createUniform("projectionMatrix");
        sceneShaderProgram.createUniform("modelViewMatrix");
        sceneShaderProgram.createUniform("texture_sampler");
        //Create uniform for material
        sceneShaderProgram.createMaterialUniform("material");
        //Create lighting related uniforms
        sceneShaderProgram.createUniform("specularPower");
        sceneShaderProgram.createUniform("ambientLight");
        sceneShaderProgram.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
        sceneShaderProgram.createSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS);
        sceneShaderProgram.createDirectionalLightUniform("directionalLight");
    }

    private void setupTerrainShader() throws Exception {
        terrainShaderProgram = new ShaderProgram();
        terrainShaderProgram.createVertexShader(Utils.loadResource("/shaders/terrain_vertex.vs"));
        terrainShaderProgram.createFragmentShader(Utils.loadResource("/shaders/terrain_fragment.fs"));
        terrainShaderProgram.link();

        //Create uniforms for modelView and projection Matrices and Texture
        terrainShaderProgram.createUniform("projectionMatrix");
        terrainShaderProgram.createUniform("modelViewMatrix");
        terrainShaderProgram.createUniform("texture_sampler");
        //Set texture atlas uniforms
//        terrainShaderProgram.createUniform("repeat_scale");
//        terrainShaderProgram.createUniform("atlas_scale");
        //Create uniform for material
//        terrainShaderProgram.createMaterialUniform("material");
        //Create lighting related uniforms
//        terrainShaderProgram.createUniform("specularPower");
//        terrainShaderProgram.createUniform("ambientLight");
//        terrainShaderProgram.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
//        terrainShaderProgram.createSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS);
//        terrainShaderProgram.createDirectionalLightUniform("directionalLight");
    }

    private void setupHudShader() throws Exception {
        hudShaderProgram = new ShaderProgram();
        hudShaderProgram.createVertexShader(Utils.loadResource("/shaders/hud_vertex.vs"));
        hudShaderProgram.createFragmentShader(Utils.loadResource("/shaders/hud_fragment.fs"));
        hudShaderProgram.link();

        //Create uniforms for Orthographic-model projection matrix and base color
        hudShaderProgram.createUniform("projModelMatrix");
        hudShaderProgram.createUniform("color");
        hudShaderProgram.createUniform("hasTexture");
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, Scene scene, HudInterface hud) {
        clear();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        transformation.updateProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        transformation.updateViewMatrix(camera);

        renderSkyBox(window, camera, scene);

        renderScene(window, camera, scene);

        renderHud(window, hud);
    }

    public void renderSkyBox(Window window, Camera camera, Scene scene) {
        if (scene.getSkyBox() != null) {
            skyBoxShaderProgram.bind();
            skyBoxShaderProgram.setUniform("texture_sampler", 0);

            Matrix4f projectionMatrix = transformation.getProjectionMatrix();
            skyBoxShaderProgram.setUniform("projectionMatrix", projectionMatrix);
            SkyBox skyBox = scene.getSkyBox();
            Matrix4f viewMatrix = transformation.getViewMatrix();

            //Reset transformation matrix values
            viewMatrix.m30(0);
            viewMatrix.m31(0);
            viewMatrix.m32(0);

            Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(skyBox, viewMatrix);
            skyBoxShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            skyBoxShaderProgram.setUniform("ambientLight", scene.getSceneLight().getSkyBoxLight());

            scene.getSkyBox().getMesh().render();

            skyBoxShaderProgram.unbind();
        }
    }

    private void renderScene(Window window, Camera camera, Scene scene) {
        //Render terrain
        terrainShaderProgram.bind();

        Matrix4f projectionMatrix = transformation.getProjectionMatrix();
        terrainShaderProgram.setUniform("projectionMatrix", projectionMatrix);

        Matrix4f viewMatrix = transformation.getViewMatrix();

        SceneLight sceneLight = scene.getSceneLight();
//        renderLights(terrainShaderProgram, viewMatrix, sceneLight);

        terrainShaderProgram.setUniform("texture_sampler", 0);
//        terrainShaderProgram.setUniform("atlas_scale", 1/(512f/16f));

        var chunks = scene.getVisibleChunks();
        for (MeshChunk chunk : chunks) {
            var mesh = chunk.getMesh();
            Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(chunk.getObject(), viewMatrix);
//            terrainShaderProgram.setUniform("material", mesh.getMaterial());
            terrainShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
//            terrainShaderProgram.setUniform("repeat_scale", (float)repeatScale[0]);
            mesh.render();
        }

        terrainShaderProgram.unbind();

        //Render Objects
        sceneShaderProgram.bind();

        sceneShaderProgram.setUniform("projectionMatrix", projectionMatrix);

        renderLights(sceneShaderProgram, viewMatrix, sceneLight);

        sceneShaderProgram.setUniform("texture_sampler", 0);

        var mapMeshes = scene.getGameMeshes();
        for (Mesh mesh : mapMeshes.keySet()) {
            sceneShaderProgram.setUniform("material", mesh.getMaterial());
            mesh.renderList(mapMeshes.get(mesh), (RenderObj renderObj) -> {
                Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(renderObj, viewMatrix);
                sceneShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            });
        }

        sceneShaderProgram.unbind();
    }

    public void renderLights(ShaderProgram program, Matrix4f viewMatrix, SceneLight sceneLight) {
        program.setUniform("ambientLight", sceneLight.getAmbientLight());
        program.setUniform("specularPower", specularPower);

        //Transform pointLights into ViewModel space
        PointLight[] pointLights = sceneLight.getPointLightList();
        int numLights = pointLights != null ? pointLights.length : 0;
        for (int i = 0; i < numLights; i++) {
            //Get a copy of the object and transform it's position to the view coordinates
            PointLight pointLight = new PointLight(pointLights[i]);
            Vector3f lightPos = pointLight.getPosition();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;
            program.setUniform("pointLights", pointLight, i);
        }

        SpotLight[] spotLights = sceneLight.getSpotLightList();
        numLights = spotLights != null ? spotLights.length : 0;
        for (int i = 0; i < numLights; i++) {
            SpotLight spotLight = new SpotLight(spotLights[i]);
            Vector4f dir = new Vector4f(spotLight.getConeDirection(), 0);
            dir.mul(viewMatrix);
            spotLight.setConeDirection(new Vector3f(dir.x, dir.y, dir.z));

            Vector3f lightPos = spotLight.getPointLight().getPosition();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;

            program.setUniform("spotLights", spotLight, i);
        }

        DirectionalLight dirLight = new DirectionalLight(sceneLight.getDirectionalLight());
        Vector4f dir = new Vector4f(dirLight.getDirection(), 0);
        dir.mul(viewMatrix);
        dirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        program.setUniform("directionalLight", dirLight);
    }

    private void renderHud(Window window, HudInterface hud) {
        hudShaderProgram.bind();

        Matrix4f ortho = transformation.getOrthoProjectionMatrix(0, window.getWidth(), window.getHeight(), 0);
        for (RenderObj obj : hud.getRenderObjects()) {
            var mesh = obj.getMesh();
            //Set ortho and model matrix for the object
            Matrix4f projModelMatrix = transformation.buildOrthoProjModelMatrix(obj, ortho);
            hudShaderProgram.setUniform("projModelMatrix", projModelMatrix);
            hudShaderProgram.setUniform("color", obj.getMesh().getMaterial().getAmbientColor());
            hudShaderProgram.setUniform("hasTexture", obj.getMesh().getMaterial().isTextured() ? 1 : 0);

            mesh.render();
        }

        hudShaderProgram.unbind();
    }

    public void cleanup() {
        if (skyBoxShaderProgram != null) {
            skyBoxShaderProgram.cleanup();
        }
        if (sceneShaderProgram != null) {
            sceneShaderProgram.cleanup();
        }
        if (hudShaderProgram != null) {
            hudShaderProgram.cleanup();
        }
    }
}
