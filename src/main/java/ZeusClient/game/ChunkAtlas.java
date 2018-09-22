package ZeusClient.game;

import ZeusClient.engine.helpers.ChunkSerializer;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static ZeusClient.engine.helpers.ArrayTrans3D.CHUNK_SIZE;

public class ChunkAtlas {

    private ArrayList<MeshChunk> meshChunks;
    private ArrayList<Vector3i> netWaitingChunks;
    private HashMap<Vector3i, byte[]> pendingGenChunks;

    private ArrayList<BlockChunk> activeChunks;
    private HashMap<Vector3i, BlockChunk> activeChunkMap;


    private ArrayList<EncodedBlockChunk> cachedChunks;
    private HashMap<Vector3i, EncodedBlockChunk> cachedChunkMap;

    public ChunkAtlas() {
        meshChunks = new ArrayList<>();
        netWaitingChunks = new ArrayList<>();
        pendingGenChunks = new HashMap<>();

        activeChunks = new ArrayList<>();
        activeChunkMap = new HashMap<>();

        cachedChunks = new ArrayList<>();
        cachedChunkMap = new HashMap<>();
    }

    public ArrayList<MeshChunk> getVisibleChunks() {
        return meshChunks;
    }

    public void update() {
        loadMeshes();
    }

    public synchronized void loadMeshes() {
        int maxTime = 6;

        long startLoop = System.currentTimeMillis();
        Iterator<Map.Entry<Vector3i, byte[]>> it = pendingGenChunks.entrySet().iterator();

        while (it.hasNext() && System.currentTimeMillis() - startLoop < maxTime) {
            Map.Entry<Vector3i, byte[]> entry = it.next();


            long start = System.nanoTime();
            var blockChunk = ChunkSerializer.decodeChunk(entry.getValue());
//            System.out.println(System.nanoTime() - start + " ns. Decoding");

            MeshChunk chunk = new MeshChunk(new Vector3i(entry.getKey().x, entry.getKey().y, entry.getKey().z));

            start = System.nanoTime();
            chunk.createMesh(blockChunk);

            if (chunk.getMesh() != null) {
//                System.out.println(System.nanoTime() - start + " ns. creatingMesh");
                meshChunks.add(chunk);
                activeChunkMap.put(entry.getKey(), blockChunk);
//                System.out.println("Chunk gen time: " + Math.round(((float) (System.nanoTime() - start) / 1000000f) * 100f) / 100f + "ms");
            }

            it.remove();
        }
    }

    public synchronized void loadChunksAroundPos(Vector3f pos, int range) {
        var cOffset = coordsToChunk(pos);

        for (var i = -range; i < range; i++) {
            for (var j = -range; j < range; j++) {
                for (var k = -range; k < range; k++) {
                    loadChunk(i + cOffset.x, j + cOffset.y, k + cOffset.z);
                }
            }
        }
    }

    public synchronized void loadChunk(int x, int y, int z) {
        Vector3i reqPos = new Vector3i(x, y, z);

        if (!netWaitingChunks.contains(reqPos) && !activeChunkMap.containsKey(reqPos) && !pendingGenChunks.containsKey(reqPos)) {
            Game.connection.requestChunk(new Vector3i(x, y, z), (pos, chunk) -> {
                pendingGenChunks.put(pos, chunk);
            });
        }
    }

    public synchronized BlockChunk getChunk(Vector3i pos) {
        return activeChunkMap.get(pos);
    }

    public int getBlock(int x, int y, int z) {
        return getBlock(new Vector3i(x, y, z));
    }

    public int getBlock(Vector3i pos) {
        var chunkPos = coordsToChunk(new Vector3f(pos));
        var chunk = getChunk(chunkPos);
        if (chunk == null) return -1;
        var blockPos = new Vector3i(coordToLocal(pos.x), coordToLocal(pos.y), coordToLocal(pos.z));
        return chunk.getBlock(blockPos);
    }

    private Vector3i coordsToChunk(Vector3f pos) {
        return new Vector3i((int)Math.floor(pos.x / CHUNK_SIZE), (int)Math.floor(pos.y / CHUNK_SIZE), (int)Math.floor(pos.z / CHUNK_SIZE));
    }

    private int coordToLocal(int num) {
        return (num >= 0) ? (num % CHUNK_SIZE) : ((CHUNK_SIZE - 1) - Math.abs(num + 1) % CHUNK_SIZE);
    }
}
