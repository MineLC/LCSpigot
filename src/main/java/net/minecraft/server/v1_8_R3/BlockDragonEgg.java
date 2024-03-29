package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class BlockDragonEgg extends Block {

    public BlockDragonEgg() {
        super(Material.DRAGON_EGG, MaterialMapColor.E);
        this.a(0.0625F, 0.0F, 0.0625F, 0.9375F, 1.0F, 0.9375F);
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        world.a(blockposition, (Block) this, this.a(world));
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        world.a(blockposition, (Block) this, this.a(world));
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        this.e(world, blockposition);
    }

    private void e(World world, BlockPosition blockposition) {
        if (BlockFalling.canFall(world, blockposition.down()) && blockposition.getY() >= 0) {
            byte b0 = 32;

            if (!BlockFalling.instaFall && world.areChunksLoadedBetween(blockposition.a(-b0, -b0, -b0), blockposition.a(b0, b0, b0))) {
                world.addEntity(new EntityFallingBlock(world, (double) ((float) blockposition.getX() + 0.5F), (double) blockposition.getY(), (double) ((float) blockposition.getZ() + 0.5F), this.getBlockData()));
            } else {
                world.setAir(blockposition);

                BlockPosition blockposition1;

                for (blockposition1 = blockposition; BlockFalling.canFall(world, blockposition1) && blockposition1.getY() > 0; blockposition1 = blockposition1.down()) {
                    ;
                }

                if (blockposition1.getY() > 0) {
                    world.setTypeAndData(blockposition1, this.getBlockData(), 2);
                }
            }

        }
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2) {
        this.f(world, blockposition);
        return true;
    }

    public void attack(World world, BlockPosition blockposition, EntityHuman entityhuman) {
        this.f(world, blockposition);
    }

    private void f(World world, BlockPosition blockposition) {

    }

    public int a(World world) {
        return 5;
    }

    public boolean c() {
        return false;
    }

    public boolean d() {
        return false;
    }
}
