package Zeus.game.objects;

import Zeus.engine.RenderObj;
import Zeus.engine.SimplexNoise;
import Zeus.engine.Timer;
import Zeus.engine.graphics.Material;
import Zeus.engine.graphics.Mesh;
import Zeus.engine.graphics.Texture;
import Zeus.game.ZeusGame;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorldChunk extends RenderObj {
    private ZeusGame game;

    private Texture worldTex;
    private boolean[] adjacentChunksLoaded = {false, false, false, false, false, false};
    private boolean dirty;

    int x, y, z;

    static final int CHUNK_SIZE = 16;
    //Blocks is 1 dimensional but it is stored as x, y, then z
    private int[] blocks;

    private static final int NOISE_PRECISION = 40;
    private static final float NOISE_Y_MOD = 0.08f;

    public WorldChunk(Texture worldTex, ZeusGame game, int x, int y, int z) {
        super();
        this.setPosition(x * CHUNK_SIZE, y * CHUNK_SIZE, z * CHUNK_SIZE);
        this.game = game;
        this.dirty = true;

        this.x = x;
        this.y = y;
        this.z = z;

        this.game.scene.addWorldChunk(new Vector3i(x, y, z), this);

        this.worldTex = worldTex;
        blocks = new int[CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE];
        for (var i = 0; i < CHUNK_SIZE; i++) {
            for (var j = 0; j < CHUNK_SIZE; j++) {
                for (var k = 0; k < CHUNK_SIZE; k++) {
                    int fill = 0;

                    double noiseVal = SimplexNoise.noise(((double)i + x * CHUNK_SIZE) / NOISE_PRECISION, ((double)j + y * CHUNK_SIZE)  / NOISE_PRECISION, ((double)k + z * CHUNK_SIZE) / NOISE_PRECISION);
                    if (noiseVal - NOISE_Y_MOD * (j + (y-1) * CHUNK_SIZE) > 0) fill = 1;

                    setBlock(fill, i, j, k);
                }
            }
        }

        var mesh = new Mesh(new float[] {}, new float[] {}, new float[] {}, new int[] {});
        mesh.setMaterial(new Material(new Vector4f(0, 0, 0, 0), 0));
        setMesh(mesh);

        WorldChunk adjacent;
        adjacent = game.scene.getWorldChunk(new Vector3i(x - 1, y, z));
        if (adjacent != null) {
            adjacent.setAdjacentState(0, true);
            this.setAdjacentState(1, true);
        }
        adjacent = game.scene.getWorldChunk(new Vector3i(x + 1, y, z));
        if (adjacent != null) {
            adjacent.setAdjacentState(1, true);
            this.setAdjacentState(0, true);
        }
        adjacent = game.scene.getWorldChunk(new Vector3i(x, y, z - 1));
        if (adjacent != null) {
            adjacent.setAdjacentState(2, true);
            this.setAdjacentState(3, true);
        }
        adjacent = game.scene.getWorldChunk(new Vector3i(x, y, z + 1));
        if (adjacent != null) {
            adjacent.setAdjacentState(3, true);
            this.setAdjacentState(2, true);
        }
        adjacent = game.scene.getWorldChunk(new Vector3i(x, y - 1, z));
        if (adjacent != null) {
            adjacent.setAdjacentState(4, true);
            this.setAdjacentState(5, true);
        }
        adjacent = game.scene.getWorldChunk(new Vector3i(x, y + 1, z));
        if (adjacent != null) {
            adjacent.setAdjacentState(5, true);
            this.setAdjacentState(4, true);
        }
    }

    int getBlock(int x, int y, int z) {
        int chunkXOffset = 0, chunkYOffset = 0, chunkZOffset = 0;
        if (x < 0 || x >= CHUNK_SIZE || y < 0 || y >= CHUNK_SIZE || z < 0 || z >= CHUNK_SIZE) {
            if (x < 0) {
                x += CHUNK_SIZE;
                chunkXOffset -= 1;
            }
            if (x >= CHUNK_SIZE) {
                x -= CHUNK_SIZE;
                chunkXOffset += 1;
            }
            if (y < 0) {
                y += CHUNK_SIZE;
                chunkYOffset -= 1;
            }
            if (y >= CHUNK_SIZE) {
                y -= CHUNK_SIZE;
                chunkYOffset += 1;
            }
            if (z < 0) {
                z += CHUNK_SIZE;
                chunkZOffset -= 1;
            }
            if (z >= CHUNK_SIZE) {
                z -= CHUNK_SIZE;
                chunkZOffset += 1;
            }
            var chunkPos = new Vector3i(this.x + chunkXOffset, this.y + chunkYOffset, this.z + chunkZOffset);
            var chunk = game.scene.getWorldChunk(chunkPos);
            if (chunk != null) return chunk.getBlock(x, y, z);
            else return 1;

        }
        return blocks[x + CHUNK_SIZE * (y + CHUNK_SIZE * z)];
    }

    void setBlock(int block, int x, int y, int z) {
        blocks[x + CHUNK_SIZE * (y + CHUNK_SIZE * z)] = block;
    }

    //0: X+
    //1: X-
    //2: Z+
    //3: Z-
    //4: Y+
    //5: Y-
    public void setAdjacentState(int adjInd, boolean state) {
        adjacentChunksLoaded[adjInd] = state;
        if (state && dirty) {
            var canRender = true;
            for (var i = 0; i < adjacentChunksLoaded.length; i++) {
                if (!adjacentChunksLoaded[i]) {
                    canRender = false;
                    break;
                }
            }
            if (canRender) {
                updateMesh();
            }
        }
    }

    public void updateMesh() {
        long start = System.nanoTime();
        dirty = false;

        this.getMesh().cleanup();
        MeshData m = new MeshData(this);
        var mesh = new Mesh(m.verts, m.texCoords, m.normals, m.indices);
        mesh.setMaterial(new Material(worldTex));
        setMesh(mesh);

        System.out.println("Updating chunk took: " + ((double)(System.nanoTime() - start) / 1000000));
    }
}

