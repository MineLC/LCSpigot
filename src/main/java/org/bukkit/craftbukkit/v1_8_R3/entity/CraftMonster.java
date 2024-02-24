package org.bukkit.craftbukkit.v1_8_R3.entity;

import net.minecraft.server.v1_8_R3.EntityMonster;

import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Monster;

public class CraftMonster extends CraftCreature implements Monster {

    public CraftMonster(CraftServer server, EntityMonster entity) {
        super(server, entity);
    }

    @Override
    public EntityMonster getHandle() {
        return (EntityMonster) entity;
    }

    @Override
    public String toString() {
        return "CraftMonster";
    }
}