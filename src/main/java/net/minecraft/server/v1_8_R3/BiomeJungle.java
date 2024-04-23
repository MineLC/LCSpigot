package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class BiomeJungle extends BiomeBase {

    public BiomeJungle(int i, boolean flag) {
        super(i);
        if (flag) {
            this.as.A = 2;
        } else {
            this.as.A = 50;
        }

        this.as.C = 25;
        this.as.B = 4;
    }

    public WorldGenerator b(Random random) {
        return random.nextInt(4) == 0 ? new WorldGenGrass(BlockLongGrass.EnumTallGrassType.FERN) : new WorldGenGrass(BlockLongGrass.EnumTallGrassType.GRASS);
    }

    public void a(World world, Random random, BlockPosition blockposition) {
        super.a(world, random, blockposition);
        int i = random.nextInt(16) + 8;
        int j = random.nextInt(16) + 8;
        int k = random.nextInt(world.getHighestBlockYAt(blockposition.a(i, 0, j)).getY() * 2);

        (new WorldGenMelon()).generate(world, random, blockposition.a(i, k, j));
        WorldGenVines worldgenvines = new WorldGenVines();

        for (j = 0; j < 50; ++j) {
            k = random.nextInt(16) + 8;
            int l = random.nextInt(16) + 8;

            worldgenvines.generate(world, random, blockposition.a(k, 128, l));
        }

    }
}
