package client.game;

import client.game.blockmodels.BlockModel;

import java.util.ArrayList;

public class BlockAtlas {
    private ArrayList<BlockDef> blockDefs;

    public BlockAtlas() {
        blockDefs = new ArrayList<>();
        create("_:unloaded", null, true, false, false);
        create("_:air", null, false, false, false);
    }

    public BlockDef create(String name, BlockModel model, boolean culls, boolean visible, boolean solid) {
        BlockDef def = new BlockDef(this, name, model, culls, visible, solid);
        short id = (short)(blockDefs.size());
        def.setId(id);
        blockDefs.add(def);
        return def;
    }

    public BlockDef getDef(int id) {
        return blockDefs.get(id);
    }

    public BlockDef getDef(String name) {
        for (BlockDef def : blockDefs) {
            if (def.getName().equals(name)) {
                return def;
            }
        }
        return null;
    }
}
