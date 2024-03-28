package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;

import gnu.trove.map.hash.TIntShortHashMap;
import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lc.lcspigot.commands.CommandStorage;
import lc.lcspigot.events.PreInteractEntityEvent;
import lc.lcspigot.listeners.internal.EventsExecutor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.tinylog.Logger;

// CraftBukkit start
import java.util.HashSet;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.craftbukkit.v1_8_R3.util.LazyPlayerSet;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.util.NumberConversions;
// CraftBukkit end

public class PlayerConnection implements PacketListenerPlayIn, IUpdatePlayerListBox {

    public final NetworkManager networkManager;
    private final MinecraftServer minecraftServer;
    public EntityPlayer player;
    private int e;
    private int f;
    private int i;
    private long j;
    private long k;
    private int m;
    private TIntShortHashMap n = new TIntShortHashMap();
    private double o;
    private double p;
    private double q;
    private boolean checkMovement = true;
    private boolean processedDisconnect; // CraftBukkit - added

    public PlayerConnection(MinecraftServer minecraftserver, NetworkManager networkmanager, EntityPlayer entityplayer) {
        this.minecraftServer = minecraftserver;
        this.networkManager = networkmanager;
        networkmanager.a((PacketListener) this);
        this.player = entityplayer;
        entityplayer.playerConnection = this;

        // CraftBukkit start - add fields and methods
        this.server = minecraftserver.server;
    }

    private final org.bukkit.craftbukkit.v1_8_R3.CraftServer server;
    private int lastDropTick = MinecraftServer.currentTick;
    private int dropCount = 0;
    private static final int SURVIVAL_PLACE_DISTANCE_SQUARED = 6 * 6;
    private static final int CREATIVE_PLACE_DISTANCE_SQUARED = 7 * 7;

    // Get position of last block hit for BlockDamageLevel.STOPPED

    public CraftPlayer getPlayer() {
        return (this.player == null) ? null : (CraftPlayer) this.player.getBukkitEntity();
    }
    private final static HashSet<Integer> invalidItems = new HashSet<Integer>(java.util.Arrays.asList(8, 9, 10, 11, 26, 34, 36, 43, 51, 52, 55, 59, 60, 62, 63, 64, 68, 71, 74, 75, 83, 90, 92, 93, 94, 104, 105, 115, 117, 118, 119, 125, 127, 132, 140, 141, 142, 144)); // TODO: Check after every update.
    // CraftBukkit end

    public void c() {
        ++this.e;
        this.minecraftServer.methodProfiler.a("keepAlive");
        if ((long) this.e - this.k > 40L) {
            this.k = (long) this.e;
            this.j = this.d();
            this.i = (int) this.j;
            this.sendPacket(new PacketPlayOutKeepAlive(this.i));
        }

        this.minecraftServer.methodProfiler.b();
        // CraftBukkit start
        /* Use thread-safe field access instead
        if (this.chatThrottle > 0) {
            --this.chatThrottle;
        }
        */
        // CraftBukkit end

        if (this.m > 0) {
            --this.m;
        }

        if (this.player.D() > 0L && this.minecraftServer.getIdleTimeout() > 0 && MinecraftServer.az() - this.player.D() > (long) (this.minecraftServer.getIdleTimeout() * 1000 * 60)) {
            this.player.resetIdleTimer(); // CraftBukkit - SPIGOT-854
            this.disconnect("You have been idle for too long!");
        }

    }

    public NetworkManager a() {
        return this.networkManager;
    }

    public void disconnect(String s) {
        // CraftBukkit start - fire PlayerKickEvent
        String leaveMessage = EnumChatFormat.YELLOW + this.player.getName() + " left the game.";

        PlayerKickEvent event = new PlayerKickEvent(this.server.getPlayer(this.player), s, leaveMessage);

        if (this.server.getServer().isRunning()) {
            this.server.getPluginManager().callEvent(event);
        }

        if (event.isCancelled()) {
            // Do not kick the player
            return;
        }
        // Send the possibly modified leave message
        s = event.getReason();
        // CraftBukkit end
        final ChatComponentText chatcomponenttext = new ChatComponentText(s);

        this.networkManager.a(new PacketPlayOutKickDisconnect(chatcomponenttext), new GenericFutureListener() {
            public void operationComplete(Future future) throws Exception { // CraftBukkit - fix decompile error
                PlayerConnection.this.networkManager.close(chatcomponenttext);
            }
        }, new GenericFutureListener[0]);
        this.a(chatcomponenttext); // CraftBukkit - fire quit instantly
        this.networkManager.k();
        // CraftBukkit - Don't wait
        this.minecraftServer.postToMainThread(new Runnable() {
             public void run() {
                 PlayerConnection.this.networkManager.l();
            }
        });
    }

    public void a(PacketPlayInSteerVehicle packetplayinsteervehicle) {
        PlayerConnectionUtils.ensureMainThread(packetplayinsteervehicle, this, this.player.u());
        this.player.a(packetplayinsteervehicle.a(), packetplayinsteervehicle.b(), packetplayinsteervehicle.c(), packetplayinsteervehicle.d());
    }

    public void a(PacketPlayInFlying packetplayinflying) {
        PlayerConnectionUtils.ensureMainThread(packetplayinflying, this, this.player.u());
        final boolean moveCord = !(packetplayinflying.x == 0 && packetplayinflying.y == 0 && packetplayinflying.z == 0);
        final boolean moveHead = !(packetplayinflying.pitch == 0 && packetplayinflying.yaw == 0);

        if ((!moveCord && !moveHead) || this.player.viewingCredits) {
            return;
        }
        if (!moveCord && moveHead) {
            if (!NumberConversions.isFinite(packetplayinflying.yaw)
                || !NumberConversions.isFinite(packetplayinflying.pitch)) {
                Logger.warn(player.getName() + " was caught trying to crash the server with an invalid position.");
                getPlayer().kickPlayer("NaN in position (Hacking?)"); //Spigot "Nope" -> Descriptive reason
                return;
            }
            float f2 = player.yaw;
            float f3 = player.pitch;
            if (packetplayinflying.h()) {
                f2 = packetplayinflying.d();
                f3 = packetplayinflying.e();
            }
            this.player.moveHead(f2, f3);
            return;
        }

        if (!NumberConversions.isFinite(packetplayinflying.x) 
                || !NumberConversions.isFinite(packetplayinflying.y)
                || !NumberConversions.isFinite(packetplayinflying.z)
                || !NumberConversions.isFinite(packetplayinflying.yaw)
                || !NumberConversions.isFinite(packetplayinflying.pitch)) {
            Logger.warn(player.getName() + " was caught trying to crash the server with an invalid position.");
            getPlayer().kickPlayer("NaN in position (Hacking?)"); //Spigot "Nope" -> Descriptive reason
            return;
        }
        // CraftBukkit end

        double d0 = this.player.locX;
        double d1 = this.player.locY;
        double d2 = this.player.locZ;
        double d3 = 0.0D;
        double d4 = packetplayinflying.a() - this.o;
        double d5 = packetplayinflying.b() - this.p;
        double d6 = packetplayinflying.c() - this.q;

        if (packetplayinflying.g()) {
            d3 = d4 * d4 + d5 * d5 + d6 * d6;
            if (!this.checkMovement && d3 < 0.25D) {
                this.checkMovement = true;
            }
        }
        if (!this.checkMovement || this.player.dead) {
            if (this.e - this.f > 20) {
                this.a(this.o, this.p, this.q, this.player.yaw, this.player.pitch);
            }
            return;
        }

        // CraftBukkit end
        this.f = this.e;
        double d7;
        double d8;
        double d9;

        if (this.player.vehicle != null) {
            float f = this.player.yaw;
            float f1 = this.player.pitch;

            this.player.vehicle.al();
            d7 = this.player.locX;
            d8 = this.player.locY;
            d9 = this.player.locZ;
            if (packetplayinflying.h()) {
                f = packetplayinflying.d();
                f1 = packetplayinflying.e();
            }

            this.player.onGround = packetplayinflying.f();
            this.player.l();
            this.player.setLocation(d7, d8, d9, f, f1);
            if (this.player.vehicle != null) {
                this.player.vehicle.al();
            }

            this.minecraftServer.getPlayerList().d(this.player);
            if (this.player.vehicle != null) {
                this.player.vehicle.ai = true; // CraftBukkit - moved from below
                if (d3 > 4.0D) {
                    Entity entity = this.player.vehicle;

                    this.player.playerConnection.sendPacket(new PacketPlayOutEntityTeleport(entity));
                    this.a(this.player.locX, this.player.locY, this.player.locZ, this.player.yaw, this.player.pitch);
                }

                // this.player.vehicle.ai = true; // CraftBukkit - moved up
            }

            if (this.checkMovement) {
                this.o = this.player.locX;
                this.p = this.player.locY;
                this.q = this.player.locZ;
            }
            WorldServer worldserver = this.minecraftServer.getWorldServer(this.player.dimension);
            worldserver.g(this.player);
            return;
        }

        if (this.player.isSleeping()) {
            this.player.l();
            this.player.setLocation(this.o, this.p, this.q, this.player.yaw, this.player.pitch);
            WorldServer worldserver = this.minecraftServer.getWorldServer(this.player.dimension);
            worldserver.g(this.player);
            return;
        }

        double d10 = this.player.locY;

        this.o = this.player.locX;
        this.p = this.player.locY;
        this.q = this.player.locZ;
        d7 = this.player.locX;
        d8 = this.player.locY;
        d9 = this.player.locZ;
        float f2 = this.player.yaw;
        float f3 = this.player.pitch;

        if (packetplayinflying.g() && packetplayinflying.b() == -999.0D) {
            packetplayinflying.a(false);
        }

        if (packetplayinflying.g()) {
            d7 = packetplayinflying.a();
            d8 = packetplayinflying.b();
            d9 = packetplayinflying.c();
            if (Math.abs(packetplayinflying.a()) > 3.0E7D || Math.abs(packetplayinflying.c()) > 3.0E7D) {
                this.disconnect("Illegal position");
                return;
            }
        }

        if (packetplayinflying.h()) {
            f2 = packetplayinflying.d();
            f3 = packetplayinflying.e();
        }

        this.player.l();
        this.player.setLocation(this.o, this.p, this.q, f2, f3);
        if (!this.checkMovement) {
            return;
        }

        double d11 = d7 - this.player.locX;
        double d12 = d8 - this.player.locY;
        double d13 = d9 - this.player.locZ;

        if (this.player.onGround && !packetplayinflying.f() && d12 > 0.0D) {
            this.player.bF();
        }

        this.player.moveNoClip(d11, d12, d13);
        this.player.onGround = packetplayinflying.f();

        boolean flag1 = false;

        this.player.setLocation(d7, d8, d9, f2, f3);
        this.player.checkMovement(this.player.locX - d0, this.player.locY - d1, this.player.locZ - d2);
        if (!this.player.noclip) {
            final BlockPosition position = new BlockPosition(player.locX, player.locY, player.locZ);
            final WorldServer worldserver = this.minecraftServer.getWorldServer(this.player.dimension);
            if ((flag1 || !worldserver.getWorldBorder().a(position) || worldserver.getType(position).getBlock().u()) && !this.player.isSleeping()) {
                this.a(this.o, this.p, this.q, f2, f3);
                return;
            }
        }
        this.player.onGround = packetplayinflying.f();
        this.minecraftServer.getPlayerList().d(this.player);
        this.player.a(this.player.locY - d10, packetplayinflying.f());
    }

