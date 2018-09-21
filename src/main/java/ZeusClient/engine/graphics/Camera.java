package ZeusClient.engine.graphics;

import org.joml.Vector3f;

public class Camera {
    private final Vector3f position;
    private final Vector3f rotation;

    public Camera() {
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
    }

    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        setPosition(position.x, position.y, position.z);
    }

    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        if (offsetZ != 0) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
            position.z += (float)Math.cos(Math.toRadians(rotation.y)) * offsetZ;
        }
        if (offsetX != 0) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
            position.z += (float)Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
        }
        position.y += offsetY;
    }

    public void moveAbsolute(float offsetX, float offsetY, float offsetZ) {
        position.x += offsetX;
        position.y += offsetY;
        position.z += offsetZ;
    }

    public Vector3f getAbsoluteOffsets(Vector3f o) {
        Vector3f offset = new Vector3f(0, 0, 0);
        if (o.z != 0) {
            offset.x += (float)Math.sin(Math.toRadians(rotation.y)) * -1.0f * o.z;
            offset.z += (float)Math.cos(Math.toRadians(rotation.y)) * o.z;
        }
        if (o.x != 0) {
            offset.x += (float)Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * o.x;
            offset.z += (float)Math.cos(Math.toRadians(rotation.y - 90)) * o.x;
        }
        offset.y += o.y;
        return offset;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    public void moveRotation(float offsetX, float offsetY, float offsetZ) {
        rotation.x += offsetX;
        rotation.y += offsetY;
        rotation.z += offsetZ;
    }
}
