package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class BiomeOcean extends BiomeBase {

    public BiomeOcean(int i) {
        super(i);
    }

    public BiomeBase.EnumTemperature m() {
        return BiomeBase.EnumTemperature.OCEAN;
    }

    public void a(World world, Random random, ChunkSnapshot chunksnapshot, int i, int j, double d0) {
        super.a(world, random, chunksnapshot, i, j, d0);
    }
}
