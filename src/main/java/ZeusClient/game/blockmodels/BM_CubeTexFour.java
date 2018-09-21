package ZeusClient.game.blockmodels;

import org.joml.Vector2i;

public class BM_CubeTexFour extends BlockModel {

    public BM_CubeTexFour(String texPosTop, String texPosSide, String texPosBottom) {
        zPosMP = new MeshPart[] {new MeshPart(
            new float[] {
                0, 1, 1,
                0, 0, 1,
                1, 0, 1,
                1, 1, 1,
            },
            new int[] {
                0, 1, 3, 3, 1, 2
            },
            new float[] {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
            },
            texPosSide)};

        zNegMP = new MeshPart[] {new MeshPart(
            new float[] {
                0, 1, 0,
                0, 0, 0,
                1, 0, 0,
                1, 1, 0,
            },
            new int[] {
                3, 1, 0, 2, 1, 3
            },
            new float[] {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
            },
            texPosSide)};

        yPosMP = new MeshPart[] {new MeshPart(
            new float[] {
                0, 1, 1,
                0, 1, 0,
                1, 1, 0,
                1, 1, 1,
            },
            new int[] {
                3, 1, 0, 2, 1, 3
            },
            new float[] {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
            },
            texPosTop)};

        yNegMP = new MeshPart[] {new MeshPart(
            new float[] {
                0, 0, 1,
                0, 0, 0,
                1, 0, 0,
                1, 0, 1,
            },
            new int[] {
                0, 1, 3, 3, 1, 2
            },
            new float[] {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
            },
            texPosBottom)};

        xPosMP = new MeshPart[] {new MeshPart(
            new float[] {
                1, 1, 0,
                1, 0, 0,
                1, 0, 1,
                1, 1, 1,
            },
            new int[] {
                3, 1, 0, 2, 1, 3
            },
            new float[] {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
            },
            texPosSide)};

        xNegMP = new MeshPart[] {new MeshPart(
            new float[] {
                0, 1, 0,
                0, 0, 0,
                0, 0, 1,
                0, 1, 1,
            },
            new int[] {
                0, 1, 3, 3, 1, 2
            },
            new float[] {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
            },
            texPosSide)};
    }
}
