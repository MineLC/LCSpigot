package net.minecraft.server.v1_8_R3;

import java.util.Random;

// CraftBukkit start
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.event.entity.EntityInteractEvent;
// CraftBukkit end

public class BlockRedstoneOre extends Block {

    private final boolean a;

    public BlockRedstoneOre(boolean flag) {
        super(Material.STONE);
        if (flag) {
            this.a(true);
        }

        this.a = flag;
    }

    public int a(World world) {
        return 30;
    }

    public void attack(World world, BlockPosition blockposition, EntityHuman entityhuman) {
        this.e(world, blockposition, entityhuman); // CraftBukkit - add entityhuman
        super.attack(world, blockposition, entityhuman);
    }

    public void a(World world, BlockPosition blockposition, Entity entity) {
        // CraftBukkit start
        // this.e(world, blockposition);
        // super.a(world, blockposition, entity);
        if (entity instanceof EntityHuman) {
            org.bukkit.event.player.PlayerInteractEvent event = org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory.callPlayerInteractEvent((EntityHuman) entity, org.bukkit.event.block.Action.PHYSICAL, blockposition, null, null);
            if (!event.isCancelled()) {
                this.e(world, blockposition, entity); // add entity
                super.a(world, blockposition, entity);
            }
        } else {
            EntityInteractEvent event = new EntityInteractEvent(entity.getBukkitEntity(), world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()));
            world.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                this.e(world, blockposition, entity); // add entity
                super.a(world, blockposition, entity);
            }
        }
        // CraftBukkit end
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2) {
        this.e(world, blockposition, entityhuman); // CraftBukkit - add entityhuman
        return super.interact(world, blockposition, iblockdata, entityhuman, enumdirection, f, f1, f2);
    }

    private void e(World world, BlockPosition blockposition, Entity entity) { // CraftBukkit - add Entity
        this.f(world, blockposition);
        if (this == Blocks.REDSTONE_ORE) {
            // CraftBukkit start
            if (CraftEventFactory.callEntityChangeBlockEvent(entity, blockposition.getX(), blockposition.getY(), blockposition.getZ(), Blocks.LIT_REDSTONE_ORE, 0).isCancelled()) {
                return;
            }
            // CraftBukkit end
            world.setTypeUpdate(blockposition, Blocks.LIT_REDSTONE_ORE.getBlockData());
        }

    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (this == Blocks.LIT_REDSTONE_ORE) {
            // CraftBukkit start
            if (CraftEventFactory.callBlockFadeEvent(world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()), Blocks.REDSTONE_ORE).isCancelled()) {
                return;
            }
            // CraftBukkit end
            world.setTypeUpdate(blockposition, Blocks.REDSTONE_ORE.getBlockData());
        }

    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Items.REDSTONE;
    }

    public int getDropCount(int i, Random random) {
        return this.a(random) + random.nextInt(i + 1);
    }

    public int a(Random random) {
        return 4 + random.nextInt(2);
    }

    public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {
        super.dropNaturally(world, blockposition, iblockdata, f, i);
        /* CraftBukkit start - Delegated to getExpDrop
        if (this.getDropType(iblockdata, world.random, i) != Item.getItemOf(this)) {
            int j = 1 + world.random.nextInt(5);

            this.dropExperience(world, blockposition, j);
        }
        // */
    }

    @Override
    public int getExpDrop(World world, IBlockData data, int i) {
        if (this.getDropType(data, i) != Item.getItemOf(this)) {
            int j = 1 + world.random.nextInt(5);

            return j;
        }
        return 0;
        // CraftBukkit end
    }

    private void f(World world, BlockPosition blockposition) {
    }

    protected ItemStack i(IBlockData iblockdata) {
        return new ItemStack(Blocks.REDSTONE_ORE);
    }
}
