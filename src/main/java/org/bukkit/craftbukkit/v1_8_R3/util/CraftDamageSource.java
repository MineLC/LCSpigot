package org.bukkit.craftbukkit.v1_8_R3.util;

import net.minecraft.server.v1_8_R3.DamageSource;

// Util class to create custom DamageSources.
public final class CraftDamageSource extends DamageSource {
    public static DamageSource copyOf(final DamageSource original) {
        CraftDamageSource newSource = new CraftDamageSource(original.translationIndex);

        // Check ignoresArmor
        if (original.ignoresArmor()) {
            newSource.setIgnoreArmor();
        }

        // Check magic
        if (original.isMagic()) {
            newSource.setMagic();
        }

        // Check fire
        if (original.isExplosion()) {
            newSource.setExplosion();
        }

        return newSource;
    }

    private CraftDamageSource(String identifier) {
        super(identifier);
    }
}
