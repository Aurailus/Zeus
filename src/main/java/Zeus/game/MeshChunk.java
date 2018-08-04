package Zeus.game;

import Zeus.engine.RenderObj;
import Zeus.engine.graphics.Mesh;
import org.joml.Vector3i;

import static Zeus.game.MeshManager.REGION_SIZE;

public class MeshChunk extends RenderObj {
    public static final int CHUNK_SIZE = MeshManager.CHUNK_SIZE;

    private boolean[] adjacentBlockChunks = {false, false, false, false, false, false};

    private MeshManager meshManager;
    private Vector3i pos;
    BlockChunk blockChunk;

    private boolean dirty = true;

    public MeshChunk(MeshManager meshManager, Vector3i regionPos, int cX, int cY, int cZ) {
        this(meshManager, regionPos, new Vector3i(cX, cY, cZ));
    }

    public MeshChunk(MeshManager meshManager, Vector3i regionPos, Vector3i chunkPos) {
        this.meshManager = meshManager;
        this.pos = new Vector3i(regionPos.x * REGION_SIZE + chunkPos.x, regionPos.y * REGION_SIZE + chunkPos.y, regionPos.z * REGION_SIZE + chunkPos.z);
        this.setPosition(pos.x * CHUNK_SIZE, pos.y * CHUNK_SIZE, pos.z * CHUNK_SIZE);
    }

    public void init() {
        var bm = meshManager;
        MeshChunk adjacent;
        adjacent = bm.getChunk(new Vector3i(pos.x - 1, pos.y, pos.z));
        if (adjacent != null) {
            adjacent.setAdjacentState(0, true);
            this.setAdjacentState(1, true);
        }
        adjacent = bm.getChunk(new Vector3i(pos.x + 1, pos.y, pos.z));
        if (adjacent != null) {
            adjacent.setAdjacentState(1, true);
            this.setAdjacentState(0, true);
        }
        adjacent = bm.getChunk(new Vector3i(pos.x, pos.y, pos.z - 1));
        if (adjacent != null) {
            adjacent.setAdjacentState(2, true);
            this.setAdjacentState(3, true);
        }
        adjacent = bm.getChunk(new Vector3i(pos.x, pos.y, pos.z + 1));
        if (adjacent != null) {
            adjacent.setAdjacentState(3, true);
            this.setAdjacentState(2, true);
        }
        adjacent = bm.getChunk(new Vector3i(pos.x, pos.y - 1, pos.z));
        if (adjacent != null) {
            adjacent.setAdjacentState(4, true);
            this.setAdjacentState(5, true);
        }
        adjacent = bm.getChunk(new Vector3i(pos.x, pos.y + 1, pos.z));
        if (adjacent != null) {
            adjacent.setAdjacentState(5, true);
            this.setAdjacentState(4, true);
        }
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
        blockChunk = meshManager.blockManager.getChunk(pos);
        dirty = false;

        long start = System.nanoTime();

        var mesh = getMesh();
        if (mesh != null) {
            this.meshManager.removeVisibleChunk(this);
            mesh.cleanup();
        }
        MeshData m = new MeshData(this, 0);
        if (m.verts.length != 0) {
            mesh = new Mesh(m.verts, m.texCoords, m.normals, m.indices);
            mesh.setMaterial(meshManager.worldMaterial);
            setMesh(mesh);
            this.meshManager.addVisibleChunk(this);
        }
        else {
            setMesh(null);
        }

//        System.out.println("Chunk gen took " + (System.nanoTime() - start));
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
