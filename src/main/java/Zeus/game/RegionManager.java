package Zeus.game;

import Zeus.game.objects.MapChunk;
import org.joml.Vector3i;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static Zeus.game.MapRegion.REGION_SIZE;

public class RegionManager {
    private Map<Vector3i, MapRegion> regionsMap;

    public RegionManager() {
        regionsMap = new HashMap<>();
    }

    public Map<Vector3i, MapRegion> getWorldRegions() {
        return regionsMap;
    }

    private void pushWorldRegion(Vector3i pos, MapRegion region) {
        if (regionsMap.containsKey(pos)) {
            System.out.println("Region already exists at " + pos.toString());
            return;
        }
        regionsMap.put(pos, region);
    }

    public MapRegion getRegion(Vector3i pos) {
        return regionsMap.get(pos);
    }

    public void addRegion(int x, int y, int z) {
        var r = new MapRegion(this, x, y, z);
        pushWorldRegion(new Vector3i(x, y, z), r);
        r.init();
    }

    public void addRegions(Vector3i from, Vector3i to) {
        for (int i = from.x; i < to.x; i++) {
            for (int j = from.y; j < to.y; j++) {
                for (int k = from.z; k < to.z; k++) {
                    addRegion(i, j, k);
                }
            }
        }
    }

    public Vector3i getPlayerPos() {
        return new Vector3i(0, 0, 0);
    }

    public MapChunk getChunkAt(Vector3i pos) {
        var rpos = new Vector3i(pos.x / 8, pos.y / 8, pos.z / 8);
        pos.x = BigInteger.valueOf(pos.x).mod(BigInteger.valueOf(8)).intValue();
        pos.y = BigInteger.valueOf(pos.y).mod(BigInteger.valueOf(8)).intValue();;
        pos.z = BigInteger.valueOf(pos.z).mod(BigInteger.valueOf(8)).intValue();;
        var region = regionsMap.get(rpos);
        if (region == null) return null;
        var chunk = region.chunks.get(pos);
        if (chunk == null) return null;

        return chunk;
    }
}
