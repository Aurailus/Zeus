package client.game.blockmodels;

import client.game.Game;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class MeshPart {
    private float[] positions;
    private float[] texData;
    private float[] normals;
    private int[]   indices;

    private MeshMod meshMod;
    private float   modVal;

    public MeshPart(float[] positions, int[] indices, float[] texCoords, String texName) {
        this(positions, indices, texCoords, texName, MeshMod.NONE, 0);
    }

    public MeshPart(float[] positions, int[] indices, float[] texCoords, String texName, MeshMod meshMod, float modVal) {
        this.meshMod = meshMod;
        this.modVal = modVal;

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

        texData = new float[texCoords.length];

        Vector4f texUVs = Game.assets.getTexUV(texName);

        for (var i = 0; i < texCoords.length/2; i++) {
            texData[i*2  ] = texUVs.x + (texUVs.z - texUVs.x) * texCoords[i*2];
            texData[i*2+1] = texUVs.y + (texUVs.w - texUVs.y) * texCoords[i*2+1];
        }
    }

    public MeshInfo getMeshData(MeshInfo m) {
        m.indices = indices;
        m.normals = normals;
        m.texData = texData;

        if (meshMod == MeshMod.NONE) m.positions = positions;
        else if (meshMod == MeshMod.SHIFT) {
            float shift_x = -modVal + (float)Math.random() * modVal * 2f;
            float shift_z = -modVal + (float)Math.random() * modVal * 2f;

            m.positions = new float[positions.length];
            for (int i = 0; i < positions.length/3; i++) {
                m.positions[i*3] = positions[i*3] + shift_x;
                m.positions[i*3+1] = positions[i*3+1];
                m.positions[i*3+2] = positions[i*3+2] + shift_z;
            }
        }

        return m;
    }
}
