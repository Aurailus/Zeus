package client.engine;

import client.engine.graphics.Mesh;
import org.joml.Vector3f;

public class RenderObj {
    private Mesh mesh;

    private final Vector3f position;
    private final Vector3f rotation;
    private float scale;

    public RenderObj() {
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        scale = 1;
    }

    public RenderObj(Mesh mesh) {
        this();
        this.mesh = mesh;
    }

    public RenderObj(Mesh mesh, Vector3f position, Float scale, Vector3f rotation) {
        this.mesh = mesh;
        this.position = (position != null) ? new Vector3f(position) : new Vector3f(0, 0, 0);
        this.rotation = (rotation != null) ? new Vector3f(rotation) : new Vector3f(0, 0, 0);
        this.scale = (scale != null) ? scale : 1;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public void setPosition(Vector3f position) {
        this.position.set(position);
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation.set(rotation);
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }
}