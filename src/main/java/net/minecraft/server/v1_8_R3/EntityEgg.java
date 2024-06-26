package net.minecraft.server.v1_8_R3;

// CraftBukkit start
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEggThrowEvent;
// CraftBukkit end

public class EntityEgg extends EntityProjectile {

    public EntityEgg(World world) {
        super(world);
    }

    public EntityEgg(World world, EntityLiving entityliving) {
        super(world, entityliving);
    }

    public EntityEgg(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    protected void a(MovingObjectPosition movingobjectposition) {
        if (movingobjectposition.entity != null) {
            movingobjectposition.entity.damageEntity(DamageSource.projectile(this, this.getShooter()), 0.0F);
        }

        // CraftBukkit start - Fire PlayerEggThrowEvent
        boolean hatching = !this.world.isClientSide && RANDOM.nextInt(8) == 0;
        int numHatching = (RANDOM.nextInt(32) == 0) ? 4 : 1;
        if (!hatching) {
            numHatching = 0;
        }

        EntityType hatchingType = EntityType.CHICKEN;

        Entity shooter = this.getShooter();
        if (shooter instanceof EntityPlayer) {
            Player player = (shooter == null) ? null : (Player) shooter.getBukkitEntity();

            PlayerEggThrowEvent event = new PlayerEggThrowEvent(player, (org.bukkit.entity.Egg) this.getBukkitEntity(), hatching, (byte) numHatching, hatchingType);
            this.world.getServer().getPluginManager().callEvent(event);

            hatching = event.isHatching();
            numHatching = event.getNumHatches();
            hatchingType = event.getHatchingType();
        }

        for (int j = 0; j < 8; ++j) {
            this.world.addParticle(EnumParticle.ITEM_CRACK, this.locX, this.locY, this.locZ, ((double) RANDOM.nextFloat() - 0.5D) * 0.08D, ((double) RANDOM.nextFloat() - 0.5D) * 0.08D, ((double) RANDOM.nextFloat() - 0.5D) * 0.08D, new int[] { Item.getId(Items.EGG)});
        }

        if (!this.world.isClientSide) {
            this.die();
        }

    }
}
