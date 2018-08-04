//package Zeus.game;
//
//import Zeus.engine.graphics.Material;
//import Zeus.engine.graphics.Texture;
//import org.joml.Vector3i;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class MapRegion {
//    public RegionManager manager;
//
//    private List<MapChunk> visibleChunks;
//    Map<Vector3i, MapChunk> chunks;
//
//    int x, y, z;
//
//    public static final int REGION_SIZE = 8;
//    public static final int LOD_STEP = 8;
//
//    private static Material worldMat;
//    static {
//        try {
//            worldMat = new Material(new Texture("/textures/grassblock.png"));
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    MapRegion(RegionManager manager, int regionX, int regionY, int regionZ) {
//        this.x = regionX;
//        this.y = regionY;
//        this.z = regionZ;
//        this.manager = manager;
//
//        this.visibleChunks = new ArrayList<>();
//        this.chunks = new HashMap<>();
//    }
//
//    public void init() {
//        for (int i = 0; i < REGION_SIZE; i++) {
//            for (int j = 0; j < REGION_SIZE; j++) {
//                for (int k = 0; k < REGION_SIZE; k++) {
//                    int cX = x * REGION_SIZE + i;
//                    int cY = y * REGION_SIZE + j;
//                    int cZ = z * REGION_SIZE + k;
//
//                    Vector3i pos = manager.getPlayerPos();
//
//                    int distFromPlayer = (int)Math.round(Math.sqrt(
//                            Math.pow(Math.abs(pos.x / MapChunk.CHUNK_SIZE - cX), 2) +
//                                    Math.pow(Math.abs(pos.y / MapChunk.CHUNK_SIZE - cY), 2) +
//                                    Math.pow(Math.abs(pos.z / MapChunk.CHUNK_SIZE - cZ), 2)));
//
//                    var c = new MapChunk(worldMat, this, cX, cY, cZ, Math.min(distFromPlayer / LOD_STEP, 3));
//                    chunks.put(new Vector3i(i, j, k), c);
//                    c.init();
//                }
//            }
//        }
//    }
//
//    public void addVisibleChunk(MapChunk chunk) {
//        visibleChunks.add(chunk);
//    }
//
//    public void removeVisibleChunk(MapChunk chunk) {
//        visibleChunks.remove(chunk);
//    }
//
//    public List<MapChunk> getVisibleChunks() {
//        return visibleChunks;
//    }
//}