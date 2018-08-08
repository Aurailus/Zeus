package Zeus.game;

import Zeus.engine.graphics.Material;
import Zeus.engine.graphics.Texture;
import org.joml.Vector3i;

import java.util.*;

public class MeshManager {
    static final int REGION_SIZE = BlockManager.REGION_SIZE;
    static final int CHUNK_SIZE = BlockManager.CHUNK_SIZE;
    static final boolean MULTITHREADING_ENABLED = ZeusGame.MULTITHREADING_ENABLED;

    Material worldMaterial;
    ZeusGame game;

    BlockManager blockManager;
    private List<MeshChunk> visibleChunks;
    private List<MeshChunk> dirtyChunks;

    final Map<Vector3i, MeshRegion> regions;

    public MeshManager(ZeusGame game, BlockManager blockManager) {
        this.game = game;

        this.blockManager = blockManager;
        this.visibleChunks = new ArrayList<>();
        this.dirtyChunks = new ArrayList<>();
        regions = new HashMap<>();

        try {
            worldMaterial = new Material(new Texture("/textures/grassblock.png"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateDirtyMeshes() {
        updateDirtyMeshes(-1);
    }

    public void updateDirtyMeshes(int maxTime) {
        long start = System.currentTimeMillis();
        Iterator iterator = dirtyChunks.iterator();
        while (iterator.hasNext() && (maxTime < 0 || System.currentTimeMillis() - start < maxTime)) {
            MeshChunk chunk = (MeshChunk)iterator.next();
            if (chunk.meshData != null && chunk.dirty) {
                chunk.updateMesh();
                iterator.remove();
            }
        }
    }

    public void createRegion(Vector3i pos) {
        if (regionExists(pos)) {
            System.out.println("Region already exists at " + pos + "!");
            return;
        }

        var region = new MeshRegion(this, pos);
        setRegion(region, pos);
        region.populate();
    }

    static private int chunkCoordToLocal(int num) {
        return (num >= 0) ? (num % REGION_SIZE) : ((REGION_SIZE - 1) - Math.abs(num + 1) % REGION_SIZE);
    }

    //
    // Block Region functions
    //

    public MeshRegion getRegion(int x, int y, int z) {
        return getRegion(new Vector3i(x, y, z));
    }

    public MeshRegion getRegion(Vector3i pos) {
        return regions.get(pos);
    }

    public boolean regionExists(int x, int y, int z) {
        return regionExists(new Vector3i(x, y, z));
    }

    public boolean regionExists(Vector3i pos) {
        return (regions.get(pos) != null);
    }

    public MeshRegion setRegion(MeshRegion region, int x, int y, int z) {
        return setRegion(region, new Vector3i(x, y, z));
    }

    public MeshRegion setRegion(MeshRegion region, Vector3i pos) {
        regions.put(pos, region);
        return region;
    }

    //
    // Chunk Functions
    //

    public MeshChunk getChunk(int x, int y, int z) {
        return getChunk(new Vector3i(x, y, z));
    }

    public MeshChunk getChunk(Vector3i pos) {
        var regionPos = new Vector3i((int)Math.floor((float)pos.x / REGION_SIZE), (int)Math.floor((float)pos.y / REGION_SIZE), (int)Math.floor((float)pos.z / REGION_SIZE));

        var region = getRegion(regionPos);
        if (region == null) return null;

        var chunkPos  = new Vector3i(chunkCoordToLocal(pos.x), chunkCoordToLocal(pos.y), chunkCoordToLocal(pos.z));
        return region.getChunk(chunkPos);
    }

    public MeshChunk setChunk(MeshChunk chunk, int x, int y, int z) {
        return setChunk(chunk, new Vector3i(x, y, z));
    }

    public MeshChunk setChunk(MeshChunk chunk, Vector3i pos) {
        var regionPos = new Vector3i((int)Math.floor((float)pos.x / REGION_SIZE), (int)Math.floor((float)pos.y / REGION_SIZE), (int)Math.floor((float)pos.z / REGION_SIZE));

        var region = getRegion(regionPos);
        if (region == null) region = setRegion(new MeshRegion(this, regionPos), regionPos);

        var chunkPos  = new Vector3i(chunkCoordToLocal(pos.x), chunkCoordToLocal(pos.y), chunkCoordToLocal(pos.z));
        region.setChunk(chunk, chunkPos);
        return chunk;
    }

    //
    // Chunk List Functions
    //

    public void addVisibleChunk(MeshChunk chunk) {
        visibleChunks.add(chunk);
    }

    public void removeVisibleChunk(MeshChunk chunk) {
        visibleChunks.remove(chunk);
    }

    public List<MeshChunk> getVisibleChunks() {
        return visibleChunks;
    }

    class MeshRegion {
        private MeshChunk[] chunks;
        public final Vector3i position;
        private MeshManager meshManager;

        class MeshThread extends Thread implements Runnable {
            int yLayer;
            MeshRegion region;

            private MeshThread(int yLayer, MeshRegion region) {
                this.yLayer = yLayer;
                this.region = region;
            }

            @Override
            public void run() {
                for (var i = 0; i < REGION_SIZE; i++) {
                    for (var k = 0; k < REGION_SIZE; k++) {
//                        long start = System.nanoTime();
                        var chunk = new MeshChunk(meshManager, position, i, yLayer, k);
                        chunk.init();
                        setChunk(chunk, i, yLayer, k);
//                        System.out.println(" - - BlockChunk took " + ((System.nanoTime() - start)/1_000_000f) + "ms");
                    }
                }
            }
        }


        public MeshRegion(MeshManager meshManager, int x, int y, int z) {
            this(meshManager, new Vector3i(x, y, z));
        }

        public MeshRegion(MeshManager meshManager, Vector3i pos) {
            this.position = pos;
            this.meshManager = meshManager;
            chunks = new MeshChunk[(int)Math.pow(REGION_SIZE, 3)];
        }

        public void populate() {
            if (MULTITHREADING_ENABLED) {
                Thread[] threads = new Thread[REGION_SIZE];
                for (var i = 0; i < threads.length; i++) {
                    threads[i] = new MeshThread(i, this);
                    threads[i].start();
                }

                try {
                    for (var i = 0; i < threads.length; i++) {
                        threads[i].join();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                for (var i = 0; i < REGION_SIZE; i++) {
                    for (var j = 0; j < REGION_SIZE; j++) {
                        for (var k = 0; k < REGION_SIZE; k++) {
                            long start = System.nanoTime();
                            var chunk = new MeshChunk(meshManager, position, i, j, k);
                            chunk.init();
                            setChunk(chunk, i, j, k);
//                        System.out.println(" - - MeshChunk took " + ((System.nanoTime() - start)/1_000_000f) + "ms");
                        }
                    }
                }
            }
            meshManager.dirtyChunks.addAll(Arrays.asList(chunks));
        }

        public MeshChunk getChunk(Vector3i pos) {
            return getChunk(pos.x, pos.y, pos.z);
        }

        public MeshChunk getChunk(int x, int y, int z) {
            return chunks[x + REGION_SIZE * (y + REGION_SIZE * z)];
        }

        public MeshChunk setChunk(MeshChunk chunk, Vector3i pos) {
            return setChunk(chunk, pos.x, pos.y, pos.z);
        }

        public MeshChunk setChunk(MeshChunk chunk, int x, int y, int z) {
            chunks[x + REGION_SIZE * (y + REGION_SIZE * z)] = chunk;
            return chunk;
        }
    }
}
