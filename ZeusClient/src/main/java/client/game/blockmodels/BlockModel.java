package client.game.blockmodels;

public class BlockModel {
    public MeshPart[] xPosMP, xNegMP, yPosMP, yNegMP, zPosMP, zNegMP, noCulledMP;

    public BlockModel() {

    }

    public BlockModel(MeshPart[] xPosMP, MeshPart[] xNegMP, MeshPart[] yPosMP,
                      MeshPart[] yNegMP, MeshPart[] zPosMP, MeshPart[] zNegMP,
                      MeshPart[] noCulledMP) {
        this.xPosMP = xPosMP;
        this.xNegMP = xNegMP;
        this.yPosMP = yPosMP;
        this.yNegMP = yNegMP;
        this.zPosMP = zPosMP;
        this.zNegMP = zNegMP;
        this.noCulledMP = noCulledMP;
    }
}