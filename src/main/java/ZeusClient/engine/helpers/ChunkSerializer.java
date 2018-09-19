package ZeusClient.engine.helpers;

import ZeusClient.game.BlockChunk;
import ZeusClient.game.EncodedBlockChunk;
import ZeusClient.game.ZeusGame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static ZeusClient.game.network.Bytes.*;

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
            var shorts = RLE.decodeShorts(adjRLE);
            var bools = new boolean[shorts.length];
            for (var j = 0; j < shorts.length; j++) {
                bools[j] = ZeusGame.atlas.blockDefs.get(shorts[j]).getCulls();
            }
            sides.add(bools);
        }

        return new BlockChunk(chunk, sides);
    }
}
