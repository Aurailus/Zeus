//package ZeusClient.game;
//
//import ZeusClient.game.network.ConnMan;
//import org.joml.Vector3f;
//import org.joml.Vector3i;
//
//import java.util.*;
//
//public class RegionManager {
//    private class PendingChunkObj {
//        Vector3i pos;
//        short[] data;
//
//        PendingChunkObj(Vector3i pos, short[] data) {
//            this.pos = pos;
//            this.data = data;
//        }
//    }
//
//    private static final int CHUNK_SIZE = BlockManager.CHUNK_SIZE;
//
//    private BlockManager blockMan;
//    private MeshManager meshMan;
//    private ConnMan connMan;
//    private Player player;
//
//    private ArrayList<PendingChunkObj> pendingChunkData;
//    private long lastSortTime;
//    private long lastPollTime;
//
//    public RegionManager(Player player, ZeusGame game, ConnMan connMan) {
//        this.player = player;
//        this.connMan = connMan;
//        blockMan = new BlockManager();
//        meshMan = new MeshManager(game, blockMan);
//
//        pendingChunkData = new ArrayList<>();
//        lastSortTime = System.currentTimeMillis();
//        lastPollTime = System.currentTimeMillis();
//    }
//
//    public void init() {
//    }
//
//    public void update() {
//        long startTime = System.nanoTime();
//        int maxTime = 1000000 * 4;
//
//        Vector3f posFloat = player.getPosition();
//        Vector3i chunkPos = new Vector3i(Math.round(posFloat.x/CHUNK_SIZE),
//                Math.round(posFloat.y/CHUNK_SIZE), Math.round(posFloat.z/CHUNK_SIZE));
//
//        if (System.currentTimeMillis() - lastSortTime > 1*1000) {
//            pendingChunkData.sort((PendingChunkObj a, PendingChunkObj b) -> {
//                var dA = a.pos.distance(chunkPos);
//                var dB = b.pos.distance(chunkPos);
//                return (Double.compare(dA, dB));
//            });
//            lastSortTime = System.currentTimeMillis();
//        }
//        if (System.currentTimeMillis() - lastPollTime > 1*1000) {
////            System.out.println("Loading Chunks");
//            loadChunks();
//            lastPollTime = System.currentTimeMillis();
//        }
//
//        Iterator<PendingChunkObj> iterator = pendingChunkData.iterator();
//        int dealt = 0;
//
//        while (System.nanoTime() - startTime < maxTime && iterator.hasNext()) {
//            dealt++;
//
//            PendingChunkObj entry = iterator.next();
//            iterator.remove();
//
//            blockMan.setChunk(entry.data, entry.pos);
//            MeshChunk chunk = meshMan.createChunk(entry.pos);
//
//            chunk.generateMesh();
//            chunk.updateAdjacentChunks();
//
//        }
////        System.out.println(pendingChunkData.size() + ", " + dealt);
//    }
//
//    public void render() {
//        meshMan.render();
//    }
//
//    public List<MeshChunk> getVisibleChunks() {
//        return meshMan.getVisibleChunks();
//    }
//
//    public void loadChunks() {
//        int LOAD_DISTANCE_HORI = 9;
//        int LOAD_DISTANCE_VERT = 7;
//
//        Vector3f playerPos = player.getPosition();
//        Vector3i chunkOrigin = new Vector3i(Math.round(playerPos.x/CHUNK_SIZE),
//                Math.round(playerPos.y/CHUNK_SIZE), Math.round(playerPos.z/CHUNK_SIZE));
//
//        Vector3i request = new Vector3i(0, 0, 0);
//
////        long s = System.currentTimeMillis();
//
//        for (var i = chunkOrigin.x - LOAD_DISTANCE_HORI; i < chunkOrigin.x + LOAD_DISTANCE_HORI; i++) {
//            for (var j = chunkOrigin.y - LOAD_DISTANCE_VERT; j < chunkOrigin.y + LOAD_DISTANCE_VERT; j++) {
//                for (var k = chunkOrigin.z - LOAD_DISTANCE_HORI; k < chunkOrigin.z + LOAD_DISTANCE_HORI; k++) {
//                    request.set(i, j, k);
//                    if (pendingChunkData.stream().noneMatch(c -> c.pos.equals(request)) && blockMan.getChunk(request) == null) {
//                        connMan.requestChunk(request, ((pos, data) -> pendingChunkData.add(new PendingChunkObj(pos, data))));
//                    }
//                }
//            }
//        }
//
////        System.out.println(System.currentTimeMillis() - s);
//    }
//}
