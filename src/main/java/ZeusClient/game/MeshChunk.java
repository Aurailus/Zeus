package ZeusClient.game;

import ZeusClient.engine.RenderObj;
import ZeusClient.engine.graphics.ChunkMesh;
import ZeusClient.engine.graphics.Material;
import ZeusClient.engine.graphics.Texture;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.io.File;
import java.io.FileInputStream;

import static ZeusClient.engine.helpers.ArrayTrans3D.CHUNK_SIZE;

public class MeshChunk {
    private ChunkMesh mesh;
    private RenderObj obj;
    private Vector3i pos;

    public static Material meshMaterial;

    static {
        try {
            var atlas = new FileInputStream(new File("atlas_0.png"));
            meshMaterial = new Material(new Texture(atlas));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MeshChunk(Vector3i pos) {
        this.pos = pos;
        this.obj = new RenderObj(null);
        this.obj.setPosition(new Vector3f(pos.x * CHUNK_SIZE, pos.y * CHUNK_SIZE, pos.z * CHUNK_SIZE));
    }

    public void createMesh(BlockChunk chunk) {
//        long start = System.nanoTime();
        var meshData = new ChunkMeshBuilder(chunk);
//        System.out.println("Building: " + (System.nanoTime() - start));
        long start = System.nanoTime();
        if (meshData.verts.length > 0) {
            mesh = new ChunkMesh(meshData.verts, meshData.texCoords, meshData.normals, meshData.indices);
            mesh.material = meshMaterial;
//            if (System.nanoTime() - start > 1000000) System.out.println("Initializing: " + (System.nanoTime() - start));
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
