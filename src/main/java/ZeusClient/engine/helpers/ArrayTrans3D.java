package ZeusClient.engine.helpers;

import org.joml.Vector3i;

public class ArrayTrans3D {
    public static final int CHUNK_SIZE = 16;

    public static short get(short[] arr, Vector3i pos) {
        return get(arr, pos.x, pos.y, pos.z);
    }

    public static short get(short[] arr, int x, int y, int z) {
        return arr[x + CHUNK_SIZE * (y + CHUNK_SIZE * z)];
    }

    public static short set(short[] arr, int var, Vector3i pos) {
        return set(arr, var, pos.x, pos.y, pos.z);
    }

    public static short set(short[] arr, int var, int x, int y, int z) {
        arr[x + CHUNK_SIZE * (y + CHUNK_SIZE * z)] = (short)var;
        return (short)var;
    }

    public static boolean get(boolean[] arr, Vector3i pos) {
        return get(arr, pos.x, pos.y, pos.z);
    }

    public static boolean get(boolean[] arr, int x, int y, int z) {
        return arr[x + CHUNK_SIZE * (y + CHUNK_SIZE * z)];
    }

    public static boolean set(boolean[] arr, boolean var, Vector3i pos) {
        return set(arr, var, pos.x, pos.y, pos.z);
    }

    public static boolean set(boolean[] arr, boolean var, int x, int y, int z) {
        arr[x + CHUNK_SIZE * (y + CHUNK_SIZE * z)] = var;
        return var;
    }
}
