package Zeus.game;

import Zeus.engine.graphics.Material;
import Zeus.engine.graphics.Texture;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeshManager {
    static final int REGION_SIZE = BlockManager.REGION_SIZE;
    static final int CHUNK_SIZE = BlockManager.CHUNK_SIZE;

    Material worldMaterial;;
    ZeusGame game;

    BlockManager blockManager;
    private List<MeshChunk> visibleChunks;

    final Map<Vector3i, MeshRegion> regions;

    public MeshManager(ZeusGame game, BlockManager blockManager) {
        this.game = game;
        this.blockManager = blockManager;
        this.visibleChunks = new ArrayList<>();
        regions = new HashMap<>();

        try {
            worldMaterial = new Material(new Texture("/textures/grassblock.png"));
        }
        catch (Exception e) {
            e.printStackTrace();
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

        public MeshRegion(MeshManager meshManager, int x, int y, int z) {
            this(meshManager, new Vector3i(x, y, z));
        }

        public MeshRegion(MeshManager meshManager, Vector3i pos) {
            this.position = pos;
            this.meshManager = meshManager;
            chunks = new MeshChunk[(int)Math.pow(REGION_SIZE, 3)];
        }

        public void populate() {
            for (var i = 0; i < REGION_SIZE; i++) {
                for (var j = 0; j < REGION_SIZE; j++) {
                    for (var k = 0; k < REGION_SIZE; k++) {
                        var chunk = new MeshChunk(meshManager, position, i, j, k);
                        chunk.init();
                        setChunk(chunk, i, j, k);
                    }
                }
            }
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