class MeshData {

    float[] verts;
    float[] texCoords;
    float[] normals;
    int[] indices;

    private List<Float> vertsList;
    private List<Float> texCoordsList;
    private List<Float> normalsList;
    private List<Integer> indicesList;

    private void addFace(Face face, Vector3f offset, int faceNum) {
        for (var i = 0; i < 4; i++) {
            vertsList.add(face.pos[i*3] + offset.x);
            vertsList.add(face.pos[i*3+1] + offset.y);
            vertsList.add(face.pos[i*3+2] + offset.z);
        }
        for (var i = 0; i < 4; i++) {
            normalsList.add(face.normal[0]);
            normalsList.add(face.normal[1]);
            normalsList.add(face.normal[2]);
        }
        for (var i = 0; i < 8; i++) {
            texCoordsList.add(face.texCoords[i]);
        }
        for (var i = 0; i < 6; i++) {
            indicesList.add(face.indiceOrder[i] + (faceNum*4));
        }
    }

    MeshData(WorldChunk chunk) {
        vertsList = new ArrayList<>();
        texCoordsList = new ArrayList<>();
        normalsList = new ArrayList<>();
        indicesList = new ArrayList<>();

        var faceNum = 0;
        var offset = new Vector3f(0, 0, 0);

        for (var i = 0; i < WorldChunk.CHUNK_SIZE; i++) {
            offset.x = i;
            for (var j = 0; j < WorldChunk.CHUNK_SIZE; j++) {
                offset.z = j;
                for (var k = 0; k < WorldChunk.CHUNK_SIZE; k++) {
                    offset.y = k;
                    if (chunk.getBlock(i, k, j) == 1) {
                        if (chunk.getBlock(i, k, j - 1) == 0) {
                            addFace(northFace, offset, faceNum++);
                        }
                        if (chunk.getBlock(i, k, j + 1) == 0) {
                            addFace(southFace, offset, faceNum++);
                        }
                        if (chunk.getBlock(i, k + 1, j) == 0) {
                            addFace(topFace, offset, faceNum++);
                        }
                        if (chunk.getBlock(i, k - 1, j) == 0) {
                            addFace(bottomFace, offset, faceNum++);
                        }
                        if (chunk.getBlock(i + 1, k, j) == 0) {
                            addFace(eastFace, offset, faceNum++);
                        }
                        if (chunk.getBlock(i - 1, k, j) == 0) {
                            addFace(westFace, offset, faceNum++);
                        }
//
//                        addFace(northFace, offset, faceNum++);
//                        addFace(southFace, offset, faceNum++);
//                        addFace(topFace, offset, faceNum++);
//                        addFace(bottomFace, offset, faceNum++);
//                        addFace(eastFace, offset, faceNum++);
//                        addFace(westFace, offset, faceNum++);
                    }
                }
            }
        }

        verts = new float[vertsList.size()];
        for (var i = 0; i < vertsList.size(); i++) {
            verts[i] = vertsList.get(i);
        }
        vertsList.clear();

        texCoords = new float[texCoordsList.size()];
        for (var i = 0; i < texCoordsList.size(); i++) {
            texCoords[i] = texCoordsList.get(i);
        }
        texCoordsList.clear();

        normals = new float[normalsList.size()];
        for (var i = 0; i < normalsList.size(); i++) {
            normals[i] = normalsList.get(i);
        }
        normalsList.clear();

        indices = new int[indicesList.size()];
        for (var i = 0; i < indicesList.size(); i++) {
            indices[i] = indicesList.get(i);
        }
        indicesList.clear();
    }

