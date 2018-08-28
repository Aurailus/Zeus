package ZeusClient.engine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static ZeusClient.game.network.Bytes.bytesToShort;
import static ZeusClient.game.network.Bytes.shortToBytes;

public class ChunkSerializer {
    public static short[] decodeChunk(byte[] encoded) {
        long start = System.nanoTime();

        byte[] rle;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(encoded);
            GZIPInputStream gzip = new GZIPInputStream(bis);
            bis.close();

            rle = gzip.readAllBytes();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        short[] chunk = new short[4096];

        int ind = 0;
        for (var i = 0; i < rle.length / 4; i++) {
            int length = bytesToShort(new byte[]{rle[i * 4], rle[i * 4 + 1]});
            int num = bytesToShort(new byte[]{rle[i * 4 + 2], rle[i * 4 + 3]});
            for (var j = 0; j < length; j++) {
                chunk[ind++] = (short) num;
            }
        }

        long time = System.nanoTime() - start;
//        System.out.println("Decoding chunk took " + time + "us (" + (Math.round(time / 1_000_000f * 100)/100f) + "ms)");

        return chunk;
    }

    public static byte[] encodeChunk(short[] chunk) {
        long start = System.nanoTime();

        ArrayList<Byte> list = new ArrayList<>();

        short num = chunk[0];
        short length = -1;

        for (short i : chunk) {
            length++;
            if (num != i) {
                byte[] l = shortToBytes(length);
                byte[] n = shortToBytes(num);
                list.add(l[0]);
                list.add(l[1]);
                list.add(n[0]);
                list.add(n[1]);
                num = i;
                length = 0;
            }
        }
        byte[] l = shortToBytes((short)(length + 1));
        byte[] n = shortToBytes(num);
        list.add(l[0]);
        list.add(l[1]);
        list.add(n[0]);
        list.add(n[1]);

        byte[] array = new byte[list.size()];
        for (var i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }

        ByteArrayOutputStream bos;
        try {
            bos = new ByteArrayOutputStream(array.length);
            GZIPOutputStream gzip = new GZIPOutputStream(bos);
            gzip.write(array);
            gzip.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        byte[] compressed = bos.toByteArray();
        try {
            bos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        long time = System.nanoTime() - start;
//        System.out.println("Encoding chunk took " + time + "us (" + (Math.round(time / 1_000_000f * 100)/100f) + "ms)");

        return compressed;
    }
}
