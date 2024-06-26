package net.minecraft.server.v1_8_R3;

public class ItemGlassBottle extends Item {

    public ItemGlassBottle() {
        this.a(CreativeModeTab.k);
    }

    public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
        MovingObjectPosition movingobjectposition = this.a(world, entityhuman, true);

        if (movingobjectposition == null) {
            return itemstack;
        } else {
            if (movingobjectposition.type == MovingObjectPosition.EnumMovingObjectType.BLOCK) {
                BlockPosition blockposition = movingobjectposition.a();

                if (!world.a(entityhuman, blockposition)) {
                    return itemstack;
                }

                if (!entityhuman.a(blockposition.shift(movingobjectposition.direction), movingobjectposition.direction, itemstack)) {
                    return itemstack;
                }

                if (world.getType(blockposition).getBlock().getMaterial() == Material.WATER) {
                    --itemstack.count;
                    if (itemstack.count <= 0) {
                        return new ItemStack(Items.POTION);
                    }

                    if (!entityhuman.inventory.pickup(new ItemStack(Items.POTION))) {
                        entityhuman.drop(new ItemStack(Items.POTION, 1, 0), false);
                    }
                }
            }

            return itemstack;
        }
    }
}
