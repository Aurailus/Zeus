package ZeusClient.game.blockmodels;

import ZeusClient.game.Game;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class MeshPart {
    public float[] positions;
    public float[] texData;
    public float[] normals;
    public int[]   indices;

    public MeshPart(float[] positions, int[] indices, float[] texCoords, String texName) {
        this.positions = positions;
        this.indices = indices;

        this.normals = new float[positions.length];

        Vector3f p1 = new Vector3f();
        Vector3f p2 = new Vector3f();
        Vector3f p3 = new Vector3f();
        Vector3f u = new Vector3f();
        Vector3f v = new Vector3f();
        Vector3f normal = new Vector3f();

        for (var i = 0; i < indices.length/3; i++) {

            int ind;
            ind = indices[i*3];
            p1.set(positions[ind*3], positions[ind*3+1], positions[ind*3+2]);

            ind = indices[i*3+1];
            p2.set(positions[ind*3], positions[ind*3+1], positions[ind*3+2]);

            ind = indices[i*3+2];
            p3.set(positions[ind*3], positions[ind*3+1], positions[ind*3+2]);

            u.set(p2).sub(p1);
            v.set(p3).sub(p1);

            normal.set(u).cross(v);

            ind = indices[i*3];
            this.normals[ind*3] = normal.x;
            this.normals[ind*3+1] = normal.y;
            this.normals[ind*3+2] = normal.z;
            ind = indices[i*3+1];
            this.normals[ind*3] = normal.x;
            this.normals[ind*3+1] = normal.y;
            this.normals[ind*3+2] = normal.z;
            ind = indices[i*3+2];
            this.normals[ind*3] = normal.x;
            this.normals[ind*3+1] = normal.y;
            this.normals[ind*3+2] = normal.z;
        }

        texData = new float[texCoords.length*2];

//        for (var i = 0; i < texCoords.length/2; i++) {
//            texData[i*4  ] = (float)(tex.x) / 128;
//            texData[i*4+1] = (float)(tex.y) / 128;
//            texData[i*4+2] = texCoords[i*2];
//            texData[i*4+3] = texCoords[i*2+1];
//        }

        Vector4f texUVs = Game.assets.getTexUV(texName);

        for (var i = 0; i < texCoords.length/2; i++) {
//            texData[i*4  ] = texUVs.x;
//            texData[i*4+1] = texUVs.y;
            texData[i*4  ] = texUVs.x + (texUVs.z - texUVs.x) * texCoords[i*2];
            texData[i*4+1] = texUVs.y + (texUVs.w - texUVs.y) * texCoords[i*2+1];
            texData[i*4+2] = 0;
            texData[i*4+3] = 0;
        }
    }
}
