package Zeus.game;

import Zeus.engine.MouseInput;
import Zeus.engine.Window;
import Zeus.engine.graphics.Camera;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

public class Player {
    private static final float MOUSE_SENSITIVITY = 0.2f;
    private static final float CAMERA_POS_STEP = 0.2f;
    private static final float CAMERA_DRAG = 3f;
    private static final float CAMERA_SPEED = 1f;

    final Camera camera;

    Vector3f velocity;

    private boolean[] keysDown = {false, false, false, false, false, false};

    Player(int x, int y, int z) {
        camera = new Camera();
        camera.setPosition(x, y, z);

        velocity = new Vector3f(0, 0, 0);
    }

    public void input(Window window, MouseInput mouseInput) {
        keysDown[0] = window.isKeyPressed(GLFW_KEY_W);
        keysDown[1] = window.isKeyPressed(GLFW_KEY_D);
        keysDown[2] = window.isKeyPressed(GLFW_KEY_S);
        keysDown[3] = window.isKeyPressed(GLFW_KEY_A);
        keysDown[4] = window.isKeyPressed(GLFW_KEY_SPACE);
        keysDown[5] = window.isKeyPressed(GLFW_KEY_LEFT_SHIFT);
    }

    public void update(float interval, MouseInput mouseInput) {
        if (keysDown[2] && velocity.z < 1) velocity.z = Math.min(velocity.z + CAMERA_DRAG * CAMERA_SPEED, CAMERA_SPEED);
        else if (!keysDown[2] && velocity.z > 0) velocity.z = Math.max(velocity.z - CAMERA_DRAG * CAMERA_SPEED, 0);

        if (keysDown[0] && velocity.z > -1) velocity.z = Math.max(velocity.z - CAMERA_DRAG * CAMERA_SPEED, -CAMERA_SPEED);
        else if (!keysDown[0] && velocity.z < 0) velocity.z = Math.min(velocity.z + CAMERA_DRAG * CAMERA_SPEED, 0);

        if (keysDown[1] && velocity.x < 1) velocity.x = Math.min(velocity.x + CAMERA_DRAG * CAMERA_SPEED, CAMERA_SPEED);
        else if (!keysDown[1] && velocity.x > 0) velocity.x = Math.max(velocity.x - CAMERA_DRAG * CAMERA_SPEED, 0);

        if (keysDown[3] && velocity.x > -1) velocity.x = Math.max(velocity.x - CAMERA_DRAG * CAMERA_SPEED, -CAMERA_SPEED);
        else if (!keysDown[3] && velocity.x < 0) velocity.x = Math.min(velocity.x + CAMERA_DRAG * CAMERA_SPEED, 0);

        if (keysDown[4] && velocity.y < 1) velocity.y = Math.min(velocity.y + CAMERA_DRAG * CAMERA_SPEED, CAMERA_SPEED);
        else if (!keysDown[4] && velocity.y > 0) velocity.y = Math.max(velocity.y - CAMERA_DRAG * CAMERA_SPEED, 0);

        if (keysDown[5] && velocity.y > -1) velocity.y = Math.max(velocity.y - CAMERA_DRAG * CAMERA_SPEED, -CAMERA_SPEED);
        else if (!keysDown[5] && velocity.y < 0) velocity.y = Math.min(velocity.y + CAMERA_DRAG * CAMERA_SPEED, 0);

        camera.movePosition(velocity.x * CAMERA_POS_STEP, velocity.y * CAMERA_POS_STEP, velocity.z * CAMERA_POS_STEP);

        // Update camera based on mouse
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplayVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }
    }

    public Camera getCamera() {
        return camera;
    }

    public Vector3f getRotation() {
        return getCamera().getRotation();
    }

    public Vector3f getPosition() {
        return getCamera().getPosition();
    }
}
