package Zeus.engine.graphics;

import Zeus.engine.RenderObj;
import Zeus.engine.graphics.light.SceneLight;
import Zeus.game.MeshChunk;
import Zeus.game.objects.SkyBox;

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

    public void setVisibleChunks(List<MeshChunk> visibleChunks) {
        this.visibleChunks = visibleChunks;
    }

    public List<MeshChunk> getVisibleChunks() {
        return visibleChunks;
    }
}
