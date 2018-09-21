package ZeusClient.game.blockmodels;

import org.joml.Vector2i;

public class BM_PlantLike extends BlockModel {

    public BM_PlantLike(String texPos) {
        noCulledMP = new MeshPart[] {new MeshPart(
            new float[] {
                    0.10f, 1, 0.10f,
                    0.10f, 0, 0.10f,
                    0.90f, 0, 0.90f,
                    0.90f, 1, 0.90f,

                    0.10f, 1, 0.10f,
                    0.10f, 0, 0.10f,
                    0.90f, 0, 0.90f,
                    0.90f, 1, 0.90f,

                    0.10f, 1, 0.90f,
                    0.10f, 0, 0.90f,
                    0.90f, 0, 0.10f,
                    0.90f, 1, 0.10f,

                    0.10f, 1, 0.90f,
                    0.10f, 0, 0.90f,
                    0.90f, 0, 0.10f,
                    0.90f, 1, 0.10f,
            },
            new int[] {
                    0, 1, 3, 3, 1, 2,
                    6, 5, 7, 7, 5, 4,
//                    4, 5, 7, 7, 5, 6,
                    8, 9, 11, 11, 9, 10,
                    14, 13, 15, 15, 13, 12
//                    12, 13, 15, 15, 13, 14,
            },
            new float[] {
                    0.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,

                    0.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,

                    0.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,

                    0.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,
            },
            texPos)};
    }
}
