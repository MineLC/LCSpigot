package net.minecraft.server.v1_8_R3;

public class EntityMinecartCommandBlock extends EntityMinecartAbstract {

    private int b = 0;

    public EntityMinecartCommandBlock(World world) {
        super(world);
    }

    public EntityMinecartCommandBlock(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    protected void h() {
        super.h();
        this.getDataWatcher().a(23, "");
        this.getDataWatcher().a(24, "");
    }

    protected void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
    }

    protected void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
    }

    public EntityMinecartAbstract.EnumMinecartType s() {
        return EntityMinecartAbstract.EnumMinecartType.COMMAND_BLOCK;
    }

    public IBlockData u() {
        return Blocks.COMMAND_BLOCK.getBlockData();
    }


    public void a(int i, int j, int k, boolean flag) {
        if (flag && this.ticksLived - this.b >= 4) {
            this.b = this.ticksLived;
        }

    }

    public boolean e(EntityHuman entityhuman) {
        return false;
    }

    public void i(int i) {
        super.i(i);
    }
}
