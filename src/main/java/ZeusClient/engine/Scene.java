package ZeusClient.engine;

import ZeusClient.engine.RenderObj;
import ZeusClient.engine.graphics.Mesh;
import ZeusClient.engine.graphics.light.SceneLight;
//import ZeusClient.game.MeshChunk;
import ZeusClient.game.MeshChunk;
import ZeusClient.game.objects.SkyBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {
    private Map<Mesh, List<RenderObj>> meshMap;
    private SkyBox skyBox;
    private SceneLight sceneLight;
    private List<MeshChunk> visibleChunks;

    public Scene() {
        meshMap = new HashMap<>();
    }

    public Map<Mesh, List<RenderObj>> getGameMeshes() {
        return meshMap;
    }

    public void setRenderObjects(RenderObj[] objs) {
        int numObjs = objs != null ? objs.length : 0;
        for (int i = 0; i < numObjs; i++) {
            RenderObj obj = objs[i];
            Mesh mesh = obj.getMesh();
            List<RenderObj> list = meshMap.computeIfAbsent(mesh, k -> new ArrayList<>());
            list.add(obj);
        }
    }

    public void setVisibleChunks(ArrayList<MeshChunk> visibleChunks) {
        this.visibleChunks = visibleChunks;
    }

    public void cleanup() {
        for (Mesh mesh : meshMap.keySet()) {
            mesh.cleanup();
        }
    }

    public SkyBox getSkyBox() {
        return skyBox;
    }

    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }

    public SceneLight getSceneLight() {
        return sceneLight;
    }

    public void setSceneLight(SceneLight sceneLight) {
        this.sceneLight = sceneLight;
    }

    public List<MeshChunk> getVisibleChunks() {
        return visibleChunks;
    }
}
