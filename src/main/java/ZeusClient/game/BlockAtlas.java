package ZeusClient.game;

import ZeusClient.game.blockmodels.BlockModel;

import java.util.ArrayList;

public class BlockAtlas {
    public ArrayList<BlockDef> blockDefs;

    public BlockAtlas() {
        blockDefs = new ArrayList<>();
        create("_:air", null, false, false);
    }

    public BlockDef create(String name, BlockModel model) {
        return create(name, model, true, true);
    }

    public BlockDef create(String name, BlockModel model, boolean culls) {
        return create(name, model, culls, true);
    }

    public BlockDef create(String name, BlockModel model, boolean culls, boolean visible) {
        BlockDef def = new BlockDef(this, name, model, culls, visible);
        short id = (short)(blockDefs.size());
        def.setId(id);
        blockDefs.add(def);
        return def;
    }
}
