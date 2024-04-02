package net.minecraft.server.v1_8_R3;

public final class SpawnerCreature {

    public SpawnerCreature() {}

    public int a(WorldServer worldserver, boolean flag, boolean flag1, boolean flag2) {
        return 0;
    }

    protected static BlockPosition getRandomPosition(World world, int i, int j) {
        Chunk chunk = world.getChunkAt(i, j);
        int k = i * 16 + world.random.nextInt(16);
        int l = j * 16 + world.random.nextInt(16);
        int i1 = MathHelper.c(chunk.f(new BlockPosition(k, 0, l)) + 1, 16);
        int j1 = world.random.nextInt(i1 > 0 ? i1 : chunk.g() + 16 - 1);

        return new BlockPosition(k, j1, l);
    }

    public static boolean a(EntityInsentient.EnumEntityPositionType entityinsentient_enumentitypositiontype, World world, BlockPosition blockposition) {
        return false;
    }
}