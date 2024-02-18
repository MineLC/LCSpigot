package org.bukkit.craftbukkit.v1_8_R3.inventory;

import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_8_R3.InventorySubcontainer;

public class CraftInventoryEnchanting extends CraftInventory implements EnchantingInventory {
    public CraftInventoryEnchanting(InventorySubcontainer inventory) {
        super(inventory);
    }

    @Override
    public void setItem(ItemStack item) {
        setItem(0,item);
    }

    @Override
    public ItemStack getItem() {
        return getItem(0);
    }

    @Override
    public InventorySubcontainer getInventory() {
        return (InventorySubcontainer)inventory;
    }

    @Override
    public void setSecondary(ItemStack item) {
        setItem(1, item);
    }

    @Override
    public ItemStack getSecondary() {
        return getItem(1);
    }
}
