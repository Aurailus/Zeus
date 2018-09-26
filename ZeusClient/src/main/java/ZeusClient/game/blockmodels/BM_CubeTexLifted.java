package ZeusClient.game.blockmodels;

import org.joml.Vector2i;

public class BM_CubeTexLifted extends BlockModel {

    public BM_CubeTexLifted(String texPosTop, String texPosSide, String texPosFloatSide, String texPosBottom) {
        var zPosBase = new MeshPart(
                new float[]{
                        0, 1, 1,
                        0, 0, 1,
                        1, 0, 1,
                        1, 1, 1,
                },
                new int[]{
                        0, 1, 3, 3, 1, 2,
                },
                new float[]{
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,
                },
                texPosSide);

        var zPosFloat = new MeshPart(
                new float[]{
                        0, 1, 1,
                        0, 0.2f, 1.2f,
                        1, 0.2f, 1.2f,
                        1, 1, 1,
                },
                new int[]{
                        0, 1, 3, 3, 1, 2,
                },
                new float[]{
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,
                },
                texPosFloatSide);

        zPosMP = new MeshPart[] {zPosBase, zPosFloat};

        var zNegBase = new MeshPart(
                new float[] {
                        0, 1, 0,
                        0, 0, 0,
                        1, 0, 0,
                        1, 1, 0,
                },
                new int[] {
                        3, 1, 0, 2, 1, 3,
                },
                new float[] {
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,
                },
                texPosSide);

        var zNegFloat = new MeshPart(
                new float[] {
                        0, 1, 0,
                        0, 0.2f, -0.2f,
                        1, 0.2f, -0.2f,
                        1, 1, 0,
                },
                new int[] {
                        3, 1, 0, 2, 1, 3,
                },
                new float[] {
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,
                },
                texPosFloatSide);

        zNegMP = new MeshPart[] {zNegBase, zNegFloat};

        yPosMP = new MeshPart[] {
            new MeshPart(
                    new float[]{
                            0, 1, 1,
                            0, 1, 0,
                            1, 1, 0,
                            1, 1, 1,
                    },
                    new int[]{
                            3, 1, 0, 2, 1, 3
                    },
                    new float[]{
                            0.0f, 0.0f,
                            0.0f, 1.0f,
                            1.0f, 1.0f,
                            1.0f, 0.0f,
                    },
                    texPosTop)
            };

        yNegMP = new MeshPart[]{
            new MeshPart(
                    new float[]{
                            0, 0, 1,
                            0, 0, 0,
                            1, 0, 0,
                            1, 0, 1,
                    },
                    new int[]{
                            0, 1, 3, 3, 1, 2
                    },
                    new float[]{
                            0.0f, 0.0f,
                            0.0f, 1.0f,
                            1.0f, 1.0f,
                            1.0f, 0.0f,
                    },
                    texPosBottom)
            };

        var xPosBase = new MeshPart(
                new float[] {
                        1, 1, 0,
                        1, 0, 0,
                        1, 0, 1,
                        1, 1, 1,
                },
                new int[] {
                        3, 1, 0, 2, 1, 3,
                },
                new float[] {
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,
                },
                texPosSide);

        var xPosFloat = new MeshPart(
                new float[] {
                        1, 1, 0,
                        1.2f, 0.2f, 0,
                        1.2f, 0.2f, 1,
                        1, 1, 1
                },
                new int[] {
                        3, 1, 0, 2, 1, 3,
                },
                new float[] {
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,
                },
                texPosFloatSide);

        xPosMP = new MeshPart[] {xPosBase, xPosFloat};

        var xNegBase = new MeshPart(
                new float[] {
                        0, 1, 0,
                        0, 0, 0,
                        0, 0, 1,
                        0, 1, 1,
                },
                new int[] {
                        0, 1, 3, 3, 1, 2,
                },
                new float[] {
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,
                },
                texPosSide);

        var xNegFloat = new MeshPart(
                new float[] {
                        0, 1, 0,
                        -0.2f, 0.2f, 0,
                        -0.2f, 0.2f, 1,
                        0, 1, 1
                },
                new int[] {
                        0, 1, 3, 3, 1, 2,
                },
                new float[] {
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,
                },
                texPosFloatSide);

        xNegMP = new MeshPart[] {xNegBase, xNegFloat};

    }
}
