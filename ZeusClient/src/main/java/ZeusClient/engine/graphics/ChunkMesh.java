package ZeusClient.engine.graphics;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;


public class ChunkMesh {
    private int vaoID;
    private List<Integer> vboIDList;
    private int vertexCount;
    public Material material;

//    private FloatBuffer interleavedBuffer; //Todo: Somehow check if this is causing occasional core dumps
//    private IntBuffer indicesBuffer;

    private float[] interleaved;
    private int[] indices;

    public ChunkMesh(float[] positions, float[] texCoords, float[] normals, int[] indices) {
        vertexCount = indices.length;
        vboIDList = new ArrayList<>();

        this.indices = indices;

        long buffTime = System.nanoTime();

        interleaved = new float[positions.length + texCoords.length + normals.length];
        int ind = 0;

        for (var i = 0; i < positions.length / 3; i++) {
            interleaved[ind++] = positions[i*3];
            interleaved[ind++] = positions[i*3+1];
            interleaved[ind++] = positions[i*3+2];

            interleaved[ind++] = normals[i*3];
            interleaved[ind++] = normals[i*3+1];
            interleaved[ind++] = normals[i*3+2];

            interleaved[ind++] = texCoords[i*2];
            interleaved[ind++] = texCoords[i*2+1];
        }

        buffTime = System.nanoTime() - buffTime;
    }

    public void init() {
        long allocTime = System.nanoTime();

        FloatBuffer interleavedBuffer = MemoryUtil.memAllocFloat(interleaved.length).put(interleaved).flip();
        interleaved = null;
        IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indices.length).put(indices).flip();
        indices = null;

        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        int vboID = glGenBuffers();
        vboIDList.add(vboID);

        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, interleavedBuffer, GL_STATIC_DRAW);

        int stride = (3 + 3 + 2) * 4;

        //Position VBO
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);

        //Normals VBO
        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3 * 4);

        //TexCoord VBO
        glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 6 * 4);

        //Index VBO
        vboID = glGenBuffers();
        vboIDList.add(vboID);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        allocTime = System.nanoTime() - allocTime;
//        System.out.println((float)allocTime / 1_000_000);

        MemoryUtil.memFree(interleavedBuffer);
        MemoryUtil.memFree(indicesBuffer);
    }

    public int getVaoID() {
        return vaoID;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void initChunksRender() {
        Texture texture = material.getTexture();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture.getID());
    }

    public void endChunksRender() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    private void initRender() {

        //Draw the mesh
        glBindVertexArray(getVaoID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
    }

    private void endRender() {
        //Restore State
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
    }

    public void render() {
        initRender();
        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
        endRender();
    }

    public void cleanup() {
        glDisableVertexAttribArray(0);

        //Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboID : vboIDList) {
            glDeleteBuffers(vboID);
        }

        glBindVertexArray(0);
        glDeleteBuffers(vaoID);
    }

    public void deleteBuffers() {
        glDisableVertexAttribArray(0);

        //Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboID : vboIDList) {
            glDeleteBuffers(vboID);
        }

        //Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoID);
    }
}
