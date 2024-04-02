package net.minecraft.server.v1_8_R3;

public class ItemMapEmpty extends ItemWorldMapBase {

    protected ItemMapEmpty() {
        this.a(CreativeModeTab.f);
    }

    public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
        
        return itemstack;
    }
}
