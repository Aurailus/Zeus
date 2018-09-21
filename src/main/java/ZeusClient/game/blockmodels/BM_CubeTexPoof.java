package ZeusClient.game.blockmodels;

public class BM_CubeTexPoof extends BlockModel {

    public BM_CubeTexPoof(String texBase, String texPoof) {
        noCulledMP = new MeshPart[] {new MeshPart(
        new float[] {
                -0.16f, 1.30f, -0.14f,
                -0.16f, -0.30f, -0.16f,
                1.15f, -0.30f, 1.15f,
                1.15f, 1.30f, 1.14f,

                -0.16f, 1.30f, -0.14f,
                -0.16f, -0.30f, -0.16f,
                1.15f, -0.30f, 1.15f,
                1.15f, 1.30f, 1.14f,

                -0.16f, 1.30f, 1.14f,
                -0.16f, -0.30f, 1.15f,
                1.15f, -0.30f, -0.16f,
                1.15f, 1.30f, -0.14f,

                -0.16f, 1.30f, 1.14f,
                -0.16f, -0.30f, 1.15f,
                1.15f, -0.30f, -0.16f,
                1.15f, 1.30f, -0.14f,
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
        texPoof)};

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
                texBase)};

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
                texBase)};

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
                texBase)};

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
                texBase)};

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
                texBase)};

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
                texBase)};
    }
}
