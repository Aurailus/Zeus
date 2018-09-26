package client.game.network;

import java.nio.ByteBuffer;

public class Bytes {
//    public static byte[] intToBytes(int a) {
//        byte[] ret = new byte[4];
//        ret[3] = (byte) (a & 0xFF);
//        ret[2] = (byte) ((a >> 8) & 0xFF);
//        ret[1] = (byte) ((a >> 16) & 0xFF);
//        ret[0] = (byte) ((a >> 24) & 0xFF);
//        return ret;
//    }
//
//    public static int bytesToInt(byte[] b) {
//        int value = 0;
//        for (int i = 0; i < 4; i++) {
//            int shift = (4 - 1 - i) * 8;
//            value += (b[i] & 0x000000FF) << shift;
//        }
//        return value;
//    }

//    public static byte[] longToBytes(long l) {
//        byte[] result = new byte[8];
//        for (int i = 7; i >= 0; i--) {
//            result[i] = (byte)(l & 0xFF);
//            l >>= 8;
//        }
//        return result;
//    }
//
//    public static long bytesToLong(byte[] b) {
//        long result = 0;
//        for (int i = 0; i < 8; i++) {
//            result <<= 8;
//            result |= (b[i] & 0xFF);
//        }
//        return result;
//    }

    // Long

    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();
        return buffer.getLong();
    }

    // Integer

    public static byte[] intToBytes(int x) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(x);
        return buffer.array();
    }

    public static int bytesToInt(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.put(bytes);
        buffer.flip();
        return buffer.getInt();
    }

    // Short

    public static byte[] shortToBytes(short x) {
        ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES);
        buffer.putShort(x);
        return buffer.array();
    }

    public static short bytesToShort(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES);
        buffer.put(bytes);
        buffer.flip();
        return buffer.getShort();
    }

//    public static byte[] shortToBytes(int val) {
//        return new byte[] {(byte)(val), (byte)((val >> 8))};
//    }
//
//    public static short bytesToShort(byte[] b) {
//        return ByteBuffer.wrap(new byte[]{b[1], b[0]}).getShort();
//    }
}