    public void a(double d0, double d1, double d2, float f, float f1) {
        this.a(d0, d1, d2, f, f1, Collections.<PacketPlayOutPosition.EnumPlayerTeleportFlags>emptySet()); // CraftBukkit fix decompile errors
    }

    public void a(double d0, double d1, double d2, float f, float f1, Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> set) {
        // CraftBukkit start - Delegate to teleport(Location)
        Player player = this.getPlayer();
        Location from = player.getLocation();
        Location to = new Location(this.getPlayer().getWorld(), d0, d1, d2, f, f1);
        PlayerTeleportEvent event = new PlayerTeleportEvent(player, from, to, PlayerTeleportEvent.TeleportCause.UNKNOWN);
        this.server.getPluginManager().callEvent(event);

        from = event.getFrom();
        to = event.isCancelled() ? from : event.getTo();

        this.teleport(to, set);
    }

    public void teleport(Location dest) {
        teleport(dest, Collections.emptySet());
    }

    public void teleport(Location dest, Set set) {
        double d0, d1, d2;
        float f, f1;

        d0 = dest.getX();
        d1 = dest.getY();
        d2 = dest.getZ();
        f = dest.getYaw();
        f1 = dest.getPitch();

        // TODO: make sure this is the best way to address this.
        if (Float.isNaN(f)) {
            f = 0;
        }

        if (Float.isNaN(f1)) {
            f1 = 0;
        }
        // CraftBukkit end
        this.checkMovement = false;
        this.o = d0;
        this.p = d1;
        this.q = d2;
        if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.X)) {
            this.o += this.player.locX;
        }

        if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y)) {
            this.p += this.player.locY;
        }

        if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.Z)) {
            this.q += this.player.locZ;
        }

        float f2 = f;
        float f3 = f1;

        if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y_ROT)) {
            f2 = f + this.player.yaw;
        }

        if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.X_ROT)) {
            f3 = f1 + this.player.pitch;
        }

        this.player.setLocation(this.o, this.p, this.q, f2, f3);
        this.player.playerConnection.sendPacket(new PacketPlayOutPosition(d0, d1, d2, f, f1, set));
    }

    public void a(PacketPlayInBlockDig packetplayinblockdig) {
        PlayerConnectionUtils.ensureMainThread(packetplayinblockdig, this, this.player.u());
        if (this.player.dead) return; // CraftBukkit
        WorldServer worldserver = this.minecraftServer.getWorldServer(this.player.dimension);
        BlockPosition blockposition = packetplayinblockdig.a();

        this.player.resetIdleTimer();
        // CraftBukkit start
        switch (PlayerConnection.SyntheticClass_1.a[packetplayinblockdig.c().ordinal()]) {
        case 1: // DROP_ITEM
            if (!this.player.isSpectator()) {
                // limit how quickly items can be dropped
                // If the ticks aren't the same then the count starts from 0 and we update the lastDropTick.
                if (this.lastDropTick != MinecraftServer.currentTick) {
                    this.dropCount = 0;
                    this.lastDropTick = MinecraftServer.currentTick;
                } else {
                    // Else we increment the drop count and check the amount.
                    this.dropCount++;
                    if (this.dropCount >= 20) {
                        Logger.warn(this.player.getName() + " dropped their items too quickly!");
                        this.disconnect("You dropped your items too quickly (Hacking?)");
                        return;
                    }
                }
                // CraftBukkit end
                this.player.a(false);
            }

            return;

        case 2: // DROP_ALL_ITEMS
            if (!this.player.isSpectator()) {
                this.player.a(true);
            }

            return;

        case 3: // RELEASE_USE_ITEM
            this.player.bU();
            return;

        case 4: // START_DESTROY_BLOCK
        case 5: // ABORT_DESTROY_BLOCK
        case 6: // STOP_DESTROY_BLOCK
            double d0 = this.player.locX - ((double) blockposition.getX() + 0.5D);
            double d1 = this.player.locY - ((double) blockposition.getY() + 0.5D) + 1.5D;
            double d2 = this.player.locZ - ((double) blockposition.getZ() + 0.5D);
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;

            if (d3 > 36.0D) {
                return;
            } else if (blockposition.getY() >= this.minecraftServer.getMaxBuildHeight()) {
                return;
            } else {
                if (packetplayinblockdig.c() == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK) {
                    if (!this.minecraftServer.a(worldserver, blockposition, this.player) && worldserver.getWorldBorder().a(blockposition)) {
                        this.player.playerInteractManager.a(blockposition, packetplayinblockdig.b());
                    } else {
                        // CraftBukkit start - fire PlayerInteractEvent
                        CraftEventFactory.callPlayerInteractEvent(this.player, Action.LEFT_CLICK_BLOCK, blockposition, packetplayinblockdig.b(), this.player.inventory.getItemInHand());
                        this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(worldserver, blockposition));
                        // Update any tile entity data for this block
                        TileEntity tileentity = worldserver.getTileEntity(blockposition);
                        if (tileentity != null) {
                            this.player.playerConnection.sendPacket(tileentity.getUpdatePacket());
                        }
                        // CraftBukkit end
                    }
                } else {
                    if (packetplayinblockdig.c() == PacketPlayInBlockDig.EnumPlayerDigType.STOP_DESTROY_BLOCK) {
                        this.player.playerInteractManager.a(blockposition);
                    } else if (packetplayinblockdig.c() == PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
                        this.player.playerInteractManager.e();
                    }

                    if (worldserver.getType(blockposition).getBlock().getMaterial() != Material.AIR) {
                        this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(worldserver, blockposition));
                    }
                }

                return;
            }

        default:
            throw new IllegalArgumentException("Invalid player action");
        }
        // CraftBukkit end
    }

    // Spigot start - limit place/interactions
    private long lastPlace = -1;
    private int packets = 0;

    public void a(PacketPlayInBlockPlace packetplayinblockplace) {
        PlayerConnectionUtils.ensureMainThread(packetplayinblockplace, this, this.player.u());
        WorldServer worldserver = this.minecraftServer.getWorldServer(this.player.dimension);
        boolean throttled = false;
        if (lastPlace != -1 && packetplayinblockplace.timestamp - lastPlace < 30 && packets++ >= 4) {
            throttled = true;
        } else if ( packetplayinblockplace.timestamp - lastPlace >= 30 || lastPlace == -1 )
        {
            lastPlace = packetplayinblockplace.timestamp;
            packets = 0;
        }
    // Spigot end

        // CraftBukkit start
        if (this.player.dead) return;

        // CraftBukkit - if rightclick decremented the item, always send the update packet. */
        // this is not here for CraftBukkit's own functionality; rather it is to fix
        // a notch bug where the item doesn't update correctly.
        boolean always = false;
        // CraftBukkit end

        ItemStack itemstack = this.player.inventory.getItemInHand();
        boolean flag = false;
        BlockPosition blockposition = packetplayinblockplace.a();
        EnumDirection enumdirection = EnumDirection.fromType1(packetplayinblockplace.getFace());

        this.player.resetIdleTimer();
        if (packetplayinblockplace.getFace() == 255) {
            if (itemstack == null) {
                return;
            }

            // CraftBukkit start
            int itemstackAmount = itemstack.count;
            // Spigot start - skip the event if throttled
            if (!throttled) {            
            // Raytrace to look for 'rogue armswings'
            float f1 = this.player.pitch;
            float f2 = this.player.yaw;
            double d0 = this.player.locX;
            double d1 = this.player.locY + (double) this.player.getHeadHeight();
            double d2 = this.player.locZ;
            Vec3D vec3d = new Vec3D(d0, d1, d2);

            float f3 = MathHelper.cos(-f2 * 0.017453292F - 3.1415927F);
            float f4 = MathHelper.sin(-f2 * 0.017453292F - 3.1415927F);
            float f5 = -MathHelper.cos(-f1 * 0.017453292F);
            float f6 = MathHelper.sin(-f1 * 0.017453292F);
            float f7 = f4 * f5;
            float f8 = f3 * f5;
            double d3 = player.playerInteractManager.getGameMode() == WorldSettings.EnumGamemode.CREATIVE ? 5.0D : 4.5D;
            Vec3D vec3d1 = vec3d.add((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
            MovingObjectPosition movingobjectposition = this.player.world.rayTrace(vec3d, vec3d1, false);

            boolean cancelled = false;
            if (movingobjectposition == null || movingobjectposition.type != MovingObjectPosition.EnumMovingObjectType.BLOCK) {
                org.bukkit.event.player.PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent(this.player, Action.RIGHT_CLICK_AIR, itemstack);
                cancelled = event.useItemInHand() == Event.Result.DENY;
            } else {
                if (player.playerInteractManager.firedInteract) {
                    player.playerInteractManager.firedInteract = false;
                    cancelled = player.playerInteractManager.interactResult;
                } else {
                    org.bukkit.event.player.PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, movingobjectposition.a(), movingobjectposition.direction, itemstack, true);
                    cancelled = event.useItemInHand() == Event.Result.DENY;
                }
            }

            if (!cancelled) {
                this.player.playerInteractManager.useItem(this.player, this.player.world, itemstack);
            }
            }
            // Spigot end

            // CraftBukkit - notch decrements the counter by 1 in the above method with food,
            // snowballs and so forth, but he does it in a place that doesn't cause the
            // inventory update packet to get sent
            always = (itemstack.count != itemstackAmount) || itemstack.getItem() == Item.getItemOf(Blocks.WATERLILY);
            // CraftBukkit end
        } else if (blockposition.getY() >= this.minecraftServer.getMaxBuildHeight() - 1 && (enumdirection == EnumDirection.UP || blockposition.getY() >= this.minecraftServer.getMaxBuildHeight())) {
            ChatMessage chatmessage = new ChatMessage("build.tooHigh", new Object[] { Integer.valueOf(this.minecraftServer.getMaxBuildHeight())});

            chatmessage.getChatModifier().setColor(EnumChatFormat.RED);
            this.player.playerConnection.sendPacket(new PacketPlayOutChat(chatmessage));
            flag = true;
        } else {
            // CraftBukkit start - Check if we can actually do something over this large a distance
            Location eyeLoc = this.getPlayer().getEyeLocation();
            double reachDistance = NumberConversions.square(eyeLoc.getX() - blockposition.getX()) + NumberConversions.square(eyeLoc.getY() - blockposition.getY()) + NumberConversions.square(eyeLoc.getZ() - blockposition.getZ());
            if (reachDistance > (this.getPlayer().getGameMode() == org.bukkit.GameMode.CREATIVE ? CREATIVE_PLACE_DISTANCE_SQUARED : SURVIVAL_PLACE_DISTANCE_SQUARED)) {
                return;
            }

            if (!worldserver.getWorldBorder().a(blockposition)) {
                return;
            }

            if (this.checkMovement && this.player.e((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D) < 64.0D && !this.minecraftServer.a(worldserver, blockposition, this.player) && worldserver.getWorldBorder().a(blockposition)) {
                always = throttled || !this.player.playerInteractManager.interact(this.player, worldserver, itemstack, blockposition, enumdirection, packetplayinblockplace.d(), packetplayinblockplace.e(), packetplayinblockplace.f());
            }

            flag = true;
        }

        if (flag) {
            this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(worldserver, blockposition));
            this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(worldserver, blockposition.shift(enumdirection)));
        }

        itemstack = this.player.inventory.getItemInHand();
        if (itemstack != null && itemstack.count == 0) {
            this.player.inventory.items[this.player.inventory.itemInHandIndex] = null;
            itemstack = null;
        }

        if (itemstack == null || itemstack.l() == 0) {
            this.player.g = true;
            this.player.inventory.items[this.player.inventory.itemInHandIndex] = ItemStack.b(this.player.inventory.items[this.player.inventory.itemInHandIndex]);
            Slot slot = this.player.activeContainer.getSlot(this.player.inventory, this.player.inventory.itemInHandIndex);

            this.player.activeContainer.b();
            this.player.g = false;
            // CraftBukkit - TODO CHECK IF NEEDED -- new if structure might not need 'always'. Kept it in for now, but may be able to remove in future
            if (!ItemStack.matches(this.player.inventory.getItemInHand(), packetplayinblockplace.getItemStack()) || always) {
                this.sendPacket(new PacketPlayOutSetSlot(this.player.activeContainer.windowId, slot.rawSlotIndex, this.player.inventory.getItemInHand()));
            }
        }

    }

    public void a(PacketPlayInSpectate packetplayinspectate) {
        PlayerConnectionUtils.ensureMainThread(packetplayinspectate, this, this.player.u());
        if (this.player.isSpectator()) {
            Entity entity = null;

            // CraftBukkit - use the worlds array list
            for (WorldServer worldserver : minecraftServer.worlds) {

                if (worldserver != null) {
                    entity = packetplayinspectate.a(worldserver);
                    if (entity != null) {
                        break;
                    }
                }
            }

            if (entity != null) {
                this.player.setSpectatorTarget(this.player);
                this.player.mount((Entity) null);

                /* CraftBukkit start - replace with bukkit handling for multi-world
                if (entity.world != this.player.world) {
                    WorldServer worldserver1 = this.player.u();
                    WorldServer worldserver2 = (WorldServer) entity.world;

                    this.player.dimension = entity.dimension;
                    this.sendPacket(new PacketPlayOutRespawn(this.player.dimension, worldserver1.getDifficulty(), worldserver1.getWorldData().getType(), this.player.playerInteractManager.getGameMode()));
                    worldserver1.removeEntity(this.player);
                    this.player.dead = false;
                    this.player.setPositionRotation(entity.locX, entity.locY, entity.locZ, entity.yaw, entity.pitch);
                    if (this.player.isAlive()) {
                        worldserver1.entityJoinedWorld(this.player, false);
                        worldserver2.addEntity(this.player);
                        worldserver2.entityJoinedWorld(this.player, false);
                    }

                    this.player.spawnIn(worldserver2);
                    this.minecraftServer.getPlayerList().a(this.player, worldserver1);
                    this.player.enderTeleportTo(entity.locX, entity.locY, entity.locZ);
                    this.player.playerInteractManager.a(worldserver2);
                    this.minecraftServer.getPlayerList().b(this.player, worldserver2);
                    this.minecraftServer.getPlayerList().updateClient(this.player);
                } else {
                    this.player.enderTeleportTo(entity.locX, entity.locY, entity.locZ);
                }
                */
                this.player.getBukkitEntity().teleport(entity.getBukkitEntity(), PlayerTeleportEvent.TeleportCause.SPECTATE);
                // CraftBukkit end
            }
        }

    }

    public void a(PacketPlayInResourcePackStatus packetplayinresourcepackstatus) {}

    public void a(IChatBaseComponent ichatbasecomponent) {
        // CraftBukkit start - Rarely it would send a disconnect line twice
        if (this.processedDisconnect) {
            return;
        } else {
            this.processedDisconnect = true;
        }
        // CraftBukkit end
        Logger.info(this.player.getName() + " lost connection: " + ichatbasecomponent);
        // CraftBukkit start - Replace vanilla quit message handling with our own.
        /*
        this.minecraftServer.aH();
        ChatMessage chatmessage = new ChatMessage("multiplayer.player.left", new Object[] { this.player.getScoreboardDisplayName()});

        chatmessage.getChatModifier().setColor(EnumChatFormat.YELLOW);
        this.minecraftServer.getPlayerList().sendMessage(chatmessage);
        */

        this.player.q();
        String quitMessage = this.minecraftServer.getPlayerList().disconnect(this.player);
        if ((quitMessage != null) && (quitMessage.length() > 0)) {
            this.minecraftServer.getPlayerList().sendMessage(CraftChatMessage.fromString(quitMessage));
        }
        // CraftBukkit end
        if (this.minecraftServer.T() && this.player.getName().equals(this.minecraftServer.S())) {
            Logger.info("Stopping singleplayer server as player logged out");
            this.minecraftServer.safeShutdown();
        }

    }

    public void sendPacket(final Packet packet) {
        // CraftBukkit start
        if (packet == null) {
            return;
        }

        if (packet instanceof PacketPlayOutChat) {
            PacketPlayOutChat packetplayoutchat = (PacketPlayOutChat) packet;
            EntityHuman.EnumChatVisibility entityhuman_enumchatvisibility = this.player.getChatFlags();

            if (entityhuman_enumchatvisibility == EntityHuman.EnumChatVisibility.HIDDEN) {
                return;
            }

            if (entityhuman_enumchatvisibility == EntityHuman.EnumChatVisibility.SYSTEM && !packetplayoutchat.b()) {
                return;
            }
        } else if (packet instanceof PacketPlayOutSpawnPosition) {
            PacketPlayOutSpawnPosition packet6 = (PacketPlayOutSpawnPosition) packet;
            this.player.compassTarget = new Location(this.getPlayer().getWorld(), packet6.position.getX(), packet6.position.getY(), packet6.position.getZ());
        }

        // CraftBukkit end

        this.networkManager.handle(packet);
    }

    public void a(PacketPlayInHeldItemSlot packetplayinhelditemslot) {
        // CraftBukkit start
        if (this.player.dead) return;
        PlayerConnectionUtils.ensureMainThread(packetplayinhelditemslot, this, this.player.u());
        if (packetplayinhelditemslot.a() >= 0 && packetplayinhelditemslot.a() < PlayerInventory.getHotbarSize()) {
            PlayerItemHeldEvent event = new PlayerItemHeldEvent(this.getPlayer(), this.player.inventory.itemInHandIndex, packetplayinhelditemslot.a());
            this.server.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                this.sendPacket(new PacketPlayOutHeldItemSlot(this.player.inventory.itemInHandIndex));
                this.player.resetIdleTimer();
                return;
            }
            // CraftBukkit end
            this.player.inventory.itemInHandIndex = packetplayinhelditemslot.a();
            this.player.resetIdleTimer();
        } else {
            Logger.warn(this.player.getName() + " tried to set an invalid carried item");
            this.disconnect("Invalid hotbar selection (Hacking?)"); // CraftBukkit //Spigot "Nope" -> Descriptive reason
        }
    }

    public void a(PacketPlayInChat packetplayinchat) {
        final String message = packetplayinchat.a();
        if (message.isEmpty()) {
            return;
        }

        if (message.charAt(0) == '/'){
            final String command = message.substring(1);
            if (command.isEmpty()) {
                return;
            }
            CommandStorage.execute(getPlayer(), command);
            return;
        }

        // CraftBukkit end
        if (this.player.dead || this.player.getChatFlags() == EntityHuman.EnumChatVisibility.HIDDEN) { // CraftBukkit - dead men tell no tales
            ChatMessage chatmessage = new ChatMessage("chat.cannotSend", new Object[0]);

            chatmessage.getChatModifier().setColor(EnumChatFormat.RED);
            this.sendPacket(new PacketPlayOutChat(chatmessage));
            return;
        }

        if (this.player.getChatFlags() == EntityHuman.EnumChatVisibility.SYSTEM) { // Re-add "Command Only" flag check
            ChatMessage chatmessage = new ChatMessage("chat.cannotSend", new Object[0]);

            chatmessage.getChatModifier().setColor(EnumChatFormat.RED);
            this.sendPacket(new PacketPlayOutChat(chatmessage));
            return;
        }

        this.chat(message, true);
    }

    public void chat(String s, boolean async) {
        final Player player = this.getPlayer();
        final AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(async, player, s, new LazyPlayerSet());
        this.server.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        s = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
        minecraftServer.console.sendMessage(s);
        final BaseComponent[] message = TextComponent.fromLegacyText(s);

        if (((LazyPlayerSet) event.getRecipients()).isLazy()) {
            final PacketPlayOutChat chatPacket = new PacketPlayOutChat(null);
            chatPacket.components = message;
            for (EntityPlayer recipient : minecraftServer.getPlayerList().players) {
                recipient.playerConnection.sendPacket(chatPacket);
            }
            return;
        }    

        for (Player recipient : event.getRecipients()) {
            recipient.spigot().sendMessage(message);
        }
    }
 
    public void a(PacketPlayInArmAnimation packetplayinarmanimation) {
        if (this.player.dead) return; // CraftBukkit
        PlayerConnectionUtils.ensureMainThread(packetplayinarmanimation, this, this.player.u());
        this.player.resetIdleTimer();
        // CraftBukkit start - Raytrace to look for 'rogue armswings'
        float f1 = this.player.pitch;
        float f2 = this.player.yaw;
        double d0 = this.player.locX;
        double d1 = this.player.locY + (double) this.player.getHeadHeight();
        double d2 = this.player.locZ;
        Vec3D vec3d = new Vec3D(d0, d1, d2);

        float f3 = MathHelper.cos(-f2 * 0.017453292F - 3.1415927F);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - 3.1415927F);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = player.playerInteractManager.getGameMode() == WorldSettings.EnumGamemode.CREATIVE ? 5.0D : 4.5D;
        Vec3D vec3d1 = vec3d.add((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
        MovingObjectPosition movingobjectposition = this.player.world.rayTrace(vec3d, vec3d1, false);

        if (movingobjectposition == null || movingobjectposition.type != MovingObjectPosition.EnumMovingObjectType.BLOCK) {
            CraftEventFactory.callPlayerInteractEvent(this.player, Action.LEFT_CLICK_AIR, this.player.inventory.getItemInHand());
        }

        // Arm swing animation
        PlayerAnimationEvent event = new PlayerAnimationEvent(this.getPlayer());
        this.server.getPluginManager().callEvent(event);

        if (event.isCancelled()) return;
        // CraftBukkit end
        this.player.bw();
    }

    public void a(PacketPlayInEntityAction packetplayinentityaction) {
        PlayerConnectionUtils.ensureMainThread(packetplayinentityaction, this, this.player.u());
        // CraftBukkit start
        if (this.player.dead) return;
        switch (packetplayinentityaction.b()) {
            case START_SNEAKING:
            case STOP_SNEAKING:
                PlayerToggleSneakEvent event = new PlayerToggleSneakEvent(this.getPlayer(), packetplayinentityaction.b() == PacketPlayInEntityAction.EnumPlayerAction.START_SNEAKING);
                this.server.getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return;
                }
                break;
            case START_SPRINTING:
            case STOP_SPRINTING:
                PlayerToggleSprintEvent e2 = new PlayerToggleSprintEvent(this.getPlayer(), packetplayinentityaction.b() == PacketPlayInEntityAction.EnumPlayerAction.START_SPRINTING);
                this.server.getPluginManager().callEvent(e2);

                if (e2.isCancelled()) {
                    return;
                }
                break;
        }
        // CraftBukkit end
        this.player.resetIdleTimer();
        switch (PlayerConnection.SyntheticClass_1.b[packetplayinentityaction.b().ordinal()]) {
        case 1:
            this.player.setSneaking(true);
            break;

        case 2:
            this.player.setSneaking(false);
            break;

        case 3:
            this.player.setSprinting(true);
            break;

        case 4:
            this.player.setSprinting(false);
            break;

        case 5:
            this.player.a(false, true, true);
            // this.checkMovement = false; // CraftBukkit - this is handled in teleport
            break;

        case 6:
            break;

        case 7:
            break;

        default:
            throw new IllegalArgumentException("Invalid client command!");
        }

    }

    public void a(PacketPlayInUseEntity packetplayinuseentity) {
        if (this.player.dead) {
            return;
        } // CraftBukkit

        PlayerConnectionUtils.ensureMainThread(packetplayinuseentity, this, this.player.u());
        WorldServer worldserver = this.minecraftServer.getWorldServer(this.player.dimension);

        final Vec3D vec3d = packetplayinuseentity.b();

        if (vec3d != null) {
            final PreInteractEntityEvent event = new PreInteractEntityEvent(getPlayer(), packetplayinuseentity.getID(), vec3d.a, vec3d.b, vec3d.c, worldserver.getWorld());
            EventsExecutor.execute(event);
        
            if (event.isCancelled()) {
                return;
            }
        }

        Entity entity = packetplayinuseentity.a((World) worldserver);
        if (entity == null) {
            return;
        }
        // Spigot Start
        if (entity == player && !player.isSpectator()) {
            disconnect("Cannot interact with self!");
            return;
        }
        // Spigot End

        this.player.resetIdleTimer();
        boolean flag = this.player.hasLineOfSight(entity);
        double d0 = 36.0D;

        if (!flag) {
            d0 = 9.0D;
        }

        if (this.player.h(entity) >= d0) {
            return;
        }
        ItemStack itemInHand = this.player.inventory.getItemInHand(); // CraftBukkit

        if (packetplayinuseentity.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT
            || packetplayinuseentity.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT
        ) {
            // CraftBukkit start
            boolean triggerLeashUpdate = itemInHand != null && itemInHand.getItem() == Items.LEAD && entity instanceof EntityInsentient;
            Item origItem = this.player.inventory.getItemInHand() == null ? null : this.player.inventory.getItemInHand().getItem();
            PlayerInteractEntityEvent interactEvent;
            if (packetplayinuseentity.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT) {
                interactEvent = new PlayerInteractEntityEvent((Player) this.getPlayer(), entity.getBukkitEntity());
            } else {
                Vec3D target = packetplayinuseentity.b();
                interactEvent = new PlayerInteractAtEntityEvent((Player) this.getPlayer(), entity.getBukkitEntity(), new org.bukkit.util.Vector(target.a, target.b, target.c));
            }
            this.server.getPluginManager().callEvent(interactEvent);

            if (triggerLeashUpdate && (interactEvent.isCancelled() || this.player.inventory.getItemInHand() == null || this.player.inventory.getItemInHand().getItem() != Items.LEAD)) {
                // Refresh the current leash state
                this.sendPacket(new PacketPlayOutAttachEntity(1, entity, ((EntityInsentient) entity).getLeashHolder()));
            }

            if (interactEvent.isCancelled() || this.player.inventory.getItemInHand() == null || this.player.inventory.getItemInHand().getItem() != origItem) {
                // Refresh the current entity metadata
                this.sendPacket(new PacketPlayOutEntityMetadata(entity.getId(), entity.datawatcher, true));
            }

            if (interactEvent.isCancelled()) {
                return;
                }
            // CraftBukkit end
        }

        if (packetplayinuseentity.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT) {
            this.player.u(entity);

            // CraftBukkit start
            if (itemInHand != null && itemInHand.count <= -1) {
                this.player.updateInventory(this.player.activeContainer);
            }
            // CraftBukkit end
        } else if (packetplayinuseentity.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT) {
            entity.a((EntityHuman) this.player, packetplayinuseentity.b());

            // CraftBukkit start
            if (itemInHand != null && itemInHand.count <= -1) {
                this.player.updateInventory(this.player.activeContainer);
            }
            // CraftBukkit end
        } else if (packetplayinuseentity.a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
            if (entity instanceof EntityItem || entity instanceof EntityExperienceOrb || entity instanceof EntityArrow || (entity == this.player && !player.isSpectator())) { // CraftBukkit
                this.disconnect("Attempting to attack an invalid entity");
                this.minecraftServer.warning("Player " + this.player.getName() + " tried to attack an invalid entity");
                return;
            }

            this.player.attack(entity);

            // CraftBukkit start
            if (itemInHand != null && itemInHand.count <= -1) {
                this.player.updateInventory(this.player.activeContainer);
            }
            // CraftBukkit end
        }
    }

    public void a(PacketPlayInClientCommand packetplayinclientcommand) {
        PlayerConnectionUtils.ensureMainThread(packetplayinclientcommand, this, this.player.u());
        this.player.resetIdleTimer();
        PacketPlayInClientCommand.EnumClientCommand packetplayinclientcommand_enumclientcommand = packetplayinclientcommand.a();

        switch (PlayerConnection.SyntheticClass_1.c[packetplayinclientcommand_enumclientcommand.ordinal()]) {
        case 1:
            if (this.player.viewingCredits) {
                // this.player = this.minecraftServer.getPlayerList().moveToWorld(this.player, 0, true);
                this.minecraftServer.getPlayerList().changeDimension(this.player, 0, PlayerTeleportEvent.TeleportCause.END_PORTAL); // CraftBukkit - reroute logic through custom portal management
            } else if (this.player.u().getWorldData().isHardcore()) {
                if (this.minecraftServer.T() && this.player.getName().equals(this.minecraftServer.S())) {
                    this.player.playerConnection.disconnect("You have died. Game over, man, it\'s game over!");
                    this.minecraftServer.aa();
                }
            } else {
                if (this.player.getHealth() > 0.0F) {
                    return;
                }

                this.player = this.minecraftServer.getPlayerList().moveToWorld(this.player, 0, false);
            }
            break;
        }

    }

    public void a(PacketPlayInCloseWindow packetplayinclosewindow) {
        if (this.player.dead) return; // CraftBukkit
        PlayerConnectionUtils.ensureMainThread(packetplayinclosewindow, this, this.player.u());

        CraftEventFactory.handleInventoryCloseEvent(this.player); // CraftBukkit

        this.player.p();
    }

    public void a(PacketPlayInWindowClick packetplayinwindowclick) {
        if (this.player.dead) return; // CraftBukkit
        PlayerConnectionUtils.ensureMainThread(packetplayinwindowclick, this, this.player.u());
        this.player.resetIdleTimer();
        if (this.player.activeContainer.windowId == packetplayinwindowclick.a() && this.player.activeContainer.c(this.player)) {
            boolean cancelled = this.player.isSpectator(); // CraftBukkit - see below if
            if (packetplayinwindowclick.b() >= 54) {
                return;
            }
            if (false) { // this.player.isSpectator()) {
                ArrayList arraylist = Lists.newArrayList();

                for (int i = 0; i < this.player.activeContainer.c.size(); ++i) {
                    arraylist.add(((Slot) this.player.activeContainer.c.get(i)).getItem());
                }

                this.player.a(this.player.activeContainer, (List) arraylist);
            } else {
                // ItemStack itemstack = this.player.activeContainer.clickItem(packetplayinwindowclick.b(), packetplayinwindowclick.c(), packetplayinwindowclick.f(), this.player);
                // CraftBukkit start - Call InventoryClickEvent
                if (packetplayinwindowclick.b() < -1 && packetplayinwindowclick.b() != -999) {
                    return;
                }

                InventoryView inventory = this.player.activeContainer.getBukkitView();
                SlotType type = CraftInventoryView.getSlotType(inventory, packetplayinwindowclick.b());

                InventoryClickEvent event = null;
                ClickType click = ClickType.UNKNOWN;
                InventoryAction action = InventoryAction.UNKNOWN;

                ItemStack itemstack = null;

                if (packetplayinwindowclick.b() == -1) {
                    type = SlotType.OUTSIDE; // override
                    click = packetplayinwindowclick.c() == 0 ? ClickType.WINDOW_BORDER_LEFT : ClickType.WINDOW_BORDER_RIGHT;
                    action = InventoryAction.NOTHING;
                } else if (packetplayinwindowclick.f() == 0) {
                    if (packetplayinwindowclick.c() == 0) {
                        click = ClickType.LEFT;
                    } else if (packetplayinwindowclick.c() == 1) {
                        click = ClickType.RIGHT;
                    }
                    if (packetplayinwindowclick.c() == 0 || packetplayinwindowclick.c() == 1) {
                        action = InventoryAction.NOTHING; // Don't want to repeat ourselves
                        if (packetplayinwindowclick.b() == -999) {
                            if (player.inventory.getCarried() != null) {
                                action = packetplayinwindowclick.c() == 0 ? InventoryAction.DROP_ALL_CURSOR : InventoryAction.DROP_ONE_CURSOR;
                            }
                        } else {
                            Slot slot = this.player.activeContainer.getSlot(packetplayinwindowclick.b());
                            if (slot != null) {
                                ItemStack clickedItem = slot.getItem();
                                ItemStack cursor = player.inventory.getCarried();
                                if (clickedItem == null) {
                                    if (cursor != null) {
                                        action = packetplayinwindowclick.c() == 0 ? InventoryAction.PLACE_ALL : InventoryAction.PLACE_ONE;
                                    }
                                } else if (slot.isAllowed(player)) {
                                    if (cursor == null) {
                                        action = packetplayinwindowclick.c() == 0 ? InventoryAction.PICKUP_ALL : InventoryAction.PICKUP_HALF;
                                    } else if (slot.isAllowed(cursor)) {
                                        if (clickedItem.doMaterialsMatch(cursor) && ItemStack.equals(clickedItem, cursor)) {
                                            int toPlace = packetplayinwindowclick.c() == 0 ? cursor.count : 1;
                                            toPlace = Math.min(toPlace, clickedItem.getMaxStackSize() - clickedItem.count);
                                            toPlace = Math.min(toPlace, slot.inventory.getMaxStackSize() - clickedItem.count);
                                            if (toPlace == 1) {
                                                action = InventoryAction.PLACE_ONE;
                                            } else if (toPlace == cursor.count) {
                                                action = InventoryAction.PLACE_ALL;
                                            } else if (toPlace < 0) {
                                                action = toPlace != -1 ? InventoryAction.PICKUP_SOME : InventoryAction.PICKUP_ONE; // this happens with oversized stacks
                                            } else if (toPlace != 0) {
                                                action = InventoryAction.PLACE_SOME;
                                            }
                                        } else if (cursor.count <= slot.getMaxStackSize()) {
                                            action = InventoryAction.SWAP_WITH_CURSOR;
                                        }
                                    } else if (cursor.getItem() == clickedItem.getItem() && (!cursor.usesData() || cursor.getData() == clickedItem.getData()) && ItemStack.equals(cursor, clickedItem)) {
                                        if (clickedItem.count >= 0) {
                                            if (clickedItem.count + cursor.count <= cursor.getMaxStackSize()) {
                                                // As of 1.5, this is result slots only
                                                action = InventoryAction.PICKUP_ALL;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (packetplayinwindowclick.f() == 1) {
                    if (packetplayinwindowclick.c() == 0) {
                        click = ClickType.SHIFT_LEFT;
                    } else if (packetplayinwindowclick.c() == 1) {
                        click = ClickType.SHIFT_RIGHT;
                    }
                    if (packetplayinwindowclick.c() == 0 || packetplayinwindowclick.c() == 1) {
                        if (packetplayinwindowclick.b() < 0) {
                            action = InventoryAction.NOTHING;
                        } else {
                            Slot slot = this.player.activeContainer.getSlot(packetplayinwindowclick.b());
                            if (slot != null && slot.isAllowed(this.player) && slot.hasItem()) {
                                action = InventoryAction.MOVE_TO_OTHER_INVENTORY;
                            } else {
                                action = InventoryAction.NOTHING;
                            }
                        }
                    }
                } else if (packetplayinwindowclick.f() == 2) {
                    if (packetplayinwindowclick.c() >= 0 && packetplayinwindowclick.c() < 9) {
                        click = ClickType.NUMBER_KEY;
                        Slot clickedSlot = this.player.activeContainer.getSlot(packetplayinwindowclick.b());
                        if (clickedSlot.isAllowed(player)) {
                            ItemStack hotbar = this.player.inventory.getItem(packetplayinwindowclick.c());
                            boolean canCleanSwap = hotbar == null || (clickedSlot.inventory == player.inventory && clickedSlot.isAllowed(hotbar)); // the slot will accept the hotbar item
                            if (clickedSlot.hasItem()) {
                                if (canCleanSwap) {
                                    action = InventoryAction.HOTBAR_SWAP;
                                } else {
                                    int firstEmptySlot = player.inventory.getFirstEmptySlotIndex();
                                    if (firstEmptySlot > -1) {
                                        action = InventoryAction.HOTBAR_MOVE_AND_READD;
                                    } else {
                                        action = InventoryAction.NOTHING; // This is not sane! Mojang: You should test for other slots of same type
                                    }
                                }
                            } else if (!clickedSlot.hasItem() && hotbar != null && clickedSlot.isAllowed(hotbar)) {
                                action = InventoryAction.HOTBAR_SWAP;
                            } else {
                                action = InventoryAction.NOTHING;
                            }
                        } else {
                            action = InventoryAction.NOTHING;
                        }
                        // Special constructor for number key
                        event = new InventoryClickEvent(inventory, type, packetplayinwindowclick.b(), click, action, packetplayinwindowclick.c());
                    }
                } else if (packetplayinwindowclick.f() == 3) {
                    if (packetplayinwindowclick.c() == 2) {
                        click = ClickType.MIDDLE;
                        if (packetplayinwindowclick.b() == -999) {
                            action = InventoryAction.NOTHING;
                        } else {
                            Slot slot = this.player.activeContainer.getSlot(packetplayinwindowclick.b());
                            if (slot != null && slot.hasItem() && player.abilities.canInstantlyBuild && player.inventory.getCarried() == null) {
                                action = InventoryAction.CLONE_STACK;
                            } else {
                                action = InventoryAction.NOTHING;
                            }
                        }
                    } else {
                        click = ClickType.UNKNOWN;
                        action = InventoryAction.UNKNOWN;
                    }
                } else if (packetplayinwindowclick.f() == 4) {
                    if (packetplayinwindowclick.b() >= 0) {
                        if (packetplayinwindowclick.c() == 0) {
                            click = ClickType.DROP;
                            Slot slot = this.player.activeContainer.getSlot(packetplayinwindowclick.b());
                            if (slot != null && slot.hasItem() && slot.isAllowed(player) && slot.getItem() != null && slot.getItem().getItem() != Item.getItemOf(Blocks.AIR)) {
                                action = InventoryAction.DROP_ONE_SLOT;
                            } else {
                                action = InventoryAction.NOTHING;
                            }
                        } else if (packetplayinwindowclick.c() == 1) {
                            click = ClickType.CONTROL_DROP;
                            Slot slot = this.player.activeContainer.getSlot(packetplayinwindowclick.b());
                            if (slot != null && slot.hasItem() && slot.isAllowed(player) && slot.getItem() != null && slot.getItem().getItem() != Item.getItemOf(Blocks.AIR)) {
                                action = InventoryAction.DROP_ALL_SLOT;
                            } else {
                                action = InventoryAction.NOTHING;
                            }
                        }
                    } else {
                        // Sane default (because this happens when they are holding nothing. Don't ask why.)
                        click = ClickType.LEFT;
                        if (packetplayinwindowclick.c() == 1) {
                            click = ClickType.RIGHT;
                        }
                        action = InventoryAction.NOTHING;
                    }
                } else if (packetplayinwindowclick.f() == 5) {
                    itemstack = this.player.activeContainer.clickItem(packetplayinwindowclick.b(), packetplayinwindowclick.c(), 5, this.player);
                } else if (packetplayinwindowclick.f() == 6) {
                    click = ClickType.DOUBLE_CLICK;
                    action = InventoryAction.NOTHING;
                    if (packetplayinwindowclick.b() >= 0 && this.player.inventory.getCarried() != null) {
                        ItemStack cursor = this.player.inventory.getCarried();
                        action = InventoryAction.NOTHING;
                        // Quick check for if we have any of the item
                        if (inventory.getTopInventory().contains(org.bukkit.Material.getMaterial(Item.getId(cursor.getItem()))) || inventory.getBottomInventory().contains(org.bukkit.Material.getMaterial(Item.getId(cursor.getItem())))) {
                            action = InventoryAction.COLLECT_TO_CURSOR;
                        }
                    }
                }
                // TODO check on updates

                if (packetplayinwindowclick.f() != 5) {
                    if (click == ClickType.NUMBER_KEY) {
                        event = new InventoryClickEvent(inventory, type, packetplayinwindowclick.b(), click, action, packetplayinwindowclick.c());
                    } else {
                        event = new InventoryClickEvent(inventory, type, packetplayinwindowclick.b(), click, action);
                    }

                    org.bukkit.inventory.Inventory top = inventory.getTopInventory();
                    if (packetplayinwindowclick.b() == 0 && top instanceof CraftingInventory) {
                        org.bukkit.inventory.Recipe recipe = ((CraftingInventory) top).getRecipe();
                        if (recipe != null) {
                            if (click == ClickType.NUMBER_KEY) {
                                event = new CraftItemEvent(recipe, inventory, type, packetplayinwindowclick.b(), click, action, packetplayinwindowclick.c());
                            } else {
                                event = new CraftItemEvent(recipe, inventory, type, packetplayinwindowclick.b(), click, action);
                            }
                        }
                    }

                    event.setCancelled(cancelled);
                    server.getPluginManager().callEvent(event);

                    switch (event.getResult()) {
                        case ALLOW:
                        case DEFAULT:
                            itemstack = this.player.activeContainer.clickItem(packetplayinwindowclick.b(), packetplayinwindowclick.c(), packetplayinwindowclick.f(), this.player);
                            break;
                        case DENY:
                            /* Needs enum constructor in InventoryAction
                            if (action.modifiesOtherSlots()) {

                            } else {
                                if (action.modifiesCursor()) {
                                    this.player.playerConnection.sendPacket(new Packet103SetSlot(-1, -1, this.player.inventory.getCarried()));
                                }
                                if (action.modifiesClicked()) {
                                    this.player.playerConnection.sendPacket(new Packet103SetSlot(this.player.activeContainer.windowId, packet102windowclick.slot, this.player.activeContainer.getSlot(packet102windowclick.slot).getItem()));
                                }
                            }*/
                            switch (action) {
                                // Modified other slots
                                case PICKUP_ALL:
                                case MOVE_TO_OTHER_INVENTORY:
                                case HOTBAR_MOVE_AND_READD:
                                case HOTBAR_SWAP:
                                case COLLECT_TO_CURSOR:
                                case UNKNOWN:
                                    this.player.updateInventory(this.player.activeContainer);
                                    break;
                                // Modified cursor and clicked
                                case PICKUP_SOME:
                                case PICKUP_HALF:
                                case PICKUP_ONE:
                                case PLACE_ALL:
                                case PLACE_SOME:
                                case PLACE_ONE:
                                case SWAP_WITH_CURSOR:
                                    this.player.playerConnection.sendPacket(new PacketPlayOutSetSlot(-1, -1, this.player.inventory.getCarried()));
                                    this.player.playerConnection.sendPacket(new PacketPlayOutSetSlot(this.player.activeContainer.windowId, packetplayinwindowclick.b(), this.player.activeContainer.getSlot(packetplayinwindowclick.b()).getItem()));
                                    break;
                                // Modified clicked only
                                case DROP_ALL_SLOT:
                                case DROP_ONE_SLOT:
                                    this.player.playerConnection.sendPacket(new PacketPlayOutSetSlot(this.player.activeContainer.windowId, packetplayinwindowclick.b(), this.player.activeContainer.getSlot(packetplayinwindowclick.b()).getItem()));
                                    break;
                                // Modified cursor only
                                case DROP_ALL_CURSOR:
                                case DROP_ONE_CURSOR:
                                case CLONE_STACK:
                                    this.player.playerConnection.sendPacket(new PacketPlayOutSetSlot(-1, -1, this.player.inventory.getCarried()));
                                    break;
                                // Nothing
                                case NOTHING:
                                    break;
                            }
                            return;
                    }

                    if (event instanceof CraftItemEvent) {
                        // Need to update the inventory on crafting to
                        // correctly support custom recipes
                        player.updateInventory(player.activeContainer);
                    }
                }
                // CraftBukkit end

                if (ItemStack.matches(packetplayinwindowclick.e(), itemstack)) {
                    this.player.playerConnection.sendPacket(new PacketPlayOutTransaction(packetplayinwindowclick.a(), packetplayinwindowclick.d(), true));
                    this.player.g = true;
                    this.player.activeContainer.b();
                    this.player.broadcastCarriedItem();
                    this.player.g = false;
                } else {
                    this.n.put(this.player.activeContainer.windowId, Short.valueOf(packetplayinwindowclick.d()));
                    this.player.playerConnection.sendPacket(new PacketPlayOutTransaction(packetplayinwindowclick.a(), packetplayinwindowclick.d(), false));
                    this.player.activeContainer.a(this.player, false);
                    ArrayList arraylist1 = Lists.newArrayList();

                    for (int j = 0; j < this.player.activeContainer.c.size(); ++j) {
                        arraylist1.add(((Slot) this.player.activeContainer.c.get(j)).getItem());
                    }

                    this.player.a(this.player.activeContainer, (List) arraylist1);
                }
            }
        }

    }

    public void a(PacketPlayInEnchantItem packetplayinenchantitem) {
        PlayerConnectionUtils.ensureMainThread(packetplayinenchantitem, this, this.player.u());
        this.player.resetIdleTimer();
        if (this.player.activeContainer.windowId == packetplayinenchantitem.a() && this.player.activeContainer.c(this.player) && !this.player.isSpectator()) {
            this.player.activeContainer.a(this.player, packetplayinenchantitem.b());
            this.player.activeContainer.b();
        }

    }

    public void a(PacketPlayInSetCreativeSlot packetplayinsetcreativeslot) {
        PlayerConnectionUtils.ensureMainThread(packetplayinsetcreativeslot, this, this.player.u());
        if (this.player.playerInteractManager.isCreative()) {
            boolean flag = packetplayinsetcreativeslot.a() < 0;
            ItemStack itemstack = packetplayinsetcreativeslot.getItemStack();

            if (itemstack != null && itemstack.hasTag() && itemstack.getTag().hasKeyOfType("BlockEntityTag", 10)) {
                NBTTagCompound nbttagcompound = itemstack.getTag().getCompound("BlockEntityTag");

                if (nbttagcompound.hasKey("x") && nbttagcompound.hasKey("y") && nbttagcompound.hasKey("z")) {
                    BlockPosition blockposition = new BlockPosition(nbttagcompound.getInt("x"), nbttagcompound.getInt("y"), nbttagcompound.getInt("z"));
                    TileEntity tileentity = this.player.world.getTileEntity(blockposition);

                    if (tileentity != null) {
                        NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                        tileentity.b(nbttagcompound1);
                        nbttagcompound1.remove("x");
                        nbttagcompound1.remove("y");
                        nbttagcompound1.remove("z");
                        itemstack.a("BlockEntityTag", (NBTBase) nbttagcompound1);
                    }
                }
            }

            boolean flag1 = packetplayinsetcreativeslot.a() >= 1 && packetplayinsetcreativeslot.a() < 36 + PlayerInventory.getHotbarSize();
            // CraftBukkit - Add invalidItems check
            boolean flag2 = itemstack == null || itemstack.getItem() != null && (!invalidItems.contains(Item.getId(itemstack.getItem())) || !org.spigotmc.SpigotConfig.filterCreativeItems); // Spigot
            boolean flag3 = itemstack == null || itemstack.getData() >= 0 && itemstack.count <= 64 && itemstack.count > 0;
            // CraftBukkit start - Call click event
            if (flag || (flag1 && !ItemStack.matches(this.player.defaultContainer.getSlot(packetplayinsetcreativeslot.a()).getItem(), packetplayinsetcreativeslot.getItemStack()))) { // Insist on valid slot

                org.bukkit.entity.HumanEntity player = this.player.getBukkitEntity();
                InventoryView inventory = new CraftInventoryView(player, player.getInventory(), this.player.defaultContainer);
                org.bukkit.inventory.ItemStack item = CraftItemStack.asBukkitCopy(packetplayinsetcreativeslot.getItemStack());

                SlotType type = SlotType.QUICKBAR;
                if (flag) {
                    type = SlotType.OUTSIDE;
                } else if (packetplayinsetcreativeslot.a() < 36) {
                    if (packetplayinsetcreativeslot.a() >= 5 && packetplayinsetcreativeslot.a() < 9) {
                        type = SlotType.ARMOR;
                    } else {
                        type = SlotType.CONTAINER;
                    }
                }
                InventoryCreativeEvent event = new InventoryCreativeEvent(inventory, type, flag ? -999 : packetplayinsetcreativeslot.a(), item);
                server.getPluginManager().callEvent(event);

                itemstack = CraftItemStack.asNMSCopy(event.getCursor());

                switch (event.getResult()) {
                case ALLOW:
                    // Plugin cleared the id / stacksize checks
                    flag2 = flag3 = true;
                    break;
                case DEFAULT:
                    break;
                case DENY:
                    // Reset the slot
                    if (packetplayinsetcreativeslot.a() >= 0) {
                        this.player.playerConnection.sendPacket(new PacketPlayOutSetSlot(this.player.defaultContainer.windowId, packetplayinsetcreativeslot.a(), this.player.defaultContainer.getSlot(packetplayinsetcreativeslot.a()).getItem()));
                        this.player.playerConnection.sendPacket(new PacketPlayOutSetSlot(-1, -1, null));
                    }
                    return;
                }
            }
            // CraftBukkit end

            if (flag1 && flag2 && flag3) {
                if (itemstack == null) {
                    this.player.defaultContainer.setItem(packetplayinsetcreativeslot.a(), (ItemStack) null);
                } else {
                    this.player.defaultContainer.setItem(packetplayinsetcreativeslot.a(), itemstack);
                }

                this.player.defaultContainer.a(this.player, true);
            } else if (flag && flag2 && flag3 && this.m < 200) {
                this.m += 20;
                EntityItem entityitem = this.player.drop(itemstack, true);

                if (entityitem != null) {
                    entityitem.j();
                }
            }
        }

    }

    public void a(PacketPlayInTransaction packetplayintransaction) {
        if (this.player.dead) return; // CraftBukkit
        PlayerConnectionUtils.ensureMainThread(packetplayintransaction, this, this.player.u());
        Short oshort = (Short) this.n.get(this.player.activeContainer.windowId);

        if (oshort != null && packetplayintransaction.b() == oshort.shortValue() && this.player.activeContainer.windowId == packetplayintransaction.a() && !this.player.activeContainer.c(this.player) && !this.player.isSpectator()) {
            this.player.activeContainer.a(this.player, true);
        }

    }

    public void a(PacketPlayInUpdateSign packetplayinupdatesign) {
        if (this.player.dead) return; // CraftBukkit
        PlayerConnectionUtils.ensureMainThread(packetplayinupdatesign, this, this.player.u());
        this.player.resetIdleTimer();
        WorldServer worldserver = this.minecraftServer.getWorldServer(this.player.dimension);
        BlockPosition blockposition = packetplayinupdatesign.a();

        if (worldserver.isLoaded(blockposition)) {
            TileEntity tileentity = worldserver.getTileEntity(blockposition);

            if (!(tileentity instanceof TileEntitySign)) {
                return;
            }

            TileEntitySign tileentitysign = (TileEntitySign) tileentity;

            if (!tileentitysign.b() || tileentitysign.c() != this.player) {
                this.minecraftServer.warning("Player " + this.player.getName() + " just tried to change non-editable sign");
                this.sendPacket(new PacketPlayOutUpdateSign(tileentity.world, packetplayinupdatesign.a(), tileentitysign.lines)); // CraftBukkit
                return;
            }

            IChatBaseComponent[] aichatbasecomponent = packetplayinupdatesign.b();

            // CraftBukkit start
            Player player = this.server.getPlayer(this.player);
            int x = packetplayinupdatesign.a().getX();
            int y = packetplayinupdatesign.a().getY();
            int z = packetplayinupdatesign.a().getZ();
            String[] lines = new String[4];
            for (int i = 0; i < aichatbasecomponent.length; ++i) {
                lines[i] = aichatbasecomponent[i].c();
            }
            SignChangeEvent event = new SignChangeEvent((org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock) player.getWorld().getBlockAt(x, y, z), this.server.getPlayer(this.player), lines);
            this.server.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                System.arraycopy(org.bukkit.craftbukkit.v1_8_R3.block.CraftSign.sanitizeLines(event.getLines()), 0, tileentitysign.lines, 0, 4);
                tileentitysign.isEditable = false;
            }
            // System.arraycopy(packetplayinupdatesign.b(), 0, tileentitysign.lines, 0, 4);
            // CraftBukkit end

            tileentitysign.update();
            worldserver.notify(blockposition);
        }

    }

    public void a(PacketPlayInKeepAlive packetplayinkeepalive) {
        if (packetplayinkeepalive.a() == this.i) {
            int i = (int) (this.d() - this.j);

            this.player.ping = (this.player.ping * 3 + i) / 4;
        }

    }

    private long d() {
        return System.nanoTime() / 1000000L;
    }

    public void a(PacketPlayInAbilities packetplayinabilities) {
        PlayerConnectionUtils.ensureMainThread(packetplayinabilities, this, this.player.u());
        // CraftBukkit start
        if (this.player.abilities.canFly && this.player.abilities.isFlying != packetplayinabilities.isFlying()) {
            PlayerToggleFlightEvent event = new PlayerToggleFlightEvent(this.server.getPlayer(this.player), packetplayinabilities.isFlying());
            this.server.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                this.player.abilities.isFlying = packetplayinabilities.isFlying(); // Actually set the player's flying status
            } else {
                this.player.updateAbilities(); // Tell the player their ability was reverted
            }
        }
        // CraftBukkit end
    }

    public void a(PacketPlayInTabComplete packetplayintabcomplete) {
        PlayerConnectionUtils.ensureMainThread(packetplayintabcomplete, this, this.player.u());
        if (packetplayintabcomplete.a().charAt(0) != '/') {
            return;
        }

        final String command = packetplayintabcomplete.a().substring(1);
        if (command.isEmpty()) {
            return;
        }

        final String[] tab = CommandStorage.tab(getPlayer(), packetplayintabcomplete.a().substring(1));
        if (tab != null) {
            this.player.playerConnection.sendPacket(new PacketPlayOutTabComplete(tab));
        }
    }

    public void a(PacketPlayInSettings packetplayinsettings) {
        PlayerConnectionUtils.ensureMainThread(packetplayinsettings, this, this.player.u());
        this.player.a(packetplayinsettings);
    }

    public void a(PacketPlayInCustomPayload packetplayincustompayload) {
        PlayerConnectionUtils.ensureMainThread(packetplayincustompayload, this, this.player.u());
        PacketDataSerializer packetdataserializer;
        ItemStack itemstack;
        ItemStack itemstack1;

        if ("MC|BEdit".equals(packetplayincustompayload.a())) {
            packetdataserializer = new PacketDataSerializer(Unpooled.wrappedBuffer(packetplayincustompayload.b()));

            try {
                itemstack = packetdataserializer.i();
                if (itemstack == null) {
                    return;
                }

                if (!ItemBookAndQuill.b(itemstack.getTag())) {
                    throw new IOException("Invalid book tag!");
                }

                itemstack1 = this.player.inventory.getItemInHand();
                if (itemstack1 != null) {
                    if (itemstack.getItem() == Items.WRITABLE_BOOK && itemstack.getItem() == itemstack1.getItem()) {
                        itemstack1 = new ItemStack(Items.WRITABLE_BOOK); // CraftBukkit
                        itemstack1.a("pages", (NBTBase) itemstack.getTag().getList("pages", 8));
                        CraftEventFactory.handleEditBookEvent(player, itemstack1); // CraftBukkit
                    }

                    return;
                }
            } catch (Exception exception) {
                Logger.error("Couldn\'t handle book info", exception);
                this.disconnect("Invalid book data!"); // CraftBukkit
                return;
            } finally {
                packetdataserializer.release();
            }

            return;
        } else if ("MC|BSign".equals(packetplayincustompayload.a())) {
            packetdataserializer = new PacketDataSerializer(Unpooled.wrappedBuffer(packetplayincustompayload.b()));

            try {
                itemstack = packetdataserializer.i();
                if (itemstack == null) {
                    return;
                }

                if (!ItemWrittenBook.b(itemstack.getTag())) {
                    throw new IOException("Invalid book tag!");
                }

                itemstack1 = this.player.inventory.getItemInHand();
                if (itemstack1 != null) {
                    if (itemstack.getItem() == Items.WRITTEN_BOOK && itemstack1.getItem() == Items.WRITABLE_BOOK) {
                        // CraftBukkit start
                        itemstack1 = new ItemStack(Items.WRITTEN_BOOK);
                        itemstack1.a("author", (NBTBase) (new NBTTagString(this.player.getName())));
                        itemstack1.a("title", (NBTBase) (new NBTTagString(itemstack.getTag().getString("title"))));
                        itemstack1.a("pages", (NBTBase) itemstack.getTag().getList("pages", 8));
                        itemstack1.setItem(Items.WRITTEN_BOOK);
                        CraftEventFactory.handleEditBookEvent(player, itemstack1);
                        // CraftBukkit end
                    }

                    return;
                }
            } catch (Exception exception1) {
                Logger.error("Couldn\'t sign book", exception1);
                this.disconnect("Invalid book data!"); // CraftBukkit
                return;
            } finally {
                packetdataserializer.release();
            }

            return;
        } else if ("MC|TrSel".equals(packetplayincustompayload.a())) {
            try {
                int i = packetplayincustompayload.b().readInt();
                Container container = this.player.activeContainer;

                if (container instanceof ContainerMerchant) {
                    ((ContainerMerchant) container).d(i);
                }
            } catch (Exception exception2) {
                Logger.error("Couldn\'t select trade", exception2);
                this.disconnect("Invalid trade data!"); // CraftBukkit
            }
        } else if ("MC|AdvCdm".equals(packetplayincustompayload.a())) {
            this.player.sendMessage(new ChatMessage("advMode.notEnabled", new Object[0]));
        } else if ("MC|Beacon".equals(packetplayincustompayload.a())) {
            if (this.player.activeContainer instanceof ContainerBeacon) {
                try {
                    packetdataserializer = packetplayincustompayload.b();
                    int j = packetdataserializer.readInt();
                    int k = packetdataserializer.readInt();
                    ContainerBeacon containerbeacon = (ContainerBeacon) this.player.activeContainer;
                    Slot slot = containerbeacon.getSlot(0);

                    if (slot.hasItem()) {
                        slot.a(1);
                        IInventory iinventory = containerbeacon.e();

                        iinventory.b(1, j);
                        iinventory.b(2, k);
                        iinventory.update();
                    }
                } catch (Exception exception4) {
                    Logger.error("Couldn\'t set beacon", exception4);
                    this.disconnect("Invalid beacon data!"); // CraftBukkit
                }
            }
        } else if ("MC|ItemName".equals(packetplayincustompayload.a()) && this.player.activeContainer instanceof ContainerAnvil) {
            ContainerAnvil containeranvil = (ContainerAnvil) this.player.activeContainer;

            if (packetplayincustompayload.b() != null && packetplayincustompayload.b().readableBytes() >= 1) {
                String s1 = SharedConstants.a(packetplayincustompayload.b().c(32767));

                if (s1.length() <= 30) {
                    containeranvil.a(s1);
                }
            } else {
                containeranvil.a("");
            }
        }
        // CraftBukkit start
        else if (packetplayincustompayload.a().equals("REGISTER")) {
            String channels = packetplayincustompayload.b().toString(com.google.common.base.Charsets.UTF_8);
            for (String channel : channels.split("\0")) {
                getPlayer().addChannel(channel);
            }
        } else if (packetplayincustompayload.a().equals("UNREGISTER")) {
            String channels = packetplayincustompayload.b().toString(com.google.common.base.Charsets.UTF_8);
            for (String channel : channels.split("\0")) {
                getPlayer().removeChannel(channel);
            }
        } else {
            byte[] data = new byte[packetplayincustompayload.b().readableBytes()];
            packetplayincustompayload.b().readBytes(data);
            server.getMessenger().dispatchIncomingMessage(player.getBukkitEntity(), packetplayincustompayload.a(), data);
        }
        // CraftBukkit end
    }

    // CraftBukkit start - Add "isDisconnected" method
    public boolean isDisconnected() { // Spigot
        return !this.player.joining && !this.networkManager.channel.config().isAutoRead();
    }

    static class SyntheticClass_1 {

        static final int[] a;
        static final int[] b;
        static final int[] c = new int[PacketPlayInClientCommand.EnumClientCommand.values().length];

        static {
            try {
                PlayerConnection.SyntheticClass_1.c[PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN.ordinal()] = 1;
            } catch (NoSuchFieldError nosuchfielderror) {
                ;
            }

            try {
                PlayerConnection.SyntheticClass_1.c[PacketPlayInClientCommand.EnumClientCommand.REQUEST_STATS.ordinal()] = 2;
            } catch (NoSuchFieldError nosuchfielderror1) {
                ;
            }

            try {
                PlayerConnection.SyntheticClass_1.c[PacketPlayInClientCommand.EnumClientCommand.OPEN_INVENTORY_ACHIEVEMENT.ordinal()] = 3;
            } catch (NoSuchFieldError nosuchfielderror2) {
                ;
            }

            b = new int[PacketPlayInEntityAction.EnumPlayerAction.values().length];

            try {
                PlayerConnection.SyntheticClass_1.b[PacketPlayInEntityAction.EnumPlayerAction.START_SNEAKING.ordinal()] = 1;
            } catch (NoSuchFieldError nosuchfielderror3) {
                ;
            }

            try {
                PlayerConnection.SyntheticClass_1.b[PacketPlayInEntityAction.EnumPlayerAction.STOP_SNEAKING.ordinal()] = 2;
            } catch (NoSuchFieldError nosuchfielderror4) {
                ;
            }

            try {
                PlayerConnection.SyntheticClass_1.b[PacketPlayInEntityAction.EnumPlayerAction.START_SPRINTING.ordinal()] = 3;
            } catch (NoSuchFieldError nosuchfielderror5) {
                ;
            }

            try {
                PlayerConnection.SyntheticClass_1.b[PacketPlayInEntityAction.EnumPlayerAction.STOP_SPRINTING.ordinal()] = 4;
            } catch (NoSuchFieldError nosuchfielderror6) {
                ;
            }

            try {
                PlayerConnection.SyntheticClass_1.b[PacketPlayInEntityAction.EnumPlayerAction.STOP_SLEEPING.ordinal()] = 5;
            } catch (NoSuchFieldError nosuchfielderror7) {
                ;
            }

            try {
                PlayerConnection.SyntheticClass_1.b[PacketPlayInEntityAction.EnumPlayerAction.RIDING_JUMP.ordinal()] = 6;
            } catch (NoSuchFieldError nosuchfielderror8) {
                ;
            }

            try {
                PlayerConnection.SyntheticClass_1.b[PacketPlayInEntityAction.EnumPlayerAction.OPEN_INVENTORY.ordinal()] = 7;
            } catch (NoSuchFieldError nosuchfielderror9) {
                ;
            }

            a = new int[PacketPlayInBlockDig.EnumPlayerDigType.values().length];

            try {
                PlayerConnection.SyntheticClass_1.a[PacketPlayInBlockDig.EnumPlayerDigType.DROP_ITEM.ordinal()] = 1;
            } catch (NoSuchFieldError nosuchfielderror10) {
                ;
            }

            try {
                PlayerConnection.SyntheticClass_1.a[PacketPlayInBlockDig.EnumPlayerDigType.DROP_ALL_ITEMS.ordinal()] = 2;
            } catch (NoSuchFieldError nosuchfielderror11) {
                ;
            }

            try {
                PlayerConnection.SyntheticClass_1.a[PacketPlayInBlockDig.EnumPlayerDigType.RELEASE_USE_ITEM.ordinal()] = 3;
            } catch (NoSuchFieldError nosuchfielderror12) {
                ;
            }

            try {
                PlayerConnection.SyntheticClass_1.a[PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK.ordinal()] = 4;
            } catch (NoSuchFieldError nosuchfielderror13) {
                ;
            }

            try {
                PlayerConnection.SyntheticClass_1.a[PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK.ordinal()] = 5;
            } catch (NoSuchFieldError nosuchfielderror14) {
                ;
            }

            try {
                PlayerConnection.SyntheticClass_1.a[PacketPlayInBlockDig.EnumPlayerDigType.STOP_DESTROY_BLOCK.ordinal()] = 6;
            } catch (NoSuchFieldError nosuchfielderror15) {
                ;
            }

        }
    }
}
