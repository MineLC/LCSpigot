package net.minecraft.server.v1_8_R3;

public interface IChunkProvider {

    boolean isChunkLoaded(int i, int j);

    Chunk getOrCreateChunk(int i, int j);

    Chunk getChunkAt(BlockPosition blockposition);

    void getChunkAt(IChunkProvider ichunkprovider, int i, int j);

    boolean a(IChunkProvider ichunkprovider, Chunk chunk, int i, int j);

    boolean saveChunks(boolean flag, IProgressUpdate iprogressupdate);

    boolean unloadChunks();

    boolean canSave();

    String getName();

    BlockPosition findNearestMapFeature(World world, String s, BlockPosition blockposition);

    int getLoadedChunks();

    void recreateStructures(Chunk chunk, int i, int j);

    void c();
}
