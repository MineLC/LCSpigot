package org.bukkit.craftbukkit.v1_8_R3.entity;

import java.util.List;
import java.util.UUID;

import net.minecraft.server.v1_8_R3.*;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.metadata.MetadataValue;

import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public abstract class CraftEntity implements org.bukkit.entity.Entity {
    
    protected final CraftServer server;
    protected Entity entity;
    private EntityDamageEvent lastDamageEvent;

    public CraftEntity(final CraftServer server, final Entity entity) {
        this.server = server;
        this.entity = entity;
    }

    public static CraftEntity getEntity(CraftServer server, Entity entity) {
        /**
         * Order is *EXTREMELY* important -- keep it right! =D
         */
        if (entity instanceof EntityLiving) {
            // Players
            if (entity instanceof EntityHuman) {
                if (entity instanceof EntityPlayer) { return new CraftPlayer(server, (EntityPlayer) entity); }
                 return new CraftHumanEntity(server, (EntityHuman) entity);
            }
            if (entity instanceof EntityArmorStand) { return new CraftArmorStand(server, (EntityArmorStand) entity); }
            return new CraftLivingEntity(server, (EntityLiving) entity);
        } else if (entity instanceof EntityExperienceOrb) { return new CraftExperienceOrb(server, (EntityExperienceOrb) entity); }
        else if (entity instanceof EntityArrow) { return new CraftArrow(server, (EntityArrow) entity); }
        else if (entity instanceof EntityBoat) { return new CraftBoat(server, (EntityBoat) entity); }
        else if (entity instanceof EntityProjectile) {
            if (entity instanceof EntityEgg) { return new CraftEgg(server, (EntityEgg) entity); }
            else if (entity instanceof EntitySnowball) { return new CraftSnowball(server, (EntitySnowball) entity); }
            else if (entity instanceof EntityPotion) { return new CraftThrownPotion(server, (EntityPotion) entity); }
            else if (entity instanceof EntityEnderPearl) { return new CraftEnderPearl(server, (EntityEnderPearl) entity); }
            else if (entity instanceof EntityThrownExpBottle) { return new CraftThrownExpBottle(server, (EntityThrownExpBottle) entity); }
        }
        else if (entity instanceof EntityFallingBlock) { return new CraftFallingSand(server, (EntityFallingBlock) entity); }
        else if (entity instanceof EntityFireball) {
            if (entity instanceof EntitySmallFireball) { return new CraftSmallFireball(server, (EntitySmallFireball) entity); }
            else if (entity instanceof EntityLargeFireball) { return new CraftLargeFireball(server, (EntityLargeFireball) entity); }
            else if (entity instanceof EntityWitherSkull) { return new CraftWitherSkull(server, (EntityWitherSkull) entity); }
            else { return new CraftFireball(server, (EntityFireball) entity); }
        }
        else if (entity instanceof EntityEnderSignal) { return new CraftEnderSignal(server, (EntityEnderSignal) entity); }
        else if (entity instanceof EntityFishingHook) { return new CraftFish(server, (EntityFishingHook) entity); }
        else if (entity instanceof EntityItem) { return new CraftItem(server, (EntityItem) entity); }
        else if (entity instanceof EntityWeather) {
            if (entity instanceof EntityLightning) { return new CraftLightningStrike(server, (EntityLightning) entity); }
            else { return new CraftWeather(server, (EntityWeather) entity); }
        }
        else if (entity instanceof EntityMinecartAbstract) {
            if (entity instanceof EntityMinecartFurnace) { return new CraftMinecartFurnace(server, (EntityMinecartFurnace) entity); }
            else if (entity instanceof EntityMinecartChest) { return new CraftMinecartChest(server, (EntityMinecartChest) entity); }
            else if (entity instanceof EntityMinecartTNT) { return new CraftMinecartTNT(server, (EntityMinecartTNT) entity); }
            else if (entity instanceof EntityMinecartHopper) { return new CraftMinecartHopper(server, (EntityMinecartHopper) entity); }
            else if (entity instanceof EntityMinecartMobSpawner) { return new CraftMinecartMobSpawner(server, (EntityMinecartMobSpawner) entity); }
            else if (entity instanceof EntityMinecartRideable) { return new CraftMinecartRideable(server, (EntityMinecartRideable) entity); }
        } else if (entity instanceof EntityHanging) {
            if (entity instanceof EntityPainting) { return new CraftPainting(server, (EntityPainting) entity); }
            else if (entity instanceof EntityItemFrame) { return new CraftItemFrame(server, (EntityItemFrame) entity); }
            else if (entity instanceof EntityLeash) { return new CraftLeash(server, (EntityLeash) entity); }
            else { return new CraftHanging(server, (EntityHanging) entity); }
        }
        else if (entity instanceof EntityTNTPrimed) { return new CraftTNTPrimed(server, (EntityTNTPrimed) entity); }
        else if (entity instanceof EntityFireworks) { return new CraftFirework(server, (EntityFireworks) entity); }

        throw new AssertionError("Unknown entity " + (entity == null ? null : entity.getClass()));
    }

    public Location getLocation() {
        return new Location(getWorld(), entity.locX, entity.locY, entity.locZ, entity.yaw, entity.pitch);
    }

    public Location getLocation(Location loc) {
        if (loc != null) {
            loc.setWorld(getWorld());
            loc.setX(entity.locX);
            loc.setY(entity.locY);
            loc.setZ(entity.locZ);
            loc.setYaw(entity.yaw);
            loc.setPitch(entity.pitch);
        }

        return loc;
    }

    public Vector getVelocity() {
        return new Vector(entity.motX, entity.motY, entity.motZ);
    }

    public void setVelocity(Vector vel) {
        entity.motX = vel.getX();
        entity.motY = vel.getY();
        entity.motZ = vel.getZ();
        entity.velocityChanged = true;
    }

    public boolean isOnGround() {
        if (entity instanceof EntityArrow) {
            return ((EntityArrow) entity).isInGround();
        }
        return entity.onGround;
    }

    public World getWorld() {
        return entity.world.getWorld();
    }

    public boolean teleport(Location location) {
        return teleport(location, TeleportCause.PLUGIN);
    }

    public boolean teleport(Location location, TeleportCause cause) {
        if (entity.passenger != null || entity.dead) {
            return false;
        }

        // If this entity is riding another entity, we must dismount before teleporting.
        entity.mount(null);

        // Spigot start
        if (!location.getWorld().equals(getWorld())) {
          entity.teleportTo(location, cause.equals(TeleportCause.NETHER_PORTAL));
          return true;
        }

        // entity.world = ((CraftWorld) location.getWorld()).getHandle();
        // Spigot end
        entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        // entity.setLocation() throws no event, and so cannot be cancelled
        return true;
    }

    public boolean teleport(org.bukkit.entity.Entity destination) {
        return teleport(destination.getLocation());
    }

    public boolean teleport(org.bukkit.entity.Entity destination, TeleportCause cause) {
        return teleport(destination.getLocation(), cause);
    }

    public List<org.bukkit.entity.Entity> getNearbyEntities(double x, double y, double z) {
        @SuppressWarnings("unchecked")
        List<Entity> notchEntityList = entity.world.getEntities(entity, entity.getBoundingBox().grow(x, y, z));
        List<org.bukkit.entity.Entity> bukkitEntityList = new java.util.ArrayList<org.bukkit.entity.Entity>(notchEntityList.size());

        for (Entity e : notchEntityList) {
            bukkitEntityList.add(e.getBukkitEntity());
        }
        return bukkitEntityList;
    }

    public int getEntityId() {
        return entity.getId();
    }

    public int getFireTicks() {
        return entity.fireTicks;
    }

    public int getMaxFireTicks() {
        return entity.maxFireTicks;
    }

    public void setFireTicks(int ticks) {
        entity.fireTicks = ticks;
    }

    public void remove() {
        entity.dead = true;
    }

    public boolean isDead() {
        return !entity.isAlive();
    }

    public boolean isValid() {
        return entity.isAlive() && entity.valid;
    }

    public Server getServer() {
        return server;
    }

    public Vector getMomentum() {
        return getVelocity();
    }

    public void setMomentum(Vector value) {
        setVelocity(value);
    }

    public org.bukkit.entity.Entity getPassenger() {
        return isEmpty() ? null : getHandle().passenger.getBukkitEntity();
    }

    public boolean setPassenger(org.bukkit.entity.Entity passenger) {
        if (passenger instanceof CraftEntity) {
            ((CraftEntity) passenger).getHandle().mount(getHandle());
            return true;
        } else {
            return false;
        }
    }

    public boolean isEmpty() {
        return getHandle().passenger == null;
    }

    public boolean eject() {
        if (getHandle().passenger == null) {
            return false;
        }

        getHandle().passenger.mount(null);
        return true;
    }

    public float getFallDistance() {
        return getHandle().fallDistance;
    }

    public void setFallDistance(float distance) {
        getHandle().fallDistance = distance;
    }

    public void setLastDamageCause(EntityDamageEvent event) {
        lastDamageEvent = event;
    }

    public EntityDamageEvent getLastDamageCause() {
        return lastDamageEvent;
    }

    public UUID getUniqueId() {
        return getHandle().getUniqueID();
    }

    public int getTicksLived() {
        return getHandle().ticksLived;
    }

    public void setTicksLived(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Age must be at least 1 tick");
        }
        getHandle().ticksLived = value;
    }

    public Entity getHandle() {
        return entity;
    }

    public void playEffect(EntityEffect type) {
        this.getHandle().world.broadcastEntityEffect(getHandle(), type.getData());
    }

    public void setHandle(final Entity entity) {
        this.entity = entity;
    }

    @Override
    public String toString() {
        return "CraftEntity{" + "id=" + getEntityId() + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CraftEntity other = (CraftEntity) obj;
        return (this.getEntityId() == other.getEntityId());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.getEntityId();
        return hash;
    }

    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        server.getEntityMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    public List<MetadataValue> getMetadata(String metadataKey) {
        return server.getEntityMetadata().getMetadata(this, metadataKey);
    }

    public boolean hasMetadata(String metadataKey) {
        return server.getEntityMetadata().hasMetadata(this, metadataKey);
    }

    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        server.getEntityMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }

    public boolean isInsideVehicle() {
        return getHandle().vehicle != null;
    }

    public boolean leaveVehicle() {
        if (getHandle().vehicle == null) {
            return false;
        }

        getHandle().mount(null);
        return true;
    }

    public org.bukkit.entity.Entity getVehicle() {
        if (getHandle().vehicle == null) {
            return null;
        }

        return getHandle().vehicle.getBukkitEntity();
    }

    @Override
    public void setCustomName(String name) {
        if (name == null) {
            name = "";
        }

        getHandle().setCustomName(name);
    }

    @Override
    public String getCustomName() {
        String name = getHandle().getCustomName();

        if (name == null || name.length() == 0) {
            return null;
        }

        return name;
    }

    @Override
    public void setCustomNameVisible(boolean flag) {
        getHandle().setCustomNameVisible(flag);
    }

    @Override
    public boolean isCustomNameVisible() {
        return getHandle().getCustomNameVisible();
    }

    public String getName() {
        return getHandle().getName();
    }

    // Spigot start
    private final Spigot spigot = new Spigot()
    {
        @Override
        public boolean isInvulnerable()
        {
            return getHandle().isInvulnerable(net.minecraft.server.v1_8_R3.DamageSource.GENERIC);
        }
    };

    public Spigot spigot()
    {
        return spigot;
    }
    // Spigot end
}
