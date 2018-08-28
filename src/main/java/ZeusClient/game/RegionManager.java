package ZeusClient.game;

import java.util.List;

public class RegionManager {

    private BlockManager blockMan;
    private MeshManager meshMan;
    private ZeusGame game;

    public RegionManager(ZeusGame game) {
        this.game = game;
        blockMan = new BlockManager();
        meshMan = new MeshManager(game, blockMan);
    }

    public void init() {
    }

    public void update() {
        meshMan.update();
    }

    public void render() {
        meshMan.render();
    }

    public List<MeshChunk> getVisibleChunks() {
        return meshMan.getVisibleChunks();
    }
}
