package ZeusClient.game;

import org.joml.Vector3i;

import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class RegFileManip {
    private final static int SECTOR_SIZE = 4096;
    private final static int CHUNKS_WIDE = 16;
    private final static int CHUNKS_TOTAL = (int)Math.pow(CHUNKS_WIDE, 3);
    private final static int LOOKUP_START = 4;
    private final static int CHUNK_DATA_START = CHUNKS_TOTAL * 5 + LOOKUP_START;
    private final static int LOOKUP_ENTRY_SIZE = Integer.BYTES + 1; //Integer + Byte

    //Chunk Format
    // HEADERS
    // 4 bytes - Sectors used
    // CHUNK LOOKUP TABLE
    // 5*4096 = 12288 bytes //Sector offset + Sector size for each chunk, if size == 0, chunk doesn't exist
    // CHUNK DATA
    // 4 bytes counting exact size of chunk, followed by chunk, and padding until the next sector

    private boolean regionExists;
    private Vector3i regionPosition;
    private String regionHandle;
    private RandomAccessFile file;

    //Helper classes
    private byte[] shortToBytes(int val) throws Exception {
        if (val < 0 || val > Short.MAX_VALUE) throw new Exception("Short " + val + " is out of range.");
        return new byte[] {(byte)(val), (byte)((val >> 8))};
    }

    @SuppressWarnings("unused")
    private byte intToByte(int val) throws Exception {
        if (val < 0 || val > 255) throw new Exception("Int " + val + " is out of range.");
        return (byte)val;
    }

    private byte[] intToBytes(int val) {
        return ByteBuffer.allocate(4).putInt(val).array();
    }

    private int bytesToShort(byte[] bytes) {
        return ByteBuffer.wrap(new byte[]{bytes[1], bytes[0]}).getShort();
    }

    @SuppressWarnings("unused")
    private int byteToInt(byte by) {
        return by & 0xFF;
    }

    private int bytesToInt(byte[] bytes) {
        return new BigInteger(bytes).intValue();
    }

    private int vec3toLookup(Vector3i chunkPos) {
        return chunkPos.x + CHUNKS_WIDE * (chunkPos.y + CHUNKS_WIDE * chunkPos.z);
    }


    public RegFileManip(Vector3i regionPosition) throws Exception {
        this.regionPosition = regionPosition;
        this.regionHandle = regionPosition.x + "-" + regionPosition.y + "-" + regionPosition.z + ".zr";
        regionExists = new File(regionHandle).exists();
    }

    public boolean regionExists() {
        return regionExists;
    }

    public Vector3i getRegionPosition() {
        return regionPosition;
    }

    public void createRegionFile() {
        try {
            //noinspection ResultOfMethodCallIgnored
            new File(regionHandle).createNewFile();
            FileOutputStream writer = new FileOutputStream(regionHandle);
            writer.write(new byte[LOOKUP_START + LOOKUP_ENTRY_SIZE * CHUNKS_TOTAL]); //Create empty lookup table
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean beginAccess() {
        if (!regionExists) createRegionFile();
        //Open File
        try {
            file = new RandomAccessFile(regionHandle, "rw");
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean endAccess()  {
        //Close file
        if (file == null) {
            System.out.println("File not open!");
            return false;
        }
        else {
            try {
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    short[] readChunk(Vector3i chunkPos) throws Exception {
        long time = System.nanoTime();
        byte[] buff;

        //Go to the lookup entry for this chunk
        int lookupPosition = LOOKUP_START + vec3toLookup(chunkPos) * LOOKUP_ENTRY_SIZE;
        file.seek(lookupPosition);

        //Read the entry
        buff = new byte[Integer.BYTES];
        file.read(buff);
        int offset = bytesToInt(buff);
        int size = file.read();

        if (size <= 0) return null;

        file.seek(CHUNK_DATA_START + (SECTOR_SIZE * offset));

        //Get precise chunk size
        buff = new byte[Integer.BYTES];
        file.read(buff);
        int chunkLength = bytesToInt(buff);

        buff = new byte[chunkLength];
        file.read(buff, 0, buff.length);

        time = System.nanoTime() - time;
//            System.out.println("Reading chunk took " + time + "us (" + (Math.round(time / 1_000_000f * 100)/100f) + "ms)");
        return decodeChunk(buff);
    }

    void writeChunk(Vector3i chunkPos, short[] chunk) throws Exception {
        long time = System.nanoTime();
        byte[] buff;

        //Go to the lookup entry for this chunk
        int lookupPosition = LOOKUP_START + vec3toLookup(chunkPos) * LOOKUP_ENTRY_SIZE;
        file.seek(lookupPosition);

        //Read the entry
        buff = new byte[Integer.BYTES];
        file.read(buff);
        int offset = bytesToInt(buff);
        int size = file.read();

        byte[] data = encodeChunk(chunk); //Get compressed bytes for chunk

        int newSize = (int)Math.ceil(data.length / (float)SECTOR_SIZE);

        if (newSize > size) { //If chunk doesn't exist yet
            file.seek(0);
            buff = new byte[Integer.BYTES];
            file.read(buff);
            offset = bytesToInt(buff);
            file.seek(0);
            file.write(intToBytes(offset + newSize));

            file.seek(lookupPosition); //Seek to Lookup Table at position
        }

        file.seek(lookupPosition); //Seek to Lookup Table at position
        file.write(intToBytes(offset)); //Write offset
        file.write(newSize); //Write size

        int pointer = CHUNK_DATA_START + offset * SECTOR_SIZE; //Get the beginning position for the chunk data

        file.seek(pointer);
//        file.write(new byte[newSize * SECTOR_SIZE], 0, newSize * SECTOR_SIZE);
        file.seek(pointer);
        file.write(data, 0, data.length);

        time = System.nanoTime() - time;
//        System.out.println("Writing chunk took " + time + "us (" + (Math.round(time / 1_000_000f * 100)/100f) + "ms)");
    }

    public byte[] encodeChunk(short[] chunk) throws Exception {
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
        byte[] l = shortToBytes(length + 1);
        byte[] n = shortToBytes(num);
        list.add(l[0]);
        list.add(l[1]);
        list.add(n[0]);
        list.add(n[1]);

        byte[] array = new byte[list.size()];
        for (var i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream(array.length);
        GZIPOutputStream gzip = new GZIPOutputStream(bos);
        gzip.write(array);
        gzip.close();


        byte[] compressed = bos.toByteArray();
        byte[] data = Arrays.copyOf(ByteBuffer.allocate(4).putInt(compressed.length).array(), 4 + compressed.length);
        System.arraycopy(compressed, 0, data, 4, compressed.length);

        bos.close();

        long time = System.nanoTime() - start;
//        System.out.println("Encoding chunk took " + time + "us (" + (Math.round(time / 1_000_000f * 100)/100f) + "ms)");

        return data;
    }

    private short[] decodeChunk(byte[] encoded) throws IOException {
        long start = System.nanoTime();

        ByteArrayInputStream bis = new ByteArrayInputStream(encoded);
        GZIPInputStream gzip = new GZIPInputStream(bis);
        bis.close();

        byte[] rle = gzip.readAllBytes();

        short[] chunk = new short[4096];

        int ind = 0;
        for (var i = 0; i < rle.length/4; i++) {
            int length = bytesToShort(new byte[] {rle[i*4], rle[i*4 + 1]});
            int num = bytesToShort(new byte[] {rle[i*4 + 2], rle[i*4 + 3]});
            for (var j = 0; j < length; j++) {
                chunk[ind++] = (short)num;
            }
        }

        long time = System.nanoTime() - start;
//        System.out.println("Decoding chunk took " + time + "us (" + (Math.round(time / 1_000_000f * 100)/100f) + "ms)");

        return chunk;
    }

    static short[] createPlaceHolder(boolean rand) {
        var placeholder = new short[4096];
        if (!rand) for (var i = 0; i < placeholder.length; i++) placeholder[i] = (short)(i/500);
        else for (var i = 0; i < placeholder.length; i++) placeholder[i] = (short)(Math.round(Math.random()*32000));
        return placeholder;
    }
}
