//package Zeus.game;
//
//import Zeus.engine.RenderObj;
//import Zeus.engine.SimplexNoise;
//import Zeus.engine.graphics.Material;
//import Zeus.engine.graphics.Mesh;
//import org.joml.Vector3i;
//
//public class MapChunk extends RenderObj {
////    private MapRegion region;
//    private Material worldMat;
//    private boolean[] adjacentChunksLoaded = {false, false, false, false, false, false};
//    private boolean dirty;
//
//    private int x, y, z;
//    private int resolution;
//
//    public static final int CHUNK_SIZE = 16;
//    private int[] blocks;
//
//    private static final int NOISE_HORIZONTAL_PRECISION = 30;
//    private static final int NOISE_VERTICAL_PRECISION = 30;
//    private static final float NOISE_Y_MOD = 0.1f;
//
//    public MapChunk(Material worldMat, int x, int y, int z, int resolution) {
//        super();
//        this.setPosition(x * CHUNK_SIZE, y * CHUNK_SIZE, z * CHUNK_SIZE);
//        this.dirty = true;
//        this.x = x;
//        this.y = y;
//        this.z = z;
//        this.resolution = resolution;
//
//        this.worldMat = worldMat;
//
//        blocks = new int[CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE];
//        for (var i = 0; i < CHUNK_SIZE; i++) {
//            for (var j = 0; j < CHUNK_SIZE; j++) {
//                for (var k = 0; k < CHUNK_SIZE; k++) {
//                    int fill = 0;
//
//                    double noiseVal = SimplexNoise.noise(((double)i + x * CHUNK_SIZE) / NOISE_HORIZONTAL_PRECISION, ((double)j + y * CHUNK_SIZE) / NOISE_VERTICAL_PRECISION - 300, ((double)k + z * CHUNK_SIZE) / NOISE_HORIZONTAL_PRECISION);
//                    if (noiseVal - NOISE_Y_MOD * (j + (y-1) * CHUNK_SIZE) > 0) fill = 1;
////                    if (j > 8) fill = 1;
//                    setBlock(fill, i, j, k);
//                }
//            }
//        }
//    }
//
//    public void init() {
////        MapChunk adjacent;
////        adjacent = region.manager.getChunkAt(new Vector3i(x - 1, y, z));
////        if (adjacent != null) {
////            adjacent.setAdjacentState(0, true);
////            this.setAdjacentState(1, true);
////        }
////        adjacent = region.manager.getChunkAt(new Vector3i(x + 1, y, z));
////        if (adjacent != null) {
////            adjacent.setAdjacentState(1, true);
////            this.setAdjacentState(0, true);
////        }
////        adjacent = region.manager.getChunkAt(new Vector3i(x, y, z - 1));
////        if (adjacent != null) {
////            adjacent.setAdjacentState(2, true);
////            this.setAdjacentState(3, true);
////        }
////        adjacent = region.manager.getChunkAt(new Vector3i(x, y, z + 1));
////        if (adjacent != null) {
////            adjacent.setAdjacentState(3, true);
////            this.setAdjacentState(2, true);
////        }
////        adjacent = region.manager.getChunkAt(new Vector3i(x, y - 1, z));
////        if (adjacent != null) {
////            adjacent.setAdjacentState(4, true);
////            this.setAdjacentState(5, true);
////        }
////        adjacent = region.manager.getChunkAt(new Vector3i(x, y + 1, z));
////        if (adjacent != null) {
////            adjacent.setAdjacentState(5, true);
////            this.setAdjacentState(4, true);
////        }
//    }
//
//    //0: X+
//    //1: X-
//    //2: Z+
//    //3: Z-
//    //4: Y+
//    //5: Y-
//
//    public int getBlock(int x, int y, int z) {
//        return blocks[x + CHUNK_SIZE * (y + CHUNK_SIZE * z)];
//    }
//
//    public int getBlockInWorld(int x, int y, int z) {
//        int chunkXOffset = 0, chunkYOffset = 0, chunkZOffset = 0;
//        if (x < 0 || x >= CHUNK_SIZE || y < 0 || y >= CHUNK_SIZE || z < 0 || z >= CHUNK_SIZE) {
////            while (x < 0) {
////                x += CHUNK_SIZE;
////                chunkXOffset -= 1;
////            }
////            while (x >= CHUNK_SIZE) {
////                x -= CHUNK_SIZE;
////                chunkXOffset += 1;
////            }
////            while (y < 0) {
////                y += CHUNK_SIZE;
////                chunkYOffset -= 1;
////            }
////            while (y >= CHUNK_SIZE) {
////                y -= CHUNK_SIZE;
////                chunkYOffset += 1;
////            }
////            while (z < 0) {
////                z += CHUNK_SIZE;
////                chunkZOffset -= 1;
////            }
////            while (z >= CHUNK_SIZE) {
////                z -= CHUNK_SIZE;
////                chunkZOffset += 1;
////            }
////            var chunkPos = new Vector3i(this.x + chunkXOffset, this.y + chunkYOffset, this.z + chunkZOffset);
////            var chunk = region.manager.getChunkAt(chunkPos);
////            if (chunk != null) return chunk.getBlock(x, y, z);
//            return 0;
//        }
//        return blocks[x + CHUNK_SIZE * (y + CHUNK_SIZE * z)];
//    }
//
//    void setBlock(int block, int x, int y, int z) {
//        blocks[x + CHUNK_SIZE * (y + CHUNK_SIZE * z)] = block;
//    }
//
//    public void setAdjacentState(int adjInd, boolean state) {
//        adjacentChunksLoaded[adjInd] = state;
//        if (state && dirty) {
//            var canRender = true;
//            for (var i = 0; i < adjacentChunksLoaded.length; i++) {
//                if (!adjacentChunksLoaded[i]) {
//                    canRender = false;
//                    break;
//                }
//            }
//            if (canRender) {
//                updateMesh();
//            }
//        }
//    }
//
//    public void updateMesh() {
//        long start = System.nanoTime();
//        dirty = false;
//
//        var mesh = getMesh();
//        if (mesh != null) {
////            this.region.removeVisibleChunk(this);
//            mesh.cleanup();
//        }
//        MeshData m = new MeshData(this, resolution);
//        long one = System.nanoTime();
//        long two = one;
//        if (m.verts.length != 0) {
//            mesh = new Mesh(m.verts, m.texCoords, m.normals, m.indices);
//            mesh.setMaterial(worldMat);
//            two = System.nanoTime();
//            setMesh(mesh);
////            this.region.addVisibleChunk(this);
//        }
//        else {
//            setMesh(null);
//        }
//        if (two - start > 500000) { //half a millisecond
////            System.out.println("[!] CHUNK GEN TOOK: [" + (one - start) + " | " + (two - one) + "]");
//        }
//    }
//}