    private static class Face {
        float[] pos;
        float[] texCoords;
        float[] normal;
        int[] indiceOrder;

        Face(float[] pos, float[] texCoords, float[] normal, int[] indiceOrder) {
            this.pos = pos;
            this.texCoords = texCoords;
            this.normal = normal;
            this.indiceOrder = indiceOrder;
        }
    }

    private static Face southFace = new Face(
            new float[] {
                    -0.5f, 0.5f, 0.5f,
                    -0.5f, -0.5f, 0.5f,
                    0.5f, -0.5f, 0.5f,
                    0.5f, 0.5f, 0.5f,
            },
            new float[] {
                    0.0f, 0.0f,
                    0.0f, 0.5f,
                    0.5f, 0.5f,
                    0.5f, 0.0f,
            },
            new float[] {
                    0f, 0f, 1f
            },
            new int[] {
                    0, 1, 3, 3, 1, 2
            }
    );

    private static Face northFace = new Face(
            new float[] {
                    -0.5f, 0.5f, -0.5f,
                    -0.5f, -0.5f, -0.5f,
                    0.5f, -0.5f, -0.5f,
                    0.5f, 0.5f, -0.5f,
            },
            new float[] {
                    0.0f, 0.0f,
                    0.0f, 0.5f,
                    0.5f, 0.5f,
                    0.5f, 0.0f,
            },
            new float[] {
                    0f, 0f, -1f
            },
            new int[] {
                    3, 1, 0, 2, 1, 3
            }
    );

    private static Face topFace = new Face(
            new float[] {
                    -0.5f, 0.5f, 0.5f,
                    -0.5f, 0.5f, -0.5f,
                    0.5f, 0.5f, -0.5f,
                    0.5f, 0.5f, 0.5f,
            },
            new float[] {
                    0.0f, 0.5f,
                    0.0f, 1.0f,
                    0.5f, 1.0f,
                    0.5f, 0.5f,
            },
            new float[] {
                    0f, 1f, 0f
            },
            new int[] {
                    3, 1, 0, 2, 1, 3
            }
    );

    private static Face bottomFace = new Face(
            new float[] {
                    -0.5f, -0.5f, 0.5f,
                    -0.5f, -0.5f, -0.5f,
                    0.5f, -0.5f, -0.5f,
                    0.5f, -0.5f, 0.5f,
            },
            new float[] {
                    0.5f, 0.0f,
                    0.5f, 0.5f,
                    1.0f, 0.5f,
                    1.0f, 0.0f,
            },
            new float[] {
                    0f, -1f, 0f
            },
            new int[] {
                    0, 1, 3, 3, 1, 2
            }
    );

    private static Face eastFace = new Face(
            new float[] {
                    0.5f, 0.5f, -0.5f,
                    0.5f, -0.5f, -0.5f,
                    0.5f, -0.5f, 0.5f,
                    0.5f, 0.5f, 0.5f,
            },
            new float[] {
                    0.0f, 0.0f,
                    0.0f, 0.5f,
                    0.5f, 0.5f,
                    0.5f, 0.0f,
            },
            new float[] {
                    1f, 0f, 0f
            },
            new int[] {
                    3, 1, 0, 2, 1, 3
            }
    );

    private static Face westFace = new Face(
            new float[] {
                    -0.5f, 0.5f, -0.5f,
                    -0.5f, -0.5f, -0.5f,
                    -0.5f, -0.5f, 0.5f,
                    -0.5f, 0.5f, 0.5f,
            },
            new float[] {
                    0.0f, 0.0f,
                    0.0f, 0.5f,
                    0.5f, 0.5f,
                    0.5f, 0.0f,
            },
            new float[] {
                    -1f, 0f, 0f
            },
            new int[] {
                    0, 1, 3, 3, 1, 2
            }
    );
}
