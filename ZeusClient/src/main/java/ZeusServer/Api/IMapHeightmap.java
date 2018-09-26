package ZeusServer.Api;

public interface IMapHeightmap {
    int getHeight(int x, int z);
    int[][] getChunkHeightmap(int chunkX, int chunkZ);
}
