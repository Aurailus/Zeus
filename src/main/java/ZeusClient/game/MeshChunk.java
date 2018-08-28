package ZeusClient.game;

import ZeusClient.engine.RenderObj;
import ZeusClient.engine.graphics.ChunkMesh;
import org.joml.Vector3f;
import org.joml.Vector3i;

import static ZeusClient.game.MeshManager.REGION_SIZE;

public class MeshChunk extends RenderObj {
    public static final int CHUNK_SIZE = MeshManager.CHUNK_SIZE;
    public static final float LOD_STEP = CHUNK_SIZE * 20;

    private boolean[] adjacentBlockChunks = {false, false, false, false, false, false};

    MeshManager meshManager;
    private Vector3i pos;
    private int resolution;

    private ChunkMesh chunkMesh;

    public boolean dirty = true;
    public MeshData meshData = null;

    public MeshChunk(MeshManager meshManager, Vector3i regionPos, int cX, int cY, int cZ) {
        this(meshManager, regionPos, new Vector3i(cX, cY, cZ));
    }

    public MeshChunk(MeshManager meshManager, Vector3i regionPos, Vector3i chunkPos) {
        this.meshManager = meshManager;
        this.pos = new Vector3i(regionPos.x * REGION_SIZE + chunkPos.x, regionPos.y * REGION_SIZE + chunkPos.y, regionPos.z * REGION_SIZE + chunkPos.z);
        this.setPosition(pos.x * CHUNK_SIZE, pos.y * CHUNK_SIZE, pos.z * CHUNK_SIZE);

        Vector3f playerPos = meshManager.game.player.getPosition();

        int distFromPlayer = (int)Math.round(Math.sqrt(
                Math.pow(Math.abs(pos.x * CHUNK_SIZE - playerPos.x), 2) +
                Math.pow(Math.abs(pos.y * CHUNK_SIZE - playerPos.y), 2) +
                Math.pow(Math.abs(pos.z * CHUNK_SIZE - playerPos.z), 2)));

        resolution = (int)Math.min(distFromPlayer / LOD_STEP, 3);
    }

    //0: X+
    //1: X-
    //2: Z+
    //3: Z-
    //4: Y+
    //5: Y-
    public void setAdjacentState(int adjInd, boolean state) {
        adjacentBlockChunks[adjInd] = state;
        if (state && dirty) {
            var canRender = true;
            for (var i = 0; i < adjacentBlockChunks.length; i++) {
                if (!adjacentBlockChunks[i]) {
                    canRender = false;
                    break;
                }
            }
            if (canRender) {
                generateMesh();
            }
        }
    }

    public void generateMesh() {
        BlockChunk blockChunk = meshManager.blockManager.getChunk(pos);
        meshData = new MeshData(this, blockChunk, this.getResolution());
        meshManager.addDirtyChunk(this);
    }

    public void updateMesh() {
        if (meshData != null) {
            var mesh = getChunkMesh();
            if (mesh != null) {
                this.meshManager.removeVisibleChunk(this);
                mesh.cleanup();
            }
            if (meshData.verts.length != 0) {
                mesh = new ChunkMesh(meshData.verts, meshData.texCoords, meshData.normals, meshData.indices);
                mesh.setMaterial(meshManager.worldMaterial);
                chunkMesh = mesh;
                this.meshManager.addVisibleChunk(this);
            } else {
                setMesh(null);
            }
            dirty = false;
            meshData = null;
        }
    }

    public ChunkMesh getChunkMesh() {
        return chunkMesh;
    }

    public int getResolution() {
        return resolution;
    }

    public Vector3i getWorldCoords(int x, int y, int z) {
        return getWorldCoords(new Vector3i(x, y, z));
    }

    public Vector3i getWorldCoords(Vector3i position) {
        return new Vector3i(pos.x * CHUNK_SIZE + position.x, pos.y * CHUNK_SIZE + position.y, pos.z * CHUNK_SIZE + position.z);
    }

    public Vector3i getPos() {
        return pos;
    }

//        public int getBlock(Vector3i pos) {
//            return getBlock(pos.x, pos.y, pos.z);
//        }
//
//        public int getBlock(int x, int y, int z) {
//            return blocks[x + CHUNK_SIZE * (y + CHUNK_SIZE * z)];
//        }
//
//        public int setBlock(int block, Vector3i pos) {
//            return setBlock(block, pos.x, pos.y, pos.z);
//        }
//
//        public int setBlock(int block, int x, int y, int z) {
//            blocks[x + CHUNK_SIZE * (y + CHUNK_SIZE * z)] = block;
//            return block;
//        }
}
