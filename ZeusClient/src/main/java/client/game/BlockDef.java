package client.game;

import client.game.blockmodels.BlockModel;

public class BlockDef {

    private BlockModel model;
    private String name;
    private short id;
    private boolean culls;
    private boolean visible;
    private boolean solid;

    public BlockDef(BlockAtlas atlas, String name, BlockModel model, boolean culls, boolean visible, boolean solid) {
        setName(name);
        setModel(model);
        setCulls(culls);
        setVisible(visible);
        setSolid(solid);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setModel(BlockModel model) {
        this.model = model;
    }

    public void setId(short id) {
        this.id = id;
    }

    public void setCulls(boolean culls) {
        this.culls = culls;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setSolid(boolean solid) {
        this.solid = solid;
    }

    public String getName() {
        return name;
    }

    public BlockModel getModel() {
        return model;
    }

    public boolean getCulls() {
        return culls;
    }

    public boolean getVisible() {
        return visible;
    }

    public boolean getSolid() {
        return solid;
    }
}