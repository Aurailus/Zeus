package ZeusClient.engine.helpers;

import ZeusClient.game.BlockChunk;
import ZeusClient.game.EncodedBlockChunk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static ZeusClient.game.network.Bytes.bytesToInt;
import static ZeusClient.game.network.Bytes.bytesToShort;
import static ZeusClient.game.network.Bytes.shortToBytes;

public class ChunkSerializer {
    public static BlockChunk decodeChunk(byte[] encoded) {
        byte[] decoded;

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(encoded);
            GZIPInputStream gzip = new GZIPInputStream(bis);
            bis.close();

            decoded = gzip.readAllBytes();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        ByteArrayInputStream bis = new ByteArrayInputStream(decoded);

        byte[] buff = new byte[4];
        bis.read(buff, 0, 4);
        var size = bytesToInt(buff);

        byte[] mainChunkRLE = new byte[size];
        bis.read(mainChunkRLE, 0, size);
        short[] chunk = RLE.decodeShorts(mainChunkRLE);

        ArrayList<boolean[]> sides = new ArrayList<>();
        for (var i = 0; i < 6; i++) {
            buff = new byte[4];
            bis.read(buff, 0, 4);
            var size2 = bytesToInt(buff);

            byte[] adjRLE = new byte[size2];
            bis.read(adjRLE, 0, size2);
            sides.add(RLE.decodeBools(adjRLE));
        }

        return new BlockChunk(chunk, sides);
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
