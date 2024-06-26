package net.minecraft.server.v1_8_R3;

// CraftBukkit start
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
// CraftBukkit end

public class ItemFlintAndSteel extends Item {

    public ItemFlintAndSteel() {
        this.maxStackSize = 1;
        this.setMaxDurability(64);
        this.a(CreativeModeTab.i);
    }

    public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2) {
        BlockPosition clicked = blockposition; // CraftBukkit
        blockposition = blockposition.shift(enumdirection);
        if (!entityhuman.a(blockposition, enumdirection, itemstack)) {
            return false;
        } else {
            if (world.getType(blockposition).getBlock().getMaterial() == Material.AIR) {
                // CraftBukkit start - Store the clicked block
                if (CraftEventFactory.callBlockIgniteEvent(world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), org.bukkit.event.block.BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL, entityhuman).isCancelled()) {
                    itemstack.damage(1, entityhuman);
                    return false;
                }

                CraftBlockState blockState = CraftBlockState.getBlockState(world, blockposition.getX(), blockposition.getY(), blockposition.getZ());
                // CraftBukkit end
                world.makeSound((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, "fire.ignite", 1.0F, ItemFlintAndSteel.g.nextFloat() * 0.4F + 0.8F);
                world.setTypeUpdate(blockposition, Blocks.FIRE.getBlockData());

                // CraftBukkit start
                org.bukkit.event.block.BlockPlaceEvent placeEvent = CraftEventFactory.callBlockPlaceEvent(world, entityhuman, blockState, clicked.getX(), clicked.getY(), clicked.getZ());

                if (placeEvent.isCancelled() || !placeEvent.canBuild()) {
                    placeEvent.getBlockPlaced().setTypeIdAndData(0, (byte) 0, false);
                    return false;
                }
                // CraftBukkit end
            }

            itemstack.damage(1, entityhuman);
            return true;
        }
    }
}
