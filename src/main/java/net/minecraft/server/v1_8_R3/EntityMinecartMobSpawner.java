package net.minecraft.server.v1_8_R3;

public class EntityMinecartMobSpawner extends EntityMinecartAbstract {

    public EntityMinecartMobSpawner(World world) {
        super(world);
    }

    public EntityMinecartMobSpawner(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    public EntityMinecartAbstract.EnumMinecartType s() {
        return EntityMinecartAbstract.EnumMinecartType.SPAWNER;
    }

    public IBlockData u() {
        return Blocks.MOB_SPAWNER.getBlockData();
    }

    protected void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
    }

    protected void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
    }

    public void t_() {
        super.t_();
    }
}
