package client.game;

import client.engine.RenderObj;
import client.engine.graphics.ChunkMesh;
import client.engine.graphics.Material;
import org.joml.Vector3f;
import org.joml.Vector3i;

import static helpers.ArrayTrans3D.CHUNK_SIZE;

public class MeshChunk {
    private World world;
    private ChunkMesh mesh;
    private RenderObj obj;
    private Vector3i pos;

    public static Material meshMaterial;

    public MeshChunk(Vector3i pos, World world) {
        this.world = world;
        this.pos = pos;
        this.obj = new RenderObj(null);
        this.obj.setPosition(new Vector3f(pos.x * CHUNK_SIZE, pos.y * CHUNK_SIZE, pos.z * CHUNK_SIZE));
    }

    //This method is usually called from a GenChunkTask, don't do any OGL methods here.
    public void createMesh(BlockChunk chunk) {
        BlockChunk[] adjacent = new BlockChunk[6];
        adjacent[0] = world.getChunk(new Vector3i(pos.x + 1, pos.y, pos.z));
        adjacent[1] = world.getChunk(new Vector3i(pos.x - 1, pos.y, pos.z));
        adjacent[2] = world.getChunk(new Vector3i(pos.x, pos.y + 1, pos.z));
        adjacent[3] = world.getChunk(new Vector3i(pos.x, pos.y - 1, pos.z));
        adjacent[4] = world.getChunk(new Vector3i(pos.x, pos.y, pos.z + 1));
        adjacent[5] = world.getChunk(new Vector3i(pos.x, pos.y, pos.z - 1));

        var meshData = new ChunkMeshBuilder(chunk, adjacent);

        if (meshData.verts.length > 0) {
            mesh = new ChunkMesh(meshData.verts, meshData.texCoords, meshData.normals, meshData.indices);
            mesh.material = meshMaterial;
        }
    }

    public ChunkMesh getMesh() {
        return mesh;
    }

    public Vector3i getPos() {
        return pos;
    }

    public RenderObj getObject() {
        return obj;
    }
}
