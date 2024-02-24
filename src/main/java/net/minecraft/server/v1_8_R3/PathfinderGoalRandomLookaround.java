package net.minecraft.server.v1_8_R3;

public class PathfinderGoalRandomLookaround extends PathfinderGoal {

    private EntityInsentient a;
    private double b;
    private double c;
    private int d;

    public PathfinderGoalRandomLookaround(EntityInsentient entityinsentient) {
        this.a = entityinsentient;
        this.a(3);
    }

    public boolean a() {
        return this.a.bc().nextFloat() < 0.02F;
    }

    public boolean b() {
        return this.d >= 0;
    }

    public void c() {
        double d0 = 6.283185307179586D * this.a.bc().nextDouble();

        this.b = Math.cos(d0);
        this.c = Math.sin(d0);
        this.d = 20 + this.a.bc().nextInt(20);
    }

    public void e() {
        --this.d;
        this.a.getControllerLook().a(this.a.locX + this.b, this.a.locY + (double) this.a.getHeadHeight(), this.a.locZ + this.c, 10.0F, (float) this.a.bQ());
    }
}