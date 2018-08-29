package ZeusClient.game;

import ZeusClient.game.network.ConnMan;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.*;

public class RegionManager {
    private static final int CHUNK_SIZE = BlockManager.CHUNK_SIZE;

    private BlockManager blockMan;
    private MeshManager meshMan;
    private ConnMan connMan;
    private ZeusGame game;

    private HashMap<Vector3i, short[]> pendingChunkData;
    private int chunksReceived = 0;

    public RegionManager(ZeusGame game, ConnMan connMan) {
        this.game = game;
        this.connMan = connMan;
        blockMan = new BlockManager();
        meshMan = new MeshManager(game, blockMan);

        pendingChunkData = new HashMap<>();
    }

    public void init() {
    }

    public void update() {
        long startTime = System.nanoTime();
        int maxTime = 1000000 * 8;

        Iterator<Map.Entry<Vector3i, short[]>> iterator = pendingChunkData.entrySet().iterator();
        int dealt = 0;

        while (System.nanoTime() - startTime < maxTime && iterator.hasNext()) {
            dealt++;

            Map.Entry<Vector3i, short[]> entry = iterator.next();
            iterator.remove();

            blockMan.setChunk(entry.getValue(), entry.getKey());
            MeshChunk chunk = meshMan.createChunk(entry.getKey());


            chunk.generateMesh();
            chunk.updateMesh();

        }
//        System.out.println(pendingChunkData.entrySet().size() + ", " + dealt);
    }

    public void render() {
//        meshMan.render();
    }

    public List<MeshChunk> getVisibleChunks() {
        return meshMan.getVisibleChunks();
    }

    public void loadChunks(Player player) {
        int LOAD_DISTANCE = 15;

        Vector3f playerPos = player.getPosition();
        Vector3i chunkOrigin = new Vector3i(Math.round(playerPos.x/CHUNK_SIZE), Math.round(playerPos.y/CHUNK_SIZE), Math.round(playerPos.z/CHUNK_SIZE));

        Vector3i request = new Vector3i(0, 0, 0);

        for (var i = chunkOrigin.x - LOAD_DISTANCE; i < chunkOrigin.x + LOAD_DISTANCE; i++) {
            for (var j = chunkOrigin.y - LOAD_DISTANCE; j < chunkOrigin.y + LOAD_DISTANCE; j++) {
                for (var k = chunkOrigin.z - LOAD_DISTANCE; k < chunkOrigin.z + LOAD_DISTANCE; k++) {
                    request.set(i, j, k);
                    if (!pendingChunkData.containsKey(request)) {
                        connMan.requestChunk(request, ((pos, data) -> {
                            pendingChunkData.put(pos, data);
                            chunksReceived ++;
                            System.out.println(chunksReceived);
                        }));
                    }
                }
            }
        }
    }
}
