package Zeus.game;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class MeshData {
    private static float[] resScale = {1, 0.5f, 0.25f, 0.125f, 0.0625f};

    public float[] verts;
    public float[] texCoords;
    public float[] normals;
    public int[] indices;

    private List<Float> vertsList;
    private List<Float> texCoordsList;
    private List<Float> normalsList;
    private List<Integer> indicesList;

    private MeshChunk chunk;

    private int resolution;
    private int[][][] blocks;


    private void addFace(Face face, Vector3f offset, int faceNum) {
        float scale = 1 / resScale[resolution];
        for (var i = 0; i < 4; i++) {
            vertsList.add(face.pos[i*3]   * scale + offset.x * scale);
            vertsList.add(face.pos[i*3+1] * scale + offset.y * scale);
            vertsList.add(face.pos[i*3+2] * scale + offset.z * scale);
        }
        for (var i = 0; i < 4; i++) {
            normalsList.add(face.normal[0]);
            normalsList.add(face.normal[1]);
            normalsList.add(face.normal[2]);
        }
        for (var i = 0; i < 16; i++) {
            texCoordsList.add(face.texCoords[i]);
        }
        for (var i = 0; i < 6; i++) {
            indicesList.add(face.indiceOrder[i] + (faceNum*4));
        }
    }

    private boolean faceShouldRender(int x, int y, int z) {
//        if (x < 0 || y < 0 || z < 0 || x >= blocks.length || y >= blocks.length || z >= blocks.length)
//            return 0;
//            return chunk.blockChunk.blockManager.getBlock(
//                    chunk.blockChunk.getWorldCoords(
//                    Math.round(x / resScale[resolution]), Math.round(y / resScale[resolution]), Math.round(z / resScale[resolution]))) == 0;

        return getBlock(x, y, z) == 0;
    }

    private int getBlock(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0 || x >= blocks.length || y >= blocks.length || z >= blocks.length)
            return 0;
//            return chunk.blockChunk.blockManager.getBlock(
//                    chunk.blockChunk.getWorldCoords(
//                            Math.round(x / resScale[resolution]), Math.round(y / resScale[resolution]), Math.round(z / resScale[resolution])));

        return blocks[x][y][z];
    }

    public MeshData(MeshChunk chunk, BlockChunk b, int resolution) {
        vertsList = new ArrayList<>();
        texCoordsList = new ArrayList<>();
        normalsList = new ArrayList<>();
        indicesList = new ArrayList<>();

        this.chunk = chunk;
        this.resolution = resolution;

        final int ARRAY_SIZE = Math.round(MeshChunk.CHUNK_SIZE * resScale[resolution]);

        blocks = new int[ARRAY_SIZE][ARRAY_SIZE][ARRAY_SIZE];

        for (var i = 0; i < ARRAY_SIZE; i++) {
            for (var j = 0; j < ARRAY_SIZE; j++) {
                for (var k = 0; k < ARRAY_SIZE; k++) {
                    blocks[i][j][k] = b.getBlock(Math.round(i / resScale[resolution]), Math.round(j / resScale[resolution]), Math.round(k / resScale[resolution]));
                }
            }
        }

        var faceNum = 0;
        var offset = new Vector3f(0, 0, 0);

        for (var i = 0; i < ARRAY_SIZE; i++) {
            offset.x = i;
            for (var j = 0; j < ARRAY_SIZE; j++) {
                offset.z = j;
                for (var k = 0; k < ARRAY_SIZE; k++) {
                    offset.y = k;
                    if (getBlock(i, k, j) == 1) {
                        if (faceShouldRender(i, k, j - 1)) {
                            addFace(northFace, offset, faceNum++);
                        }
                        if (faceShouldRender(i, k, j + 1)) {
                            addFace(southFace, offset, faceNum++);
                        }
                        if (faceShouldRender(i, k + 1, j)) {
                            addFace(topFace, offset, faceNum++);
                        }
                        if (faceShouldRender(i, k - 1, j)) {
                            addFace(bottomFace, offset, faceNum++);
                        }
                        if (faceShouldRender(i + 1, k, j)) {
                            addFace(eastFace, offset, faceNum++);
                        }
                        if (faceShouldRender(i - 1, k, j)) {
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

        blocks = null;
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
                    0, 1, 1,
                    0, 0, 1,
                    1, 0, 1,
                    1, 1, 1,
            },
            new float[] {
                    0.0f, 0.0f, 0.0f, 0.0f,
                    0.0f, 0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 0.0f,
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
                    0, 1, 0,
                    0, 0, 0,
                    1, 0, 0,
                    1, 1, 0,
            },
            new float[] {
                    0.0f, 0.0f, 0.0f, 0.0f,
                    0.0f, 0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 0.0f,
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
                    0, 1, 1,
                    0, 1, 0,
                    1, 1, 0,
                    1, 1, 1,
            },
            new float[] {
                    0.0f, 0.5f, 0.0f, 0.0f,
                    0.0f, 0.5f, 0.0f, 1.0f,
                    0.0f, 0.5f, 1.0f, 1.0f,
                    0.0f, 0.5f, 1.0f, 0.0f,
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
                    0, 0, 1,
                    0, 0, 0,
                    1, 0, 0,
                    1, 0, 1,
            },
            new float[] {
                    0.5f, 0.0f, 0.0f, 0.0f,
                    0.5f, 0.0f, 0.0f, 1.0f,
                    0.5f, 0.0f, 1.0f, 1.0f,
                    0.5f, 0.0f, 1.0f, 0.0f,
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
                    1, 1, 0,
                    1, 0, 0,
                    1, 0, 1,
                    1, 1, 1,
            },
            new float[] {
                    0.0f, 0.0f, 0.0f, 0.0f,
                    0.0f, 0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 0.0f,
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
                    0, 1, 0,
                    0, 0, 0,
                    0, 0, 1,
                    0, 1, 1,
            },
            new float[] {
                    0.0f, 0.0f, 0.0f, 0.0f,
                    0.0f, 0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 0.0f,
            },
            new float[] {
                    -1f, 0f, 0f
            },
            new int[] {
                    0, 1, 3, 3, 1, 2
            }
    );
}
