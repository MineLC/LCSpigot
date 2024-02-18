package net.minecraft.server.v1_8_R3;

public interface IRecipe {

    boolean a(InventoryCrafting inventorycrafting, World world);

    ItemStack a(InventoryCrafting inventorycrafting);

    int a();

    ItemStack b();

    ItemStack[] b(InventoryCrafting inventorycrafting);

    org.bukkit.inventory.Recipe toBukkitRecipe(); // CraftBukkit

    java.util.List<ItemStack> getIngredients(); // Spigot
}
