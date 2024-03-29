package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;

import org.tinylog.Logger;

public class EntityTracker {

    private final WorldServer world;
    private Set<EntityTrackerEntry> c = Sets.newHashSet();
    public IntHashMap<EntityTrackerEntry> trackedEntities = new IntHashMap(); // CraftBukkit - public
    private int e;

    public EntityTracker(WorldServer worldserver) {
        this.world = worldserver;
        this.e = worldserver.getMinecraftServer().getPlayerList().d();
    }

    public void track(Entity entity) {
        if (entity instanceof EntityPlayer) {
            this.addEntity(entity, 512, 2);
            EntityPlayer entityplayer = (EntityPlayer) entity;
            Iterator iterator = this.c.iterator();

            while (iterator.hasNext()) {
                EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();

                if (entitytrackerentry.tracker != entityplayer) {
                    entitytrackerentry.updatePlayer(entityplayer);
                }
            }
        } else if (entity instanceof EntityFishingHook) {
            this.addEntity(entity, 32, 5, true);
        } else if (entity instanceof EntityArrow) {
            this.addEntity(entity, 32, 20, false);
        } else if (entity instanceof EntitySmallFireball) {
            this.addEntity(entity, 32, 10, false);
        } else if (entity instanceof EntityFireball) {
            this.addEntity(entity, 32, 10, false);
        } else if (entity instanceof EntitySnowball) {
            this.addEntity(entity, 32, 10, true);
        } else if (entity instanceof EntityEnderPearl) {
            this.addEntity(entity, 32, 10, true);
        } else if (entity instanceof EntityEnderSignal) {
            this.addEntity(entity, 32, 4, true);
        } else if (entity instanceof EntityEgg) {
            this.addEntity(entity, 32, 10, true);
        } else if (entity instanceof EntityPotion) {
            this.addEntity(entity, 32, 10, true);
        } else if (entity instanceof EntityThrownExpBottle) {
            this.addEntity(entity, 32, 10, true);
        } else if (entity instanceof EntityFireworks) {
            this.addEntity(entity, 32, 10, true);
        } else if (entity instanceof EntityItem) {
            this.addEntity(entity, 32, 20, true);
        } else if (entity instanceof EntityMinecartAbstract) {
            this.addEntity(entity, 32, 3, true);
        } else if (entity instanceof EntityBoat) {
            this.addEntity(entity, 48, 3, true);
        } else if (entity instanceof EntityTNTPrimed) {
            this.addEntity(entity, 48, 10, true);
        } else if (entity instanceof EntityFallingBlock) {
            this.addEntity(entity, 48, 20, true);
        } else if (entity instanceof EntityHanging) {
            this.addEntity(entity, 48, Integer.MAX_VALUE, false);
        } else if (entity instanceof EntityArmorStand) {
            this.addEntity(entity, 48, 3, true);
        } else if (entity instanceof EntityExperienceOrb) {
            this.addEntity(entity, 32, 20, true);
        } 

    }

    public void addEntity(Entity entity, int i, int j) {
        this.addEntity(entity, i, j, false);
    }

    public void addEntity(Entity entity, int i, final int j, boolean flag) {
        org.spigotmc.AsyncCatcher.catchOp( "entity track"); // Spigot
        if (i > this.e) {
            i = this.e;
        }

        try {
            if (this.trackedEntities.b(entity.getId())) {
                throw new IllegalStateException("Entity is already tracked!");
            }

            EntityTrackerEntry entitytrackerentry = new EntityTrackerEntry(entity, i, j, flag);

            this.c.add(entitytrackerentry);
            this.trackedEntities.a(entity.getId(), entitytrackerentry);
            entitytrackerentry.scanPlayers(this.world.players);
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Adding entity to track");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity To Track");

            crashreportsystemdetails.a("Tracking range", (Object) (i + " blocks"));
            final int finalI = i; // CraftBukkit - fix decompile error
            crashreportsystemdetails.a("Update interval", new Callable() {
                public String a() throws Exception {
                    String s = "Once per " + finalI + " ticks"; // CraftBukkit

                    if (finalI == Integer.MAX_VALUE) { // CraftBukkit
                        s = "Maximum (" + s + ")";
                    }

                    return s;
                }

                public Object call() throws Exception {
                    return this.a();
                }
            });
            entity.appendEntityCrashDetails(crashreportsystemdetails);
            CrashReportSystemDetails crashreportsystemdetails1 = crashreport.a("Entity That Is Already Tracked");

            ((EntityTrackerEntry) this.trackedEntities.get(entity.getId())).tracker.appendEntityCrashDetails(crashreportsystemdetails1);

            try {
                throw new ReportedException(crashreport);
            } catch (ReportedException reportedexception) {
                Logger.error("\"Silently\" catching entity tracking error.", reportedexception);
            }
        }

    }

    public void untrackEntity(Entity entity) {
        org.spigotmc.AsyncCatcher.catchOp( "entity untrack"); // Spigot
        if (entity instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) entity;
            Iterator iterator = this.c.iterator();

            while (iterator.hasNext()) {
                EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();

                entitytrackerentry.a(entityplayer);
            }
        }

        EntityTrackerEntry entitytrackerentry1 = (EntityTrackerEntry) this.trackedEntities.d(entity.getId());

        if (entitytrackerentry1 != null) {
            this.c.remove(entitytrackerentry1);
            entitytrackerentry1.a();
        }

    }

    public void updatePlayers() {
        for (final EntityTrackerEntry entitytrackerentry : this.c) {
            entitytrackerentry.track(this.world.players);

            if (entitytrackerentry.n && entitytrackerentry.tracker instanceof EntityPlayer) {
                for (final EntityTrackerEntry entitytrackerentry1 : this.c) {
                    entitytrackerentry.track(this.world.players);

                    if (entitytrackerentry1.tracker != entitytrackerentry.tracker) {
                        entitytrackerentry1.updatePlayer((EntityPlayer)entitytrackerentry.tracker);
                    }
                }
            }
        }
    }

    public void a(EntityPlayer entityplayer) {
        Iterator iterator = this.c.iterator();

        while (iterator.hasNext()) {
            EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();

            if (entitytrackerentry.tracker == entityplayer) {
                entitytrackerentry.scanPlayers(this.world.players);
            } else {
                entitytrackerentry.updatePlayer(entityplayer);
            }
        }

    }

    public void a(Entity entity, Packet packet) {
        EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) this.trackedEntities.get(entity.getId());

        if (entitytrackerentry != null) {
            entitytrackerentry.broadcast(packet);
        }

    }

    public void sendPacketToEntity(Entity entity, Packet packet) {
        EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) this.trackedEntities.get(entity.getId());

        if (entitytrackerentry != null) {
            entitytrackerentry.broadcastIncludingSelf(packet);
        }

    }

    public void untrackPlayer(EntityPlayer entityplayer) {
        Iterator iterator = this.c.iterator();

        while (iterator.hasNext()) {
            EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();

            entitytrackerentry.clear(entityplayer);
        }

    }

    public void a(EntityPlayer entityplayer, Chunk chunk) {
        Iterator iterator = this.c.iterator();

        while (iterator.hasNext()) {
            EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();

            if (entitytrackerentry.tracker != entityplayer && entitytrackerentry.tracker.ae == chunk.locX && entitytrackerentry.tracker.ag == chunk.locZ) {
                entitytrackerentry.updatePlayer(entityplayer);
            }
        }

    }
}
