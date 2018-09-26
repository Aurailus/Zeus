package client.game.objects;

import client.engine.OBJLoader;
import client.engine.RenderObj;
import client.engine.graphics.Material;
import client.engine.graphics.Mesh;
import client.engine.graphics.Texture;

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
