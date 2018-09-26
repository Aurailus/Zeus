package ZeusClient.engine.helpers;

import org.joml.Vector3i;

public class ArrayTrans3D {
    public static final int CHUNK_SIZE = 16;

    public static Vector3i indToVec(int idx) {
        int z = idx / (CHUNK_SIZE * CHUNK_SIZE);
        idx -= (z * CHUNK_SIZE * CHUNK_SIZE);
        int y = idx / CHUNK_SIZE;
        int x = idx % CHUNK_SIZE;
        return new Vector3i(x, y, z);
    }

    public static void indToVec(int idx, Vector3i toSet) {
        toSet.z = idx / (CHUNK_SIZE * CHUNK_SIZE);
        idx -= (toSet.z * CHUNK_SIZE * CHUNK_SIZE);
        toSet.y = idx / CHUNK_SIZE;
        toSet.x = idx % CHUNK_SIZE;
    }

    public static int vecToInd(Vector3i pos) {
        return vecToInd(pos.x, pos.y, pos.z);
    }

    public static int vecToInd(int x, int y, int z) {
        return x + CHUNK_SIZE * (y + CHUNK_SIZE * z);
    }

    public static short get(short[] arr, Vector3i pos) {
        return get(arr, pos.x, pos.y, pos.z);
    }

    public static short get(short[] arr, int x, int y, int z) {
        return arr[vecToInd(x, y, z)];
    }

    public static short set(short[] arr, int var, Vector3i pos) {
        return set(arr, var, pos.x, pos.y, pos.z);
    }

    public static short set(short[] arr, int var, int x, int y, int z) {
        arr[vecToInd(x, y, z)] = (short)var;
        return (short)var;
    }

    public static boolean get(boolean[] arr, Vector3i pos) {
        return get(arr, pos.x, pos.y, pos.z);
    }

    public static boolean get(boolean[] arr, int x, int y, int z) {
        return arr[vecToInd(x, y, z)];
    }

    public static boolean set(boolean[] arr, boolean var, Vector3i pos) {
        return set(arr, var, pos.x, pos.y, pos.z);
    }

    public static boolean set(boolean[] arr, boolean var, int x, int y, int z) {
        arr[vecToInd(x, y, z)] = var;
        return var;
    }
}
