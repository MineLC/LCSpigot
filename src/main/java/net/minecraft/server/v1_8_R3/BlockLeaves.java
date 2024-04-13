package net.minecraft.server.v1_8_R3;

import java.util.Random;


public abstract class BlockLeaves extends BlockTransparent {

    public static final BlockStateBoolean DECAYABLE = BlockStateBoolean.of("decayable");
    public static final BlockStateBoolean CHECK_DECAY = BlockStateBoolean.of("check_decay");
    int[] N;

    public BlockLeaves() {
        super(Material.LEAVES, false);
        this.a(true);
        this.a(CreativeModeTab.c);
        this.c(0.2F);
        this.e(1);
        this.a(BlockLeaves.h);
    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {

    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
    }

    public int a(Random random) {
        return random.nextInt(20) == 0 ? 1 : 0;
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Item.getItemOf(Blocks.SAPLING);
    }

    public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {
        if (!world.isClientSide) {
            int j = this.d(iblockdata);

            if (i > 0) {
                j -= 2 << i;
                if (j < 10) {
                    j = 10;
                }
            }

            if (world.random.nextInt(j) == 0) {
                Item item = this.getDropType(iblockdata, i);

                a(world, blockposition, new ItemStack(item, 1, this.getDropData(iblockdata)));
            }

            j = 200;
            if (i > 0) {
                j -= 10 << i;
                if (j < 40) {
                    j = 40;
                }
            }

            this.a(world, blockposition, iblockdata, j);
        }

    }

    protected void a(World world, BlockPosition blockposition, IBlockData iblockdata, int i) {}

    protected int d(IBlockData iblockdata) {
        return 20;
    }

    public boolean c() {
        return !this.R;
    }

    public boolean w() {
        return false;
    }

    public abstract BlockWood.EnumLogVariant b(int i);
}
