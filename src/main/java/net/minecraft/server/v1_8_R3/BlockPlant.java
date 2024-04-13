package net.minecraft.server.v1_8_R3;

import java.util.Random;
// CraftBukkit start
// CraftBukkit end

public class BlockPlant extends Block {

    protected BlockPlant() {
        this(Material.PLANT);
    }

    protected BlockPlant(Material material) {
        this(material, material.r());
    }

    protected BlockPlant(Material material, MaterialMapColor materialmapcolor) {
        super(material, materialmapcolor);
        this.a(true);
        float f = 0.2F;

        this.a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 3.0F, 0.5F + f);
        this.a(CreativeModeTab.c);
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        return super.canPlace(world, blockposition) && this.c(world.getType(blockposition.down()).getBlock());
    }

    protected boolean c(Block block) {
        return block == Blocks.GRASS || block == Blocks.DIRT || block == Blocks.FARMLAND;
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        super.doPhysics(world, blockposition, iblockdata, block);
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
    }


    public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return null;
    }

    public boolean c() {
        return false;
    }

    public boolean d() {
        return false;
    }
}
