package net.minecraft.server.v1_8_R3;

// CraftBukkit start
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseEvent;
// CraftBukkit end

public abstract class DispenseBehaviorProjectile extends DispenseBehaviorItem {

    public DispenseBehaviorProjectile() {}

    public ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
        World world = isourceblock.i();
        IPosition iposition = BlockDispenser.a(isourceblock);
        EnumDirection enumdirection = BlockDispenser.b(isourceblock.f());
        IProjectile iprojectile = this.a(world, iposition);

        // iprojectile.shoot((double) enumdirection.getAdjacentX(), (double) ((float) enumdirection.getAdjacentY() + 0.1F), (double) enumdirection.getAdjacentZ(), this.b(), this.a());
        // CraftBukkit start
        ItemStack itemstack1 = itemstack.a(1);
        org.bukkit.block.Block block = world.getWorld().getBlockAt(isourceblock.getBlockPosition().getX(), isourceblock.getBlockPosition().getY(), isourceblock.getBlockPosition().getZ());
        CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

        BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector((double) enumdirection.getAdjacentX(), (double) ((float) enumdirection.getAdjacentY() + 0.1F), (double) enumdirection.getAdjacentZ()));
        if (!BlockDispenser.eventFired) {
            world.getServer().getPluginManager().callEvent(event);
        }

        if (event.isCancelled()) {
            itemstack.count++;
            return itemstack;
        }

        if (!event.getItem().equals(craftItem)) {
            itemstack.count++;
            // Chain to handler for new item
            ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
            IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.N.get(eventStack.getItem());
            if (idispensebehavior != IDispenseBehavior.a && idispensebehavior != this) {
                idispensebehavior.a(isourceblock, eventStack);
                return itemstack;
            }
        }

        iprojectile.shoot(event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ(), this.b(), this.a());
        ((Entity) iprojectile).projectileSource = new org.bukkit.craftbukkit.v1_8_R3.projectiles.CraftBlockProjectileSource((TileEntityDispenser) isourceblock.getTileEntity());
        // CraftBukkit end
        world.addEntity((Entity) iprojectile);
        // itemstack.a(1); // CraftBukkit - Handled during event processing
        return itemstack;
    }

    protected void a(ISourceBlock isourceblock) {
        isourceblock.i().triggerEffect(1002, isourceblock.getBlockPosition(), 0);
    }

    protected abstract IProjectile a(World world, IPosition iposition);

    protected float a() {
        return 6.0F;
    }

    protected float b() {
        return 1.1F;
    }
}
