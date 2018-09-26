package helpers;

import client.game.BlockChunk;
import client.game.Game;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static helpers.Bytes.*;

public class ChunkSerializer {

    public static byte[] encodeChunk(short[] chunk, ArrayList<short[]> sides) {
        ArrayList<Byte> list = new ArrayList<>();
        byte[] rleArray = RLE.encode(chunk);
        byte[] size = intToBytes(rleArray.length);
        list.add(size[0]);
        list.add(size[1]);
        list.add(size[2]);
        list.add(size[3]);

        ByteArrayInputStream rleStream = new ByteArrayInputStream(rleArray);
        while (rleStream.available() > 0) list.add((byte)rleStream.read());

        for (short[] shorts : sides) {
            addAdjacent(list, shorts);
        }

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

        return compressed;
    }

    private static void addAdjacent(ArrayList<Byte> list, short[] adj) {
        byte[] rleArray = RLE.encode(adj);
        byte[] size = intToBytes(rleArray.length);
        list.add(size[0]);
        list.add(size[1]);
        list.add(size[2]);
        list.add(size[3]);

        ByteArrayInputStream rleStream = new ByteArrayInputStream(rleArray);
        while (rleStream.available() > 0) list.add((byte)rleStream.read());
    }

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
            var shorts = RLE.decodeShorts(adjRLE);
            var bools = new boolean[shorts.length];
            for (var j = 0; j < shorts.length; j++) {
                bools[j] = Game.definitions.getDef(shorts[j]).getCulls();
            }
            sides.add(bools);
        }

        return new BlockChunk(chunk, sides);
    }
}
