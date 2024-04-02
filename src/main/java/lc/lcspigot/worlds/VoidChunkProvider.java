package lc.lcspigot.worlds;

import net.minecraft.server.v1_8_R3.BiomeBase;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.ChunkSnapshot;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.IChunkProvider;
import net.minecraft.server.v1_8_R3.IProgressUpdate;
import net.minecraft.server.v1_8_R3.World;

public class VoidChunkProvider implements IChunkProvider {

    private final World a;
    private final IBlockData[] c = new IBlockData[256];

    public VoidChunkProvider(World world) {
        this.a = world;
        world.b(0);
    }

    public Chunk getOrCreateChunk(int i, int j) {
        ChunkSnapshot chunksnapshot = new ChunkSnapshot();

        int k;

        for (int l = 0; l < this.c.length; ++l) {
            IBlockData iblockdata = this.c[l];

            if (iblockdata != null) {
                for (int i1 = 0; i1 < 16; ++i1) {
                    for (k = 0; k < 16; ++k) {
                        chunksnapshot.a(i1, l, k, iblockdata);
                    }
                }
            }
        }

        Chunk chunk = new Chunk(this.a, chunksnapshot, i, j);
        BiomeBase[] abiomebase = this.a.getWorldChunkManager().getBiomeBlock(null, i * 16, j * 16, 16, 16);
        byte[] abyte = chunk.getBiomeIndex();

        for (k = 0; k < abyte.length; ++k) {
            abyte[k] = (byte) abiomebase[k].id;
        }

        return chunk;
    }

    public boolean isChunkLoaded(int i, int j) {
        return true;
    }

    public void getChunkAt(IChunkProvider ichunkprovider, int i, int j) {

    }

    public boolean a(IChunkProvider ichunkprovider, Chunk chunk, int i, int j) {
        return false;
    }

    public boolean saveChunks(boolean flag, IProgressUpdate iprogressupdate) {
        return true;
    }

    public void c() {}

    public boolean unloadChunks() {
        return false;
    }

    public boolean canSave() {
        return true;
    }

    public String getName() {
        return "FlatLevelSource";
    }

    public BlockPosition findNearestMapFeature(World world, String s, BlockPosition blockposition) {
        return null;
    }

    public int getLoadedChunks() {
        return 0;
    }

    public void recreateStructures(Chunk chunk, int i, int j) {

    }

    public Chunk getChunkAt(BlockPosition blockposition) {
        return this.getOrCreateChunk(blockposition.getX() >> 4, blockposition.getZ() >> 4);
    }
}
