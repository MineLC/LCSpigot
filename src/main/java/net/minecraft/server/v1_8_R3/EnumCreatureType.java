package net.minecraft.server.v1_8_R3;

public enum EnumCreatureType {

    MONSTER(IMonster.class, 70, false, false),
    AMBIENT(EntityAmbient.class, 15, true, false), 
    WATER_CREATURE(EntityWaterAnimal.class, 5, true, false);

    private final Class<? extends IAnimal> e;
    private final int f;
    private final boolean h;
    private final boolean i;

    private EnumCreatureType(Class<? extends IAnimal> oclass, int i, boolean flag, boolean flag1) {
        this.e = oclass;
        this.f = i;
        this.h = flag;
        this.i = flag1;
    }

    public Class<? extends IAnimal> a() {
        return this.e;
    }

    public int b() {
        return this.f;
    }

    public boolean d() {
        return this.h;
    }

    public boolean e() {
        return this.i;
    }
}
