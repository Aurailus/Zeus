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
    private final int vaoID;
    private final List<Integer> vboIDList;
    private final int vertexCount;
    public Material material;

    public ChunkMesh(float[] positions, float[] texCoords, float[] normals, int[] indices) {
        FloatBuffer interleavedBuffer = null;
        IntBuffer indicesBuffer = null;

        try {
            vertexCount = indices.length;
            vboIDList = new ArrayList<>();

            vaoID = glGenVertexArrays();
            glBindVertexArray(vaoID);

            long buffTime = System.nanoTime();

            interleavedBuffer = MemoryUtil.memAllocFloat(positions.length + texCoords.length + normals.length);

            indicesBuffer = MemoryUtil.memAllocInt(indices.length).put(indices).flip();

            for (var i = 0; i < positions.length / 3; i++) {
                interleavedBuffer.put(positions[i*3]);
                interleavedBuffer.put(positions[i*3 + 1]);
                interleavedBuffer.put(positions[i*3 + 2]);

                interleavedBuffer.put(normals[i*3]);
                interleavedBuffer.put(normals[i*3 + 1]);
                interleavedBuffer.put(normals[i*3 + 2]);

                interleavedBuffer.put(texCoords[i*2]);
                interleavedBuffer.put(texCoords[i*2 + 1]);
            }

            interleavedBuffer.flip();

            buffTime = System.nanoTime() - buffTime;

            long allocTime = System.nanoTime();

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

            System.out.println(buffTime + " | " + allocTime);
        }
        finally {
            //Release the memory allocated for the mesh after creating it.
            if (interleavedBuffer != null) {
                MemoryUtil.memFree(interleavedBuffer);
            }
            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);
            }
        }
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
