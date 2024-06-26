package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class WorldGenDeadBush extends WorldGenerator {

    public WorldGenDeadBush() {}

    public boolean generate(World world, Random random, BlockPosition blockposition) {
        Block block;

        while (((block = world.getType(blockposition).getBlock()).getMaterial() == Material.AIR || block.getMaterial() == Material.LEAVES) && blockposition.getY() > 0) {
            blockposition = blockposition.down();
        }

        return true;
    }
}
