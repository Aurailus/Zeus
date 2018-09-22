package ZeusClient.game;

import ZeusClient.engine.RenderObj;
import ZeusClient.engine.graphics.ChunkMesh;
import ZeusClient.engine.graphics.Material;
import org.joml.Vector3f;
import org.joml.Vector3i;

import static ZeusClient.engine.helpers.ArrayTrans3D.CHUNK_SIZE;

public class MeshChunk {
    private ChunkMesh mesh;
    private RenderObj obj;
    private Vector3i pos;

    public static Material meshMaterial;

    public MeshChunk(Vector3i pos) {
        this.pos = pos;
        this.obj = new RenderObj(null);
        this.obj.setPosition(new Vector3f(pos.x * CHUNK_SIZE, pos.y * CHUNK_SIZE, pos.z * CHUNK_SIZE));
    }

    public void createMesh(BlockChunk chunk) {
        var meshData = new ChunkMeshBuilder(chunk);

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
