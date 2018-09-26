package client.game;

import client.engine.Entity;
import client.engine.MouseInput;
import client.engine.Window;
import client.engine.graphics.Camera;
import client.engine.Utils;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

public class Player extends Entity {
    private static final float MOUSE_SENSITIVITY = 0.2f;

    private static final float MAX_VERTICAL_SPEED = 0.9f;

    private static final float GROUND_DRAG = 0.8f;
    private static final float SKY_DRAG = 0.85f;

    private static final float FALL_SPEED = 0.02f;

    private static final float JUMP_VELOCITY = 0.1f;
    private static final float GROUNDED_SPEED = 0.02f;

    private final Camera camera;

    private boolean[] keysDown = {false, false, false, false, false, false};
    private boolean flying = false;
    private boolean fDown = false;

    Player(int x, int y, int z) {
        super(new Vector3f(x, y, z));
        camera = new Camera();
    }

    public void input(Window window, MouseInput mouseInput) {
        keysDown[0] = window.isKeyPressed(GLFW_KEY_W);
        keysDown[1] = window.isKeyPressed(GLFW_KEY_D);
        keysDown[2] = window.isKeyPressed(GLFW_KEY_S);
        keysDown[3] = window.isKeyPressed(GLFW_KEY_A);
        keysDown[4] = window.isKeyPressed(GLFW_KEY_SPACE);
        keysDown[5] = window.isKeyPressed(GLFW_KEY_LEFT_SHIFT);

        if (window.isKeyPressed(GLFW_KEY_F) && !fDown) {
            flying = !flying;
        }
        else if (fDown) {
            fDown = false;
        }
    }

    public void update(float interval, MouseInput mouseInput) {
        if (flying) {
            move();
            walkControls();
        }
        else {
            flyControls();
        }

        super.update();

        // Update camera based on mouse
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplayVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }

        camera.setPosition(getPosition());
    }

    private void walkControls() {

        float drag = SKY_DRAG;
        if (grounded) drag = GROUND_DRAG;

        if (grounded) {
            if (velocity.y < 0) velocity.y = 0;
        }
        else {
            velocity.y = Math.max(-MAX_VERTICAL_SPEED, velocity.y - FALL_SPEED);
        }

        Vector3f velocity = getVelocity();

        velocity.x = Math.max(0, Math.abs(velocity.x) * drag) * Utils.sign(velocity.x);
        velocity.z = Math.max(0, Math.abs(velocity.z) * drag) * Utils.sign(velocity.z);

        Vector3f vel = new Vector3f();

        if (keysDown[2]) vel.z = GROUNDED_SPEED;

        if (keysDown[0]) vel.z = -GROUNDED_SPEED;

        if (keysDown[1]) vel.x = GROUNDED_SPEED;

        if (keysDown[3]) vel.x = -GROUNDED_SPEED;

        if (keysDown[4] && grounded) vel.y = JUMP_VELOCITY;

        Vector3f finishedVel = getVelocity().add(camera.getAbsoluteOffsets(vel), new Vector3f());
        finishedVel.x = Math.abs(finishedVel.x) * Utils.sign(finishedVel.x);
        finishedVel.z = Math.abs(finishedVel.z) * Utils.sign(finishedVel.z);

        setVelocity(finishedVel);
    }

    private void flyControls() {
        Vector3f vel = new Vector3f();

        if (keysDown[2]) vel.z = 1;

        if (keysDown[0]) vel.z = -1;

        if (keysDown[1]) vel.x = 1;

        if (keysDown[3]) vel.x = -1;

        if (keysDown[4]) vel.y = 1;

        if (keysDown[5]) vel.y = -1;

        setPosition(getPosition().add(camera.getAbsoluteOffsets(vel), new Vector3f()));
    }

    public Camera getCamera() {
        return camera;
    }

    public Vector3f getRotation() {
        return getCamera().getRotation();
    }
}
