package ZeusClient.game.objects;

import ZeusClient.engine.OBJLoader;
import ZeusClient.engine.RenderObj;
import ZeusClient.engine.graphics.Material;
import ZeusClient.engine.graphics.Mesh;
import ZeusClient.engine.graphics.Texture;

public class SkyBox extends RenderObj {
    public SkyBox(String objModel, String texFile) throws Exception {
        super();
        Mesh skyBoxMesh = OBJLoader.loadMesh(objModel);
        Texture skyBoxTexture = new Texture(texFile);
        skyBoxMesh.setMaterial(new Material(skyBoxTexture, 0.0f));
        setMesh(skyBoxMesh);
        setPosition(0, 0, 0);
    }
}
