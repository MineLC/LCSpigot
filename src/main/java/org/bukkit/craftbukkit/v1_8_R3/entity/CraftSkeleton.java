package org.bukkit.craftbukkit.v1_8_R3.entity;

import net.minecraft.server.v1_8_R3.EntitySkeleton;

import org.apache.commons.lang3.Validate;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;

public class CraftSkeleton extends CraftMonster implements Skeleton {

    public CraftSkeleton(CraftServer server, EntitySkeleton entity) {
        super(server, entity);
    }

    @Override
    public EntitySkeleton getHandle() {
        return (EntitySkeleton) entity;
    }

    @Override
    public String toString() {
        return "CraftSkeleton";
    }

    public EntityType getType() {
        return EntityType.SKELETON;
    }

    public SkeletonType getSkeletonType() {
        return SkeletonType.getType(getHandle().getSkeletonType());
    }

    public void setSkeletonType(SkeletonType type) {
        Validate.notNull(type);
        getHandle().setSkeletonType(type.getId());
    }
}
