package net.minecraft.server.v1_8_R3;

import java.util.Random;

// CraftBukkit start
// CraftBukkit end

public class BlockSapling extends BlockPlant implements IBlockFragilePlantElement {

    public static final BlockStateEnum<BlockWood.EnumLogVariant> TYPE = BlockStateEnum.of("type", BlockWood.EnumLogVariant.class);
    public static final BlockStateInteger STAGE = BlockStateInteger.of("stage", 0, 1);

    protected BlockSapling() {
        this.j(this.blockStateList.getBlockData().set(BlockSapling.TYPE, BlockWood.EnumLogVariant.OAK).set(BlockSapling.STAGE, Integer.valueOf(0)));
        float f = 0.4F;

        this.a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
        this.a(CreativeModeTab.c);
    }

    public String getName() {
        return LocaleI18n.get(this.a() + "." + BlockWood.EnumLogVariant.OAK.d() + ".name");
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
    }

    public void grow(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {

    }


    public boolean a(World world, BlockPosition blockposition, BlockWood.EnumLogVariant blockwood_enumlogvariant) {
        IBlockData iblockdata = world.getType(blockposition);

        return iblockdata.getBlock() == this && iblockdata.get(BlockSapling.TYPE) == blockwood_enumlogvariant;
    }

    public int getDropData(IBlockData iblockdata) {
        return ((BlockWood.EnumLogVariant) iblockdata.get(BlockSapling.TYPE)).a();
    }

    public boolean a(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return true;
    }

    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return (double) world.random.nextDouble() < 0.45D;
    }

    public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        this.grow(world, blockposition, iblockdata, random);
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockSapling.TYPE, BlockWood.EnumLogVariant.a(i & 7)).set(BlockSapling.STAGE, Integer.valueOf((i & 8) >> 3));
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | ((BlockWood.EnumLogVariant) iblockdata.get(BlockSapling.TYPE)).a();

        i |= ((Integer) iblockdata.get(BlockSapling.STAGE)).intValue() << 3;
        return i;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockSapling.TYPE, BlockSapling.STAGE});
    }

    static class SyntheticClass_1 {

        static final int[] a = new int[BlockWood.EnumLogVariant.values().length];

        static {
            try {
                BlockSapling.SyntheticClass_1.a[BlockWood.EnumLogVariant.SPRUCE.ordinal()] = 1;
            } catch (NoSuchFieldError nosuchfielderror) {
                ;
            }

            try {
                BlockSapling.SyntheticClass_1.a[BlockWood.EnumLogVariant.BIRCH.ordinal()] = 2;
            } catch (NoSuchFieldError nosuchfielderror1) {
                ;
            }

            try {
                BlockSapling.SyntheticClass_1.a[BlockWood.EnumLogVariant.JUNGLE.ordinal()] = 3;
            } catch (NoSuchFieldError nosuchfielderror2) {
                ;
            }

            try {
                BlockSapling.SyntheticClass_1.a[BlockWood.EnumLogVariant.ACACIA.ordinal()] = 4;
            } catch (NoSuchFieldError nosuchfielderror3) {
                ;
            }

            try {
                BlockSapling.SyntheticClass_1.a[BlockWood.EnumLogVariant.DARK_OAK.ordinal()] = 5;
            } catch (NoSuchFieldError nosuchfielderror4) {
                ;
            }

            try {
                BlockSapling.SyntheticClass_1.a[BlockWood.EnumLogVariant.OAK.ordinal()] = 6;
            } catch (NoSuchFieldError nosuchfielderror5) {
                ;
            }

        }
    }
}
