package Zeus.engine.graphics;

import Zeus.engine.RenderObj;
import Zeus.engine.graphics.light.SceneLight;
import Zeus.game.objects.SkyBox;
import Zeus.game.objects.WorldChunk;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {
    private Map<Mesh, List<RenderObj>> meshMap;
    private Map<Vector3i, WorldChunk> worldChunkMap;
    private SkyBox skyBox;
    private SceneLight sceneLight;

    public Scene() {
        meshMap = new HashMap<>();
        worldChunkMap = new HashMap<>();
    }

    public Map<Vector3i, WorldChunk> getWorldChunks() {
        return worldChunkMap;
    }

    public Map<Mesh, List<RenderObj>> getGameMeshes() {
        return meshMap;
    }

    public void addWorldChunk(Vector3i pos, WorldChunk chunk) {
        if (worldChunkMap.containsKey(pos)) {
            System.out.println("Chunk already exists at " + pos.toString());
            return;
        }
        worldChunkMap.put(pos, chunk);
    }

    public WorldChunk getWorldChunk(Vector3i pos) {
        if (!worldChunkMap.containsKey(pos)) {
            return null;
        }
        return worldChunkMap.get(pos);
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
}
