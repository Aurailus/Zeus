package ZeusClient.engine;

import ZeusClient.engine.helpers.Utils;
import ZeusClient.game.Game;
import org.joml.Vector3f;

public class Entity {
    private Vector3f position;
    public Vector3f velocity;
    public boolean grounded;

    private boolean collidesWithBlocks;

    public Entity(Vector3f position) {
        this.position = position;
        velocity = new Vector3f(0, 0, 0);
        this.collidesWithBlocks = false;
    }

    public boolean doesCollide() {
        return collidesWithBlocks;
    }

    public void setCollides(boolean collides) {
        this.collidesWithBlocks = collides;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void update() {
        grounded = collisionAtPos(new Vector3f(0, -0.05f, 0).add(position));
    }

    public void move() {
        float MOVE_STEP = 0.05f; //Precision

        for (float i = 0; i < Math.abs(velocity.x); i += MOVE_STEP) {
            var interval = Math.max(Math.min(MOVE_STEP, Math.abs(velocity.x) - i), 0) * Utils.sign(velocity.x);
            moveIfFree(new Vector3f(interval, 0, 0), 0);
        }

        for (float i = 0; i < Math.abs(velocity.z); i += MOVE_STEP) {
            var interval = Math.max(Math.min(MOVE_STEP, Math.abs(velocity.z) - i), 0) * Utils.sign(velocity.z);
            moveIfFree(new Vector3f(0, 0, interval), 2);
        }

        for (float i = 0; i < Math.abs(velocity.y); i += MOVE_STEP) {
            var interval = Math.max(Math.min(MOVE_STEP, Math.abs(velocity.y) - i), 0) * Utils.sign(velocity.y);
            moveIfFree(new Vector3f(0, interval, 0), 1);
        }
    }

    private void moveIfFree(Vector3f offset, int axis) {
        Vector3f testPosition = getPosition().add(offset, new Vector3f());
        if (!collisionAtPos(testPosition))
            this.setPosition(testPosition);
        else {
            switch (axis) { //x, y, z
                case 0:
                    velocity.x = 0;
                    break;
                case 1:
                    velocity.y = 0;
                    break;
                case 2:
                    velocity.z = 0;
                    break;
                default:
                    break;
            }
        }
    }

    private boolean collisionAtPos(Vector3f pos) {
        int minX = (int)Math.floor(pos.x - 0.3);
        int maxX = (int)Math.floor(pos.x + 0.3);

        int minY = (int)Math.floor(pos.y - 1.8);
        int maxY = (int)Math.floor(pos.y + 0.1);

        int minZ = (int)Math.floor(pos.z - 0.3);
        int maxZ = (int)Math.floor(pos.z + 0.3);

        boolean solid = false;
        for (int i = minX; i <= maxX; i++) {
            for (int j = minY; j <= maxY; j++) {
                for (int k = minZ; k <= maxZ; k++) {
                    if (Game.world.getBlock(i, j, k) == -1 || Game.definitions.getDef(Game.world.getBlock(i, j, k)).getSolid()) {
                        solid = true;
                        break;
                    }
                }
            }
        }
        return solid;
    }
}
