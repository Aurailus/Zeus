package Zeus.game;

import Zeus.engine.HudInterface;
import Zeus.engine.OBJLoader;
import Zeus.engine.RenderObj;
import Zeus.engine.Window;
import Zeus.engine.graphics.Material;
import Zeus.engine.graphics.Mesh;
import Zeus.engine.graphics.Transformation;
import org.joml.Vector4f;

import java.awt.*;

public class Hud implements HudInterface {
    public static final Font FONT = new Font("Arial", Font.PLAIN, 20);
    private static final String CHARSET = "ISO-8859-1";

    private final RenderObj[] renderObjs;
//    private final TextItem statusTextItem;
    private final RenderObj compassItem;

    public Hud(String statusText) throws Exception {
//        FontTexture fontTexture = new FontTexture(FONT, CHARSET);
        //StatusTextItem stuff here

        //Create compass
        Mesh mesh = OBJLoader.loadMesh("/models/compass.obj");
        Material material = new Material();
        material.setAmbientColor(new Vector4f(1, 0, 0, 1));
        mesh.setMaterial(material);
        compassItem = new RenderObj(mesh);
        compassItem.setScale(40.0f);
        compassItem.setRotation(0, 0, 180);

        renderObjs = new RenderObj[]{compassItem};
    }

    public void setStatusText(String statusText) {
        //TODO
    }

    public void rotateCompass(float angle) {
        this.compassItem.setRotation(0, 0, 180 + angle);
    }

    @Override
    public RenderObj[] getRenderObjects() {
        return renderObjs;
    }

    @Override
    public void updateSize(Window window) {
        //statustextsetposition
        this.compassItem.setPosition(window.getWidth() - 40f, 50f, 0);
    }
}
