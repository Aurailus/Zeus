package Zeus.game.objects;

import Zeus.engine.OBJLoader;
import Zeus.engine.RenderObj;
import Zeus.engine.graphics.Material;
import Zeus.engine.graphics.Mesh;
import Zeus.engine.graphics.Texture;
import Zeus.engine.graphics.Transformation;

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
