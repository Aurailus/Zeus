package Zeus.game;

import java.util.List;
import org.joml.Vector3i;

public class RegionManager {

    BlockManager blockMan;
    MeshManager meshMan;

    public RegionManager(ZeusGame game) {
        blockMan = new BlockManager(game);
        meshMan = new MeshManager(game, blockMan);
    }

    public void init() {
        blockMan.generateWorld(new Vector3i(0, 0, 0), new Vector3i(16, 16, 16));
        meshMan.createRegion(new Vector3i(0, 0, 0));
        meshMan.updateDirtyMeshes();
    }

    public List<MeshChunk> getVisibleChunks() {
        return meshMan.getVisibleChunks();
    }
}
