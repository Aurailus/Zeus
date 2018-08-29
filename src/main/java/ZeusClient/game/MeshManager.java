package ZeusClient.game;

import ZeusClient.engine.graphics.Material;
import ZeusClient.engine.graphics.Texture;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.*;

public class MeshManager {
    static final int REGION_SIZE = 16;
    static final int CHUNK_SIZE = BlockManager.CHUNK_SIZE;

    Material worldMaterial;
    ZeusGame game;

    BlockManager blockManager;
//    ChunkHandler chunkHandler;

    private List<MeshChunk> visibleChunks;
    private List<MeshChunk> dirtyChunks;

    private final Object dirtyLock = new Object();

    final Map<Vector3i, MeshRegion> regions;

    public MeshManager(ZeusGame game, BlockManager blockManager) {
        this.game = game;
        this.blockManager = blockManager;

        visibleChunks = new ArrayList<>();
        dirtyChunks = new ArrayList<>();
//        chunkHandler = new ChunkHandler(this);

        regions = new HashMap<>();

        try {
            worldMaterial = new Material(new Texture("/textures/grassblock.png"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
//        loadChunks(game.player);
//        chunkHandler.update(4);
    }

    public void render() {
//        chunkHandler.render();
        updateDirtyMeshes(4);
    }

    public void updateDirtyMeshes() {
        updateDirtyMeshes(-1);
    }

    public void updateDirtyMeshes(int maxTime) {
        synchronized (dirtyLock) {
            long start = System.currentTimeMillis();
            Iterator iterator = dirtyChunks.iterator();
            while (iterator.hasNext() && (maxTime < 0 || System.currentTimeMillis() - start < maxTime)) {
                MeshChunk chunk = (MeshChunk) iterator.next();
                if (chunk.meshData != null && chunk.dirty) {
                    chunk.updateMesh();
                    iterator.remove();
                }
            }
        }
    }

    public void addDirtyChunk(MeshChunk chunk) {
        synchronized (dirtyLock) {
            dirtyChunks.add(chunk);
        }
    }

    public MeshRegion createRegion(Vector3i pos) {
        if (regionExists(pos)) {
            System.out.println("Region already exists at " + pos + "!");
            return getRegion(pos);
        }

        var region = new MeshRegion(pos);
        setRegion(region, pos);
        return region;
    }

    public MeshChunk createChunk(Vector3i pos) {
        var regionPos = new Vector3i((int)Math.floor((float)pos.x / REGION_SIZE), (int)Math.floor((float)pos.y / REGION_SIZE), (int)Math.floor((float)pos.z / REGION_SIZE));

        var region = getRegion(regionPos);
        if (region == null) region = createRegion(regionPos);

        var chunkPos = new Vector3i(chunkCoordToLocal(pos.x), chunkCoordToLocal(pos.y), chunkCoordToLocal(pos.z));
        return region.setChunk(new MeshChunk(this, regionPos, chunkPos), chunkPos);
    }

    private static int chunkCoordToLocal(int num) {
        return (num >= 0) ? (num % REGION_SIZE) : ((REGION_SIZE - 1) - Math.abs(num + 1) % REGION_SIZE);
    }

    //
    // Block Region functions
    //

    public MeshRegion getRegion(int x, int y, int z) {
        return getRegion(new Vector3i(x, y, z));
    }

    public synchronized MeshRegion getRegion(Vector3i pos) {
        return regions.get(pos);
    }

    public boolean regionExists(int x, int y, int z) {
        return regionExists(new Vector3i(x, y, z));
    }

    public synchronized boolean regionExists(Vector3i pos) {
        return (regions.get(pos) != null);
    }

    public MeshRegion setRegion(MeshRegion region, int x, int y, int z) {
        return setRegion(region, new Vector3i(x, y, z));
    }

    public synchronized MeshRegion setRegion(MeshRegion region, Vector3i pos) {
        regions.put(pos, region);
        return region;
    }

    //
    // Chunk Functions
    //

    public MeshChunk getChunk(int x, int y, int z) {
        return getChunk(new Vector3i(x, y, z));
    }

    public synchronized MeshChunk getChunk(Vector3i pos) {
        var regionPos = new Vector3i((int) Math.floor((float) pos.x / REGION_SIZE), (int) Math.floor((float) pos.y / REGION_SIZE), (int) Math.floor((float) pos.z / REGION_SIZE));

        var region = getRegion(regionPos);
        if (region == null) return null;

        var chunkPos = new Vector3i(chunkCoordToLocal(pos.x), chunkCoordToLocal(pos.y), chunkCoordToLocal(pos.z));
        return region.getChunk(chunkPos);
    }

    public MeshChunk setChunk(MeshChunk chunk, int x, int y, int z) {
        return setChunk(chunk, new Vector3i(x, y, z));
    }

    public synchronized MeshChunk setChunk(MeshChunk chunk, Vector3i pos) {
        var regionPos = new Vector3i((int) Math.floor((float) pos.x / REGION_SIZE), (int) Math.floor((float) pos.y / REGION_SIZE), (int) Math.floor((float) pos.z / REGION_SIZE));

        var region = getRegion(regionPos);
        if (region == null) region = setRegion(new MeshRegion(regionPos), regionPos);

        var chunkPos = new Vector3i(chunkCoordToLocal(pos.x), chunkCoordToLocal(pos.y), chunkCoordToLocal(pos.z));
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

        public MeshRegion(int x, int y, int z) {
            this(new Vector3i(x, y, z));
        }

        public MeshRegion(Vector3i pos) {
            this.position = pos;
            chunks = new MeshChunk[(int)Math.pow(REGION_SIZE, 3)];
        }

        public MeshChunk getChunk(Vector3i pos) {
            return getChunk(pos.x, pos.y, pos.z);
        }

        public synchronized MeshChunk getChunk(int x, int y, int z) {
            return chunks[x + REGION_SIZE * (y + REGION_SIZE * z)];
        }

        public MeshChunk setChunk(MeshChunk chunk, Vector3i pos) {
            return setChunk(chunk, pos.x, pos.y, pos.z);
        }

        public synchronized MeshChunk setChunk(MeshChunk chunk, int x, int y, int z) {
            chunks[x + REGION_SIZE * (y + REGION_SIZE * z)] = chunk;
            return chunk;
        }
    }
}
