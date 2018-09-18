package ZeusClient.game;

import ZeusClient.engine.helpers.ArrayTrans3D;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

public class MeshData {
    public float[] verts;
    public float[] texCoords;
    public float[] normals;
    public int[] indices;

    private List<Float> vertsList;
    private List<Float> texCoordsList;
    private List<Float> normalsList;
    private List<Integer> indicesList;

    private void addFace(Face face, Vector3i offset, int faceNum) {
        float scale = 1;

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

    public MeshData(BlockChunk blockChunk) {

        vertsList = new ArrayList<>();
        texCoordsList = new ArrayList<>();
        normalsList = new ArrayList<>();
        indicesList = new ArrayList<>();

        var faceNum = 0;
        Vector3i offset = new Vector3i(0, 0, 0);

        for (var i = 0; i < blockChunk.getVisibleArray().length; i++) {
            ArrayTrans3D.indToVec(i, offset); //Set offset

            if (blockChunk.getVisible(offset)) {
                var adj = blockChunk.getAdjacent(offset);

                if (adj[0] == 0) { //X Pos
                    addFace(eastFace, offset, faceNum++);
                }
                if (adj[1] == 0) { //X Neg
                    addFace(westFace, offset, faceNum++);
                }
                if (adj[2] == 0) { //Y Pos
                    addFace(topFace, offset, faceNum++);
                }
                if (adj[3] == 0) { //Y Neg
                    addFace(bottomFace, offset, faceNum++);
                }
                if (adj[4] == 0) { //Z Pos
                    addFace(southFace, offset, faceNum++);
                }
                if (adj[5] == 0) { //Z Neg
                    addFace(northFace, offset, faceNum++);
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
