package org.bukkit.craftbukkit.v1_8_R3.block;

import net.minecraft.server.v1_8_R3.BlockDropper;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.TileEntityDropper;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dropper;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.inventory.Inventory;

public class CraftDropper extends CraftBlockState implements Dropper {
    private final CraftWorld world;
    private final TileEntityDropper dropper;

    public CraftDropper(final Block block) {
        super(block);

        world = (CraftWorld) block.getWorld();
        dropper = (TileEntityDropper) world.getTileEntityAt(getX(), getY(), getZ());
    }

    public CraftDropper(final Material material, TileEntityDropper te) {
        super(material);
        world = null;
        dropper = te;
    }

    public Inventory getInventory() {
        return new CraftInventory(dropper);
    }

    public void drop() {
        Block block = getBlock();

        if (block.getType() == Material.DROPPER) {
            BlockDropper drop = (BlockDropper) Blocks.DROPPER;

            drop.dispense(world.getHandle(), new BlockPosition(getX(), getY(), getZ()));
        }
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);

        if (result) {
            dropper.update();
        }

        return result;
    }

    @Override
    public TileEntityDropper getTileEntity() {
        return dropper;
    }
}
