package net.minecraft.server.v1_8_R3;

import org.bukkit.event.block.BlockRedstoneEvent; // CraftBukkit

public class BlockBloodStone extends Block {

    public BlockBloodStone() {
        super(Material.STONE);
        this.a(CreativeModeTab.b);
    }

    public MaterialMapColor g(IBlockData iblockdata) {
        return MaterialMapColor.K;
    }

    // CraftBukkit start
    @Override
    public void doPhysics(World world, BlockPosition position, IBlockData iblockdata, Block block) {
        if (block != null && block.isPowerSource()) {
            org.bukkit.block.Block bl = world.getWorld().getBlockAt(position.getX(), position.getY(), position.getZ());
            int power = bl.getBlockPower();

            BlockRedstoneEvent event = new BlockRedstoneEvent(bl, power, power);
            world.getServer().getPluginManager().callEvent(event);
        }
    }
    // CraftBukkit end
}
