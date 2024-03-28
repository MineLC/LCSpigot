package net.minecraft.server.v1_8_R3;

import com.google.common.base.Predicate;

// CraftBukkit start
import org.bukkit.craftbukkit.v1_8_R3.util.BlockStateListPopulator;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
// CraftBukkit end

public class BlockPumpkin extends BlockDirectional {

    private ShapeDetector snowGolemPart;
    private ShapeDetector snowGolem;
    private ShapeDetector ironGolemPart;
    private ShapeDetector ironGolem;
    private static final Predicate<IBlockData> Q = new Predicate() {
        public boolean a(IBlockData iblockdata) {
            return iblockdata != null && (iblockdata.getBlock() == Blocks.PUMPKIN || iblockdata.getBlock() == Blocks.LIT_PUMPKIN);
        }

        public boolean apply(Object object) {
            return this.a((IBlockData) object);
        }
    };

    protected BlockPumpkin() {
        super(Material.PUMPKIN, MaterialMapColor.q);
        this.j(this.blockStateList.getBlockData().set(BlockPumpkin.FACING, EnumDirection.NORTH));
        this.a(true);
        this.a(CreativeModeTab.b);
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        super.onPlace(world, blockposition, iblockdata);
        this.f(world, blockposition);
    }

    public boolean e(World world, BlockPosition blockposition) {
        return this.getDetectorSnowGolemPart().a(world, blockposition) != null || this.getDetectorIronGolemPart().a(world, blockposition) != null;
    }

    private void f(World world, BlockPosition blockposition) {
        
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        return world.getType(blockposition).getBlock().material.isReplaceable() && World.a((IBlockAccess) world, blockposition.down());
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        return this.getBlockData().set(BlockPumpkin.FACING, entityliving.getDirection().opposite());
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockPumpkin.FACING, EnumDirection.fromType2(i));
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((EnumDirection) iblockdata.get(BlockPumpkin.FACING)).b();
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockPumpkin.FACING});
    }

    protected ShapeDetector getDetectorSnowGolemPart() {
        if (this.snowGolemPart == null) {
            this.snowGolemPart = ShapeDetectorBuilder.a().a(new String[] { " ", "#", "#"}).a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.SNOW))).b();
        }

        return this.snowGolemPart;
    }

    protected ShapeDetector getDetectorSnowGolem() {
        if (this.snowGolem == null) {
            this.snowGolem = ShapeDetectorBuilder.a().a(new String[] { "^", "#", "#"}).a('^', ShapeDetectorBlock.a(BlockPumpkin.Q)).a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.SNOW))).b();
        }

        return this.snowGolem;
    }

    protected ShapeDetector getDetectorIronGolemPart() {
        if (this.ironGolemPart == null) {
            this.ironGolemPart = ShapeDetectorBuilder.a().a(new String[] { "~ ~", "###", "~#~"}).a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.IRON_BLOCK))).a('~', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.AIR))).b();
        }

        return this.ironGolemPart;
    }

    protected ShapeDetector getDetectorIronGolem() {
        if (this.ironGolem == null) {
            this.ironGolem = ShapeDetectorBuilder.a().a(new String[] { "~^~", "###", "~#~"}).a('^', ShapeDetectorBlock.a(BlockPumpkin.Q)).a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.IRON_BLOCK))).a('~', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.AIR))).b();
        }

        return this.ironGolem;
    }
}
