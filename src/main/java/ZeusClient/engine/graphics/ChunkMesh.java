package ZeusClient.engine.graphics;

import ZeusClient.engine.RenderObj;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
    private Material material;

    public ChunkMesh(float[] positions, float[] texCoords, float[] normals, int[] indices) {
        FloatBuffer posBuffer = null;
        FloatBuffer texCoordsBuffer = null;
        FloatBuffer normalsBuffer = null;
        IntBuffer indicesBuffer = null;

        /*
            A VAO (Vertex Array Object) is an "array" on the graphics card of VBOs.
            A VBO (Vertex Buffer Object) is a list of values (usually floats) on the
                graphics card which correlates to an IN value in the vertex shader,
                which can store any arbitrary value but is referenced by the indices
                buffer. I.E. Any values in any VBO are sent per-index to the shader for use.
         */

        try {
            vertexCount = indices.length;
            vboIDList = new ArrayList<>();

            vaoID = glGenVertexArrays();
            glBindVertexArray(vaoID);


            long buffTime = System.nanoTime();
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            posBuffer.put(positions).flip();
            texCoordsBuffer = MemoryUtil.memAllocFloat(texCoords.length);
            texCoordsBuffer.put(texCoords).flip();
            normalsBuffer = MemoryUtil.memAllocFloat(normals.length);
            normalsBuffer.put(normals).flip();
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();

            buffTime = System.nanoTime() - buffTime;

            long allocTime = System.nanoTime();
            //Position VBO
            int vboID = glGenBuffers();
            vboIDList.add(vboID);
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            //TexCoord VBO
            vboID = glGenBuffers();
            vboIDList.add(vboID);
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferData(GL_ARRAY_BUFFER, texCoordsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, 0);

            //Vertex Normals VBO
            vboID = glGenBuffers();
            vboIDList.add(vboID);
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

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
            if (posBuffer != null) {
                MemoryUtil.memFree(posBuffer);
            }
            if (texCoordsBuffer != null) {
                MemoryUtil.memFree(texCoordsBuffer);
            }
            if (normalsBuffer != null) {
                MemoryUtil.memFree(normalsBuffer);
            }
            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);
            }
        }
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getVaoID() {
        return vaoID;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    private void initRender() {
        Texture texture = material.getTexture();
        if (texture != null) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture.getID());
        }

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

        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void render() {
        initRender();
        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
        endRender();
    }

    public void renderList(List<RenderObj> list, Consumer<RenderObj> consumer) {
        initRender();

        for (RenderObj obj : list) {
            consumer.accept(obj);
            glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
        }

        endRender();
    }

    public void cleanup() {
        glDisableVertexAttribArray(0);

        //Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboID : vboIDList) {
            glDeleteBuffers(vboID);
        }

        //Delete the texture
        Texture texture = material.getTexture();
        if (texture != null) {
            texture.cleanup();
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
