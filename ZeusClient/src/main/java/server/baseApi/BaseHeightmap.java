package server.baseApi;

import server.api.IMapHeightmap;
import helpers.OpenSimplexNoise;

import static server.server.MapGen.CHUNK_SIZE;

public class BaseHeightmap implements IMapHeightmap {
    long seed;

    private OpenSimplexNoise terrainNoise;

    public BaseHeightmap(long seed) {
        this.seed = seed;

        terrainNoise = new OpenSimplexNoise(seed);
    }

    @Override
    public int getHeight(int x, int z) {
        float[] points = getLerpPoints(x, z);
        return getHeight(x, z, points);
    }

    @Override
    public int[][] getChunkHeightmap(int chunkX, int chunkZ) {

        float[] points = getLerpPoints(chunkX*CHUNK_SIZE, chunkZ*CHUNK_SIZE);

        int[][] heightMap = new int[CHUNK_SIZE][CHUNK_SIZE];

        for (var i = 0; i < 8; i++) {
            for (var k = 0; k < 8; k++) {
                heightMap[i][k] = getHeight(i + chunkX*CHUNK_SIZE, k + chunkZ*CHUNK_SIZE, new float[] {points[0], points[1], points[2], points[3]});
            }
        }

        for (var i = 8; i < 16; i++) {
            for (var k = 0; k < 8; k++) {
                heightMap[i][k] = getHeight(i + chunkX*CHUNK_SIZE, k + chunkZ*CHUNK_SIZE, new float[] {points[1], points[4], points[3], points[5]});
            }
        }

        for (var i = 0; i < 8; i++) {
            for (var k = 8; k < 16; k++) {
                heightMap[i][k] = getHeight(i + chunkX*CHUNK_SIZE, k + chunkZ*CHUNK_SIZE, new float[] {points[2], points[3], points[6], points[7]});
            }
        }

        for (var i = 8; i < 16; i++) {
            for (var k = 8; k < 16; k++) {
                heightMap[i][k] = getHeight(i + chunkX*CHUNK_SIZE, k + chunkZ*CHUNK_SIZE, new float[] {points[3], points[5], points[7], points[8]});
            }
        }

        return heightMap;
    }


    private int getHeight(int x, int z, float[] points) {

        int xx = (int)Math.floor((float)x/8);
        int zz = (int)Math.floor((float)z/8);

        float v = ((float)x/8) - xx;
        float u = ((float)z/8) - zz;

        float x1 = lerp(points[0], points[2], u);
        float x2 = lerp(points[1], points[3], u);

        float average = lerp(x1, x2, v);

        return Math.round(average);
    }

    private float[] getLerpPoints(int chunkX, int chunkZ) {
        int xx = (int)Math.floor((float)chunkX/8);
        int zz = (int)Math.floor((float)chunkZ/8);

        float[] points = new float[9];

        points[0] = getTerrainPoint(xx*8,     zz*8);
        points[1] = getTerrainPoint((xx+1)*8, zz*8);
        points[2] = getTerrainPoint(xx*8,     (zz+1)*8);
        points[3] = getTerrainPoint((xx+1)*8, (zz+1)*8);
        points[4] = getTerrainPoint((xx+2)*8, zz*8);
        points[5] = getTerrainPoint((xx+2)*8, (zz+1)*8);
        points[6] = getTerrainPoint(xx*8,     (zz+2)*8);
        points[7] = getTerrainPoint((xx+1)*8, (zz+2)*8);
        points[8] = getTerrainPoint((xx+2)*8, (zz+2)*8);

        return points;
    }

    private float lerp(float s, float e, float t) {
        return s + (e - s) * t;
    }

    private int addPerlin(int x, int z, int horz, int vert) {
        return (int)(terrainNoise.eval((float)x / horz, (float)z / horz) * vert);
    }

    private float getTerrainPoint(int x, int y) {
        double value;

        value  = addPerlin(x, y, 600, 50) * 0.6;
        value += addPerlin(x, y, 100, 25) * 0.6;
        value += addPerlin(x, y, 50, 13) * 0.6;

        return (float)value;
    }
}
