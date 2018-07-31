package Zeus.engine;

import Zeus.engine.graphics.Transformation;

public interface HudInterface {
    RenderObj[] getRenderObjects();
    void updateSize(Window window);

    default void cleanup() {
        RenderObj[] objs = getRenderObjects();
        for (RenderObj obj : objs) {
            obj.getMesh().cleanup();
        }
    }
}
