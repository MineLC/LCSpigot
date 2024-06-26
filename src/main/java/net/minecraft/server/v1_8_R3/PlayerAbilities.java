package net.minecraft.server.v1_8_R3;

public class PlayerAbilities {

    public boolean isInvulnerable;
    public boolean isFlying;
    public boolean canFly;
    public boolean canInstantlyBuild;
    public boolean mayBuild = true;
    public float flySpeed = 0.05F; // CraftBukkit - public
    public float walkSpeed = 0.1F; // CraftBukkit - public

    public PlayerAbilities() {}

    public void a(NBTTagCompound nbttagcompound) {
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();

        nbttagcompound1.setBoolean("invulnerable", this.isInvulnerable);
        nbttagcompound1.setBoolean("flying", this.isFlying);
        nbttagcompound1.setBoolean("mayfly", this.canFly);
        nbttagcompound1.setBoolean("instabuild", this.canInstantlyBuild);
        nbttagcompound1.setBoolean("mayBuild", this.mayBuild);
        nbttagcompound1.setFloat("flySpeed", this.flySpeed);
        nbttagcompound1.setFloat("walkSpeed", this.walkSpeed);
        nbttagcompound.set("abilities", nbttagcompound1);
    }

    public void b(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.hasKeyOfType("abilities", 10)) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("abilities");

            this.isInvulnerable = nbttagcompound1.getBoolean("invulnerable");
            this.isFlying = nbttagcompound1.getBoolean("flying");
            this.canFly = nbttagcompound1.getBoolean("mayfly");
            this.canInstantlyBuild = nbttagcompound1.getBoolean("instabuild");
            if (nbttagcompound1.hasKeyOfType("flySpeed", 99)) {
                this.flySpeed = nbttagcompound1.getFloat("flySpeed");
                this.walkSpeed = nbttagcompound1.getFloat("walkSpeed");
            }

            if (nbttagcompound1.hasKeyOfType("mayBuild", 1)) {
                this.mayBuild = nbttagcompound1.getBoolean("mayBuild");
            }
        }

    }

    public float a() {
        return this.flySpeed;
    }

    public float b() {
        return this.walkSpeed;
    }
}
