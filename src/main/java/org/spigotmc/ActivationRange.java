package org.spigotmc;

import java.util.List;

import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityAmbient;
import net.minecraft.server.v1_8_R3.EntityArrow;
import net.minecraft.server.v1_8_R3.EntityComplexPart;
import net.minecraft.server.v1_8_R3.EntityFireball;
import net.minecraft.server.v1_8_R3.EntityFireworks;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityProjectile;
import net.minecraft.server.v1_8_R3.EntityTNTPrimed;
import net.minecraft.server.v1_8_R3.EntityWeather;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.World;

public class ActivationRange
{

    static AxisAlignedBB maxBB = AxisAlignedBB.a( 0, 0, 0, 0, 0, 0 );
    static AxisAlignedBB miscBB = AxisAlignedBB.a( 0, 0, 0, 0, 0, 0 );
    static AxisAlignedBB animalBB = AxisAlignedBB.a( 0, 0, 0, 0, 0, 0 );
    static AxisAlignedBB monsterBB = AxisAlignedBB.a( 0, 0, 0, 0, 0, 0 );

    /**
     * Initializes an entities type on construction to specify what group this
     * entity is in for activation ranges.
     *
     * @param entity
     * @return group id
     */
    public static byte initializeEntityActivationType(Entity entity)
    {
        if (entity instanceof EntityAmbient )
        {
            return 2; // Animal
        } else
        {
            return 3; // Misc
        }
    }

    /**
     * These entities are excluded from Activation range checks.
     *
     * @param entity
     * @param world
     * @return boolean If it should always tick.
     */
    public static boolean initializeEntityActivationState(Entity entity, SpigotWorldConfig config)
    {
        if ( ( entity.activationType == 3 && config.miscActivationRange == 0 )
                || ( entity.activationType == 2 && config.animalActivationRange == 0 )
                || ( entity.activationType == 1 && config.monsterActivationRange == 0 )
                || entity instanceof EntityHuman
                || entity instanceof EntityProjectile
                || entity instanceof EntityComplexPart
                || entity instanceof EntityFireball
                || entity instanceof EntityWeather
                || entity instanceof EntityTNTPrimed
                || entity instanceof EntityFireworks )
        {
            return true;
        }

        return false;
    }

    /**
     * Find what entities are in range of the players in the world and set
     * active if in range.
     *
     * @param world
     */
    public static void activateEntities(World world)
    {
        final int miscActivationRange = world.spigotConfig.miscActivationRange;
        final int animalActivationRange = world.spigotConfig.animalActivationRange;
        final int monsterActivationRange = world.spigotConfig.monsterActivationRange;

        int maxRange = Math.max( monsterActivationRange, animalActivationRange );
        maxRange = Math.max( maxRange, miscActivationRange );
        maxRange = Math.min( ( world.spigotConfig.viewDistance << 4 ) - 8, maxRange );

        for (Entity player : world.players){
            player.activatedTick = MinecraftServer.currentTick;
            maxBB = player.getBoundingBox().grow( maxRange, 256, maxRange );
            miscBB = player.getBoundingBox().grow( miscActivationRange, miscActivationRange, miscActivationRange );
            animalBB = player.getBoundingBox().grow( animalActivationRange, animalActivationRange, animalActivationRange );
            monsterBB = player.getBoundingBox().grow( monsterActivationRange, monsterActivationRange, monsterActivationRange );

            int i = MathHelper.floor( maxBB.a / 16.0D );
            int j = MathHelper.floor( maxBB.d / 16.0D );
            int k = MathHelper.floor( maxBB.c / 16.0D );
            int l = MathHelper.floor( maxBB.f / 16.0D );

            for ( int i1 = i; i1 <= j; ++i1 )
            {
                for ( int j1 = k; j1 <= l; ++j1 )
                {
                    final Chunk chunk = world.getChunkIfLoaded( i1, j1 );
                    if (chunk != null) {
                        activateChunkEntities(chunk);
                    }
                }
            }
        }
    }

    /**
     * Checks for the activation state of all entities in this chunk.
     *
     * @param chunk
     */
    private static void activateChunkEntities(Chunk chunk)
    {
        final List<Entity>[] slices = chunk.entitySlices;
        for (final List<Entity> slice : slices) {
            for (final Entity entity : slice ) {
                if ( MinecraftServer.currentTick > entity.activatedTick ) {
                    if ( entity.defaultActivationState ) {
                        entity.activatedTick = MinecraftServer.currentTick;
                        continue;
                    }
                    switch ( entity.activationType ) {
                        case 1:
                            if (monsterBB.b(entity.getBoundingBox())){
                                entity.activatedTick = MinecraftServer.currentTick;
                            }
                            break;
                        case 2:
                            if (animalBB.b(entity.getBoundingBox())){
                                entity.activatedTick = MinecraftServer.currentTick;
                            }
                            break;
                        case 3:
                        default:
                            if (miscBB.b(entity.getBoundingBox())){
                                entity.activatedTick = MinecraftServer.currentTick;
                            }
                            break;
                    }
                }
            }
        }
    }

    /**
     * If an entity is not in range, do some more checks to see if we should
     * give it a shot.
     *
     * @param entity
     * @return
     */
    public static boolean checkEntityImmunities(Entity entity)
    {
        // quick checks.
        if ( entity.inWater || entity.fireTicks > 0 )
        {
            return true;
        }
        if ( !( entity instanceof EntityArrow ) )
        {
            if ( !entity.onGround || entity.passenger != null
                    || entity.vehicle != null )
            {
                return true;
            }
        } else if ( !( (EntityArrow) entity ).inGround )
        {
            return true;
        }
        // special cases.
        if ( entity instanceof EntityLiving )
        {
            EntityLiving living = (EntityLiving) entity;
            if ( /*TODO: Missed mapping? living.attackTicks > 0 || */ living.hurtTicks > 0 || living.effects.size() > 0 )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the entity is active for this tick.
     *
     * @param entity
     * @return
     */
    public static boolean checkIfActive(Entity entity)
    {
        // Never safe to skip fireworks or entities not yet added to chunk
        if ( !entity.isAddedToChunk() || entity instanceof EntityFireworks ) {
            return true;
        }

        boolean isActive = entity.activatedTick >= MinecraftServer.currentTick || entity.defaultActivationState;

        // Should this entity tick?
        if ( !isActive )
        {
            if ( ( MinecraftServer.currentTick - entity.activatedTick - 1 ) % 20 == 0 )
            {
                // Check immunities every 20 ticks.
                if ( checkEntityImmunities( entity ) )
                {
                    // Triggered some sort of immunity, give 20 full ticks before we check again.
                    entity.activatedTick = MinecraftServer.currentTick + 20;
                }
                isActive = true;
            }
            // Add a little performance juice to active entities. Skip 1/4 if not immune.
        } else if ( !entity.defaultActivationState && entity.ticksLived % 4 == 0 && !checkEntityImmunities( entity ) )
        {
            isActive = false;
        }
        int x = MathHelper.floor( entity.locX );
        int z = MathHelper.floor( entity.locZ );
        // Make sure not on edge of unloaded chunk
        Chunk chunk = entity.world.getChunkIfLoaded( x >> 4, z >> 4 );
        if ( isActive && !( chunk != null && chunk.areNeighborsLoaded( 1 ) ) )
        {
            isActive = false;
        }
        return isActive;
    }
}
