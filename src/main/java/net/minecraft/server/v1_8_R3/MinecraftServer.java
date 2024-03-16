package net.minecraft.server.v1_8_R3;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import javax.imageio.ImageIO;
import org.apache.commons.lang3.Validate;
import org.bukkit.craftbukkit.v1_8_R3.Main;
import org.bukkit.craftbukkit.v1_8_R3.SpigotTimings;
import org.tinylog.Logger;

// CraftBukkit start

import jline.console.ConsoleReader;
import joptsimple.OptionSet;
import lc.lcspigot.configuration.LCConfig;

public abstract class MinecraftServer extends ReentrantIAsyncHandler<TasksPerTick> implements Runnable, IAsyncTaskHandler, IMojangStatistics {

    public static final File a = new File("usercache.json");
    private static MinecraftServer l;
    public Convertable convertable; // CraftBukkit - remove final, public
    private final MojangStatisticsGenerator n = new MojangStatisticsGenerator("server", this, az());
    public File universe; // CraftBukkit - remove final, public
    private final List<IUpdatePlayerListBox> p = Lists.newArrayList();
    public final MethodProfiler methodProfiler = new MethodProfiler();
    private ServerConnection q; // Spigot
    private final ServerPing r = new ServerPing();
    private final Random s = new Random();
    private String serverIp;
    private int u = -1;
    public WorldServer[] worldServer;
    private PlayerList v;
    private boolean isRunning = true;
    private boolean isStopped;
    private int ticks;
    protected final Proxy e;
    public String f;
    public int g;
    private boolean onlineMode;
    private boolean spawnAnimals;
    private boolean spawnNPCs;
    private boolean pvpMode;
    private boolean allowFlight;
    private String motd;
    private int F;
    private int G = 0;
    public final long[] h = new long[100];
    public long[][] i;
    private KeyPair H;
    private String I;
    private String J;
    private boolean demoMode;
    private boolean M;
    private boolean N;
    private String O = "";
    private String P = "";
    private boolean Q;
    private long R;
    private String S;
    private boolean T;
    private boolean U;
    private final YggdrasilAuthenticationService V;
    private final MinecraftSessionService W;
    private long X = 0L;
    private final GameProfileRepository Y;
    private final UserCache Z;
    protected final Queue<FutureTask<?>> j = new CachedSizeConcurrentLinkedQueue<FutureTask<?>>(); // Spigot, PAIL: Rename
    private Thread serverThread;
    private long ab = az();

    // PandaSpigot start - Modern tick loop
    private long nextTickTime;
    private long delayedTasksMaxNextTickTime;
    private boolean mayHaveDelayedTasks;
    private boolean forceTicks;
    private boolean isOversleep = false;
    private volatile boolean isReady;
    private long lastOverloadWarning;
    public long serverStartTime;
    public volatile Thread shutdownThread;
    private long lastTick = 0;
    private long catchupTime = 0;

    // CraftBukkit start
    public List<WorldServer> worlds = new ArrayList<WorldServer>();
    public org.bukkit.craftbukkit.v1_8_R3.CraftServer server;
    public OptionSet options;
    public org.bukkit.command.ConsoleCommandSender console;
    public org.bukkit.command.RemoteConsoleCommandSender remoteConsole;
    public ConsoleReader reader;
    public static int currentTick = (int) (System.currentTimeMillis() / 50);
    public final Thread primaryThread;
    public java.util.Queue<Runnable> processQueue = new CachedSizeConcurrentLinkedQueue<Runnable>();
    public int autosavePeriod;
    // CraftBukkit end
    // Spigot start
    private static final int TPS = 20;
    private static final int TICK_TIME = 1000000000 / TPS;
    private static final int SAMPLE_INTERVAL = 100;
    public final double[] recentTps = new double[ 3 ];
    // Spigot end

    public MinecraftServer(OptionSet options, Proxy proxy, File file1) {
        super("Server");
        io.netty.util.ResourceLeakDetector.setEnabled( false ); // Spigot - disable
        this.e = proxy;
        MinecraftServer.l = this;
        // this.universe = file; // CraftBukkit
        // this.q = new ServerConnection(this); // Spigot
        this.Z = new UserCache(this, file1);
        // this.convertable = new WorldLoaderServer(file); // CraftBukkit - moved to DedicatedServer.init
        this.V = new YggdrasilAuthenticationService(proxy, UUID.randomUUID().toString());
        this.W = this.V.createMinecraftSessionService();
        this.Y = this.V.createProfileRepository();
        // CraftBukkit start
        this.options = options;
        // Try to see if we're actually running in a terminal, disable jline if not
        if (System.console() == null) {
            System.setProperty("jline.terminal", "jline.UnsupportedTerminal");
            Main.useJline = false;
        }

        try {
            reader = new ConsoleReader(System.in, System.out);
            reader.setExpandEvents(false); // Avoid parsing exceptions for uncommonly used event designators
        } catch (Throwable e) {
            try {
                // Try again with jline disabled for Windows users without C++ 2008 Redistributable
                System.setProperty("jline.terminal", "jline.UnsupportedTerminal");
                System.setProperty("user.language", "en");
                Main.useJline = false;
                reader = new ConsoleReader(System.in, System.out);
                reader.setExpandEvents(false);
            } catch (IOException ex) {
                Logger.warn((String) null, ex);
            }
        }
        Runtime.getRuntime().addShutdownHook(new org.bukkit.craftbukkit.v1_8_R3.util.ServerShutdownThread(this));

        this.serverThread = primaryThread = new Thread(this, "Server thread"); // Moved from main
    }

    public abstract PropertyManager getPropertyManager();
    // CraftBukkit end

    protected abstract boolean init() throws IOException;

    protected void a(String s) {
        if (this.getConvertable().isConvertable(s)) {
            Logger.info("Converting map!");
            this.b("menu.convertingLevel");
            this.getConvertable().convert(s, new IProgressUpdate() {
                private long b = System.currentTimeMillis();

                public void a(String s) {}

                public void a(int i) {
                    if (System.currentTimeMillis() - this.b >= 1000L) {
                        this.b = System.currentTimeMillis();
                        Logger.info("Converting... " + i + "%");
                    }

                }

                public void c(String s) {}
            });
        }

    }

    protected synchronized void b(String s) {
        this.S = s;
    }

    protected void a(String s, String s1, long i, WorldType worldtype, String s2) {
        this.a(s);
        this.b("menu.loadingLevel");
        this.worldServer = new WorldServer[3];
  
        int worldCount = 3;

        for (int j = 0; j < worldCount; ++j) {
            WorldServer world;
            byte dimension = 0;

            if (j == 1) {
                if (getAllowNether()) {
                    dimension = -1;
                } else {
                    continue;
                }
            }

            if (j == 2) {
                if (server.getAllowEnd()) {
                    dimension = 1;
                } else {
                    continue;
                }
            }

            String worldType = org.bukkit.World.Environment.getEnvironment(dimension).toString().toLowerCase();
            String name = (dimension == 0) ? s : s + "_" + worldType;

            org.bukkit.generator.ChunkGenerator gen = this.server.getGenerator(name);
            WorldSettings worldsettings = new WorldSettings(i, this.getGamemode(), this.getGenerateStructures(), this.isHardcore(), worldtype);
            worldsettings.setGeneratorSettings(s2);

            if (j == 0) {
                IDataManager idatamanager = new ServerNBTManager(server.getWorldContainer(), s1, true);
                WorldData worlddata = idatamanager.getWorldData();
                if (worlddata == null) {
                    worlddata = new WorldData(worldsettings, s1);
                }
                worlddata.checkName(s1); // CraftBukkit - Migration did not rewrite the level.dat; This forces 1.8 to take the last loaded world as respawn (in this case the end)
                world = (WorldServer) (new WorldServer(this, idatamanager, worlddata, dimension, this.methodProfiler, org.bukkit.World.Environment.getEnvironment(dimension), gen)).b();

                world.a(worldsettings);
                this.server.scoreboardManager = new org.bukkit.craftbukkit.v1_8_R3.scoreboard.CraftScoreboardManager(this, world.getScoreboard());
            } else {
                String dim = "DIM" + dimension;

                File newWorld = new File(new File(name), dim);
                File oldWorld = new File(new File(s), dim);

                if ((!newWorld.isDirectory()) && (oldWorld.isDirectory())) {
                    Logger.info("---- Migration of old " + worldType + " folder required ----");
                    Logger.info("Unfortunately due to the way that Minecraft implemented multiworld support in 1.6, Bukkit requires that you move your " + worldType + " folder to a new location in order to operate correctly.");
                    Logger.info("We will move this folder for you, but it will mean that you need to move it back should you wish to stop using Bukkit in the future.");
                    Logger.info("Attempting to move " + oldWorld + " to " + newWorld + "...");

                    if (newWorld.exists()) {
                        Logger.warn("A file or folder already exists at " + newWorld + "!");
                        Logger.info("---- Migration of old " + worldType + " folder failed ----");
                    } else if (newWorld.getParentFile().mkdirs()) {
                        if (oldWorld.renameTo(newWorld)) {
                            Logger.info("Success! To restore " + worldType + " in the future, simply move " + newWorld + " to " + oldWorld);
                            // Migrate world data too.
                            try {
                                com.google.common.io.Files.copy(new File(new File(s), "level.dat"), new File(new File(name), "level.dat"));
                            } catch (IOException exception) {
                                Logger.warn("Unable to migrate world data.");
                            }
                            Logger.info("---- Migration of old " + worldType + " folder complete ----");
                        } else {
                            Logger.warn("Could not move folder " + oldWorld + " to " + newWorld + "!");
                            Logger.info("---- Migration of old " + worldType + " folder failed ----");
                        }
                    } else {
                        Logger.warn("Could not create path for " + newWorld + "!");
                        Logger.info("---- Migration of old " + worldType + " folder failed ----");
                    }
                }

                IDataManager idatamanager = new ServerNBTManager(server.getWorldContainer(), name, true);
                // world =, b0 to dimension, s1 to name, added Environment and gen
                WorldData worlddata = idatamanager.getWorldData();
                if (worlddata == null) {
                    worlddata = new WorldData(worldsettings, name);
                }
                worlddata.checkName(name); // CraftBukkit - Migration did not rewrite the level.dat; This forces 1.8 to take the last loaded world as respawn (in this case the end)
                world = (WorldServer) new SecondaryWorldServer(this, idatamanager, dimension, this.worlds.get(0), this.methodProfiler, worlddata, org.bukkit.World.Environment.getEnvironment(dimension), gen).b();
            }

            this.server.getPluginManager().callEvent(new org.bukkit.event.world.WorldInitEvent(world.getWorld()));

            world.addIWorldAccess(new WorldManager(this, world));
            if (!this.T()) {
                world.getWorldData().setGameType(this.getGamemode());
            }

            worlds.add(world);
            getPlayerList().setPlayerFileData(worlds.toArray(new WorldServer[worlds.size()]));
        }

        // CraftBukkit end
        this.a(this.getDifficulty());
        this.k();
    }

    protected void k() {
        int i = 0;

        this.b("menu.generatingTerrain");

        // CraftBukkit start - fire WorldLoadEvent and handle whether or not to keep the spawn in memory
        for (int m = 0; m < worlds.size(); m++) {
            WorldServer worldserver = this.worlds.get(m);
            Logger.info("Preparing start region for level " + m + " (Seed: " + worldserver.getSeed() + ")");

            if (!worldserver.getWorld().getKeepSpawnInMemory()) {
                continue;
            }

            BlockPosition blockposition = worldserver.getSpawn();
            long j = az();
            i = 0;

            for (int k = -192; k <= 192 && this.isRunning(); k += 16) {
                for (int l = -192; l <= 192 && this.isRunning(); l += 16) {
                    long i1 = az();

                    if (i1 - j > 1000L) {
                        this.a_("Preparing spawn area", i * 100 / 625);
                        j = i1;
                    }

                    ++i;
                    worldserver.chunkProviderServer.getChunkAt(blockposition.getX() + k >> 4, blockposition.getZ() + l >> 4);
                }
            }
        }

        for (WorldServer world : this.worlds) {
            this.server.getPluginManager().callEvent(new org.bukkit.event.world.WorldLoadEvent(world.getWorld()));
        }
        // CraftBukkit end
        this.s();
    }

    protected void a(String s, IDataManager idatamanager) {
        File file = new File(idatamanager.getDirectory(), "resources.zip");

        if (file.isFile()) {
            this.setResourcePack("level://" + s + "/" + file.getName(), "");
        }

    }

    public abstract boolean getGenerateStructures();

    public abstract WorldSettings.EnumGamemode getGamemode();

    public abstract EnumDifficulty getDifficulty();

    public abstract boolean isHardcore();

    public abstract int p();

    public abstract boolean q();

    public abstract boolean r();

    protected void a_(String s, int i) {
        this.f = s;
        this.g = i;
        Logger.info(s + ": " + i + "%");
    }

    protected void s() {
        this.f = null;
        this.g = 0;

        this.server.enablePlugins(org.bukkit.plugin.PluginLoadOrder.POSTWORLD); // CraftBukkit
    }

    protected void saveChunks(boolean flag) throws ExceptionWorldConflict { // CraftBukkit - added throws
        if (!this.N && LCConfig.getConfig().canSaveWorlds) {
            // CraftBukkit start
            for (int j = 0; j < worlds.size(); ++j) {
                WorldServer worldserver = worlds.get(j);
                // CraftBukkit end

                if (worldserver != null) {
                    if (!flag) {
                        Logger.info("Saving chunks for level \'" + worldserver.getWorldData().getName() + "\'/" + worldserver.worldProvider.getName());
                    }

                    try {
                        worldserver.save(true, (IProgressUpdate) null);
                        worldserver.saveLevel(); // CraftBukkit
                    } catch (ExceptionWorldConflict exceptionworldconflict) {
                        Logger.warn(exceptionworldconflict.getMessage());
                    }
                }
            }

        }
    }

    // CraftBukkit start
    private boolean hasStopped = false;
    private final Object stopLock = new Object();
    // CraftBukkit end

    public void stop() throws ExceptionWorldConflict { // CraftBukkit - added throws
        // CraftBukkit start - prevent double stopping on multiple threads
        synchronized(stopLock) {
            if (hasStopped) return;
            hasStopped = true;
        }
        // CraftBukkit end
        if (!this.N) {
            Logger.info("Stopping server");
            // CraftBukkit start
            if (this.server != null) {
                this.server.disablePlugins();
            }
            // CraftBukkit end
            if (this.aq() != null) {
                this.aq().b();
            }

            if (this.v != null) {
                Logger.info("Saving players");
                this.v.savePlayers();
                this.v.u();
            }

            if (this.worldServer != null) {
                Logger.info("Saving worlds");
                this.saveChunks(false);

                /* CraftBukkit start - Handled in saveChunks
                for (int i = 0; i < this.worldServer.length; ++i) {
                    WorldServer worldserver = this.worldServer[i];

                    worldserver.saveLevel();
                }
                // CraftBukkit end */
            }

            if (this.n.d()) {
                this.n.e();
            }
            // Spigot start
            if( org.spigotmc.SpigotConfig.saveUserCacheOnStopOnly )
            {
                Logger.info("Saving usercache.json");
                this.Z.c();
            }
            //Spigot end
        }
    }

    public String getServerIp() {
        return this.serverIp;
    }

    public void c(String s) {
        this.serverIp = s;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public void safeShutdown() {
        this.isRunning = false;
    }

    // Spigot Start
    private static double calcTps(double avg, double exp, double tps)
    {
        return ( avg * exp ) + ( tps * ( 1 - exp ) );
    }
    // Spigot End
 
    public void run() {
        try {
            if (this.init()) {
                System.gc();
    
                this.ab = az();

                this.r.setMOTD(new ChatComponentText(this.motd));
                this.r.setServerInfo(new ServerPing.ServerData("1.8.4", 47));
                this.a(this.r);

                // Spigot start
                Arrays.fill( recentTps, 20 );
                // PandaSpigot start - Modern tick loop
                long start = System.nanoTime(), curTime, tickSection = start;
                lastTick = start - TICK_TIME;
                // PandaSpigot end
                
                while (this.isRunning) {
                    long i = ((curTime = System.nanoTime()) / 1_000_000L) - this.nextTickTime; // Paper
                    if (i > 5000L && this.nextTickTime - this.lastOverloadWarning >= 30000L && ticks > 500) { // CraftBukkit
                        long j = i / 50L;

                        Logger.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", i, j);

                        this.nextTickTime += j * 50L;
                        this.lastOverloadWarning = this.nextTickTime;
                    }

                    if (MinecraftServer.currentTick++ % SAMPLE_INTERVAL == 0) {
                        double currentTps = 1E9 / ( curTime - tickSection ) * SAMPLE_INTERVAL;
                        recentTps[0] = calcTps( recentTps[0], 0.92, currentTps ); // 1/exp(5sec/1min)
                        recentTps[1] = calcTps( recentTps[1], 0.9835, currentTps ); // 1/exp(5sec/5min)
                        recentTps[2] = calcTps( recentTps[2], 0.9945, currentTps ); // 1/exp(5sec/15min)
                        tickSection = curTime;
                    }
                    lastTick = curTime;

                    this.nextTickTime += 50L;
                    this.methodProfiler.a("tick");
                    this.A(this::haveTime);
                    this.methodProfiler.c("nextTickWait");
                    this.mayHaveDelayedTasks = true;
                    this.delayedTasksMaxNextTickTime = Math.max(getMillis() + 50L, this.nextTickTime);
                    this.waitUntilNextTick();
                    this.methodProfiler.b();
                    this.isReady = true;
                    // PandaSpigot end
                }
                // Spigot end
            } else {
                this.a((CrashReport) null);
            }
        } catch (Throwable throwable) {
            Logger.error("Encountered an unexpected exception", throwable);
            // Spigot Start
            if ( throwable.getCause() != null )
            {
                Logger.error( "\tCause of unexpected exception was", throwable.getCause() );
            }
            // Spigot End
            CrashReport crashreport = null;

            if (throwable instanceof ReportedException) {
                crashreport = this.b(((ReportedException) throwable).a());
            } else {
                crashreport = this.b(new CrashReport("Exception in server tick loop", throwable));
            }

            File file = new File(new File(this.y(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");

            if (crashreport.a(file)) {
                Logger.error("This crash report has been saved to: " + file.getAbsolutePath());
            } else {
                Logger.error("We were unable to save this crash report to disk.");
            }

            this.a(crashreport);
        } finally {
            try {
                org.spigotmc.WatchdogThread.doStop();
                this.isStopped = true;
                this.stop();
            } catch (Throwable throwable1) {
                Logger.error("Exception stopping the server", throwable1);
            } finally {
                // CraftBukkit start - Restore terminal to original settings
                try {
                    reader.getTerminal().restore();
                } catch (Exception ignored) {
                }
                // CraftBukkit end
                this.z();
            }

        }

    }

    private void a(ServerPing serverping) {
        File file = this.d("server-icon.png");
        ByteBuf bytebuf1 = null;
        if (file.isFile()) {
            ByteBuf bytebuf = Unpooled.buffer();

            try {
                BufferedImage bufferedimage = ImageIO.read(file);

                Validate.validState(bufferedimage.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                Validate.validState(bufferedimage.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
                ImageIO.write(bufferedimage, "PNG", new ByteBufOutputStream(bytebuf));
                bytebuf1 = Base64.encode(bytebuf);

                serverping.setFavicon("data:image/png;base64," + bytebuf1.toString(Charsets.UTF_8));
            } catch (Exception exception) {
                Logger.error("Couldn\'t load server icon", exception);
            } finally {
                bytebuf.release();
                if (bytebuf1 != null) bytebuf1.release();
            }
        }

    }

    public File y() {
        return new File(".");
    }

    protected void a(CrashReport crashreport) {}

    protected void z() {}

    protected void A(java.util.function.BooleanSupplier shouldKeepTicking) throws ExceptionWorldConflict { // CraftBukkit - added throws
        SpigotTimings.serverTickTimer.startTiming(); // Spigot
        long i = System.nanoTime();
        isOversleep = true;
        this.controlTerminate(() -> !this.canOversleep());
        isOversleep = false;
        // PandaSpigot end

        ++this.ticks;
        if (this.T) {
            this.T = false;
            this.methodProfiler.a = true;
            this.methodProfiler.a();
        }

        this.methodProfiler.a("root");
        this.B();
        if (i - this.X >= 5000000000L) {
            this.X = i;
            this.r.setPlayerSample(new ServerPing.ServerPingPlayerSample(this.J(), this.I()));
            GameProfile[] agameprofile = new GameProfile[Math.min(this.I(), 12)];
            int j = MathHelper.nextInt(this.s, 0, this.I() - agameprofile.length);

            for (int k = 0; k < agameprofile.length; ++k) {
                agameprofile[k] = ((EntityPlayer) this.v.v().get(j + k)).getProfile();
            }

            Collections.shuffle(Arrays.asList(agameprofile));
            this.r.b().a(agameprofile);
        }

        if (LCConfig.getConfig().canSaveWorlds && autosavePeriod > 0 && this.ticks % autosavePeriod == 0) { // CraftBukkit
            SpigotTimings.worldSaveTimer.startTiming(); // Spigot
            this.methodProfiler.a("save");
            this.v.savePlayers();
            // Spigot Start
            // We replace this with saving each individual world as this.saveChunks(...) is broken,
            // and causes the main thread to sleep for random amounts of time depending on chunk activity
            // Also pass flag to only save modified chunks
            server.playerCommandState = true;
            for (World world : worlds) {
                world.getWorld().save(false);
            }
            server.playerCommandState = false;
            // this.saveChunks(true);
            // Spigot End
            this.methodProfiler.b();
            SpigotTimings.worldSaveTimer.stopTiming(); // Spigot
        }

        // PandaSpigot start - Modern tick loop
        long endTime = System.nanoTime();
        long remaining = (TICK_TIME - (endTime - lastTick)) - catchupTime;
        // PandaSpigot end

        this.methodProfiler.a("tallying");
        this.h[this.ticks % 100] = System.nanoTime() - i;
        this.methodProfiler.b();
        this.methodProfiler.a("snooper");
        if (getSnooperEnabled() && !this.n.d() && this.ticks > 100) {  // Spigot
            this.n.a();
        }

        if (getSnooperEnabled() && this.ticks % 6000 == 0) { // Spigot
            this.n.b();
        }

        this.methodProfiler.b();
        this.methodProfiler.b();
        org.spigotmc.WatchdogThread.tick(); // Spigot
        SpigotTimings.serverTickTimer.stopTiming(); // Spigot
        org.spigotmc.CustomTimingsHandler.tick(); // Spigot
    }

    public void B() {
        this.methodProfiler.a("jobs");

        // Spigot start
        FutureTask<?> entry;
        int count = this.j.size();
        while (count-- > 0 && (entry = this.j.poll()) != null) {
            SystemUtils.a(entry);
         }
        // Spigot end

        this.methodProfiler.c("levels");

        SpigotTimings.schedulerTimer.startTiming(); // Spigot
        // CraftBukkit start
        this.server.getScheduler().mainThreadHeartbeat(this.ticks);
        SpigotTimings.schedulerTimer.stopTiming(); // Spigot

        // Run tasks that are waiting on processing
        SpigotTimings.processQueueTimer.startTiming(); // Spigot
        while (!processQueue.isEmpty()) {
            processQueue.remove().run();
        }
        SpigotTimings.processQueueTimer.stopTiming(); // Spigot

        SpigotTimings.chunkIOTickTimer.startTiming(); // Spigot
        org.bukkit.craftbukkit.v1_8_R3.chunkio.ChunkIOExecutor.tick();
        SpigotTimings.chunkIOTickTimer.stopTiming(); // Spigot

        SpigotTimings.timeUpdateTimer.startTiming(); // Spigot
        // Send time updates to everyone, it will get the right time from the world the player is in.
        if (this.ticks % LCConfig.getConfig().tickTime == 0) {
            for (final WorldServer world : this.worlds) {
                if (!world.getGameRules().getBoolean("doDaylightCycle")) {
                    break;
                }
                final PacketPlayOutUpdateTime packet = new PacketPlayOutUpdateTime(world.getTime(), world.getDayTime(), true);
                for (final EntityHuman human : world.players) {
                    if (!(human instanceof EntityPlayer)) {
                        continue;
                    }
                    ((EntityPlayer)human).playerConnection.sendPacket(packet);
                }
            }
        }
        SpigotTimings.timeUpdateTimer.stopTiming(); // Spigot

        int i;

        for (i = 0; i < this.worlds.size(); ++i) {
            // if (i == 0 || this.getAllowNether()) {
                WorldServer worldserver = this.worlds.get(i);

                this.methodProfiler.a(worldserver.getWorldData().getName());
                /* Drop global time updates
                if (this.ticks % 20 == 0) {
                    this.methodProfiler.a("timeSync");
                    this.v.a(new PacketPlayOutUpdateTime(worldserver.getTime(), worldserver.getDayTime(), worldserver.getGameRules().getBoolean("doDaylightCycle")), worldserver.worldProvider.getDimension());
                    this.methodProfiler.b();
                }
                // CraftBukkit end */

                this.methodProfiler.a("tick");

                CrashReport crashreport;

                try {
                    worldserver.timings.doTick.startTiming(); // Spigot
                    worldserver.doTick();
                    worldserver.timings.doTick.stopTiming(); // Spigot
                } catch (Throwable throwable) {
                    // Spigot Start
                    try {
                    crashreport = CrashReport.a(throwable, "Exception ticking world");
                    } catch (Throwable t){
                        throw new RuntimeException("Error generating crash report", t);
                    }
                    // Spigot End
                    worldserver.a(crashreport);
                    throw new ReportedException(crashreport);
                }

                try {
                    worldserver.timings.tickEntities.startTiming(); // Spigot
                    worldserver.tickEntities();
                    worldserver.timings.tickEntities.stopTiming(); // Spigot
                } catch (Throwable throwable1) {
                    // Spigot Start
                    try {
                    crashreport = CrashReport.a(throwable1, "Exception ticking world entities");
                    } catch (Throwable t){
                        throw new RuntimeException("Error generating crash report", t);
                    }
                    // Spigot End
                    worldserver.a(crashreport);
                    throw new ReportedException(crashreport);
                }

                this.methodProfiler.b();
                this.methodProfiler.a("tracker");
                // PandaSpigot end
                worldserver.timings.tracker.startTiming(); // Spigot
                // PandaSpigot start - controlled flush for entity tracker packets
                List<NetworkManager> disabledFlushes = new ArrayList<>(worldserver.players.size());
                for (EntityHuman player : worldserver.players) {
                    if (!(player instanceof EntityPlayer)) continue; // skip non-player entities
                    PlayerConnection connection = ((EntityPlayer) player).playerConnection;
                    if (connection != null) {
                        connection.networkManager.disableAutomaticFlush();
                        disabledFlushes.add(connection.networkManager);
                    }
                }
                try {
                    worldserver.getTracker().updatePlayers();
                    // PandaSpigot start - controlled flush for entity tracker packets
                } finally {
                    for (NetworkManager networkManager : disabledFlushes) {
                        networkManager.enableAutomaticFlush();
                    }
                }
                // PandaSpigot end
                worldserver.timings.tracker.stopTiming(); // Spigot
                this.methodProfiler.b();
                this.methodProfiler.b();
            // } // CraftBukkit

            // this.i[i][this.ticks % 100] = System.nanoTime() - j; // CraftBukkit
        }

        this.methodProfiler.c("connection");
        SpigotTimings.connectionTimer.startTiming(); // Spigot
        this.aq().c();
        SpigotTimings.connectionTimer.stopTiming(); // Spigot
        this.methodProfiler.c("players");
        SpigotTimings.playerListTimer.startTiming(); // Spigot
        this.v.tick();
        SpigotTimings.playerListTimer.stopTiming(); // Spigot
        this.methodProfiler.c("tickables");

        SpigotTimings.tickablesTimer.startTiming(); // Spigot
        for (i = 0; i < this.p.size(); ++i) {
            ((IUpdatePlayerListBox) this.p.get(i)).c();
        }
        SpigotTimings.tickablesTimer.stopTiming(); // Spigot

        this.methodProfiler.b();
    }

    public boolean getAllowNether() {
        return true;
    }

    public void a(IUpdatePlayerListBox iupdateplayerlistbox) {
        this.p.add(iupdateplayerlistbox);
    }

    public static void main(final OptionSet options) { // CraftBukkit - replaces main(String[] astring)
        DispenserRegistry.c();

        try {
            DedicatedServer dedicatedserver = new DedicatedServer(options);

            if (options.has("port")) {
                int port = (Integer) options.valueOf("port");
                if (port > 0) {
                    dedicatedserver.setPort(port);
                }
            }

            if (options.has("universe")) {
                dedicatedserver.universe = (File) options.valueOf("universe");
            }

            if (options.has("world")) {
                dedicatedserver.setWorld((String) options.valueOf("world"));
            }

            dedicatedserver.primaryThread.start();
            // CraftBukkit end
        } catch (Exception exception) {
            Logger.error("Failed to start the minecraft server", exception);
        }

    }

    public void C() {
        /* CraftBukkit start - prevent abuse
        this.serverThread = new Thread(this, "Server thread");
        this.serverThread.start();
        // CraftBukkit end */
    }

    public File d(String s) {
        return new File(this.y(), s);
    }

    public void info(String s) {
        Logger.info(s);
    }

    public void warning(String s) {
        Logger.warn(s);
    }

    public WorldServer getWorldServer(int i) {
        // CraftBukkit start
        for (WorldServer world : worlds) {
            if (world.dimension == i) {
                return world;
            }
        }
        return worlds.get(0);
        // CraftBukkit end
    }

    public String E() {
        return this.serverIp;
    }

    public int F() {
        return this.u;
    }

    public String G() {
        return this.motd;
    }

    public String getVersion() {
        return "1.8.4";
    }

    public int I() {
        return this.v.getPlayerCount();
    }

    public int J() {
        return this.v.getMaxPlayers();
    }

    public String[] getPlayers() {
        return this.v.f();
    }

    public GameProfile[] L() {
        return this.v.g();
    }

    public boolean isDebugging() {
        return this.getPropertyManager().getBoolean("debug", false); // CraftBukkit - don't hardcode
    }

    public void g(String s) {
        Logger.error(s);
    }


    public String getServerModName() {
        return "Spigot"; // Spigot - Spigot > // CraftBukkit - cb > vanilla!
    }

    public CrashReport b(CrashReport crashreport) {
        crashreport.g().a("Profiler Position", new Callable() {
            public String a() throws Exception {
                return MinecraftServer.this.methodProfiler.a ? MinecraftServer.this.methodProfiler.c() : "N/A (disabled)";
            }

            public Object call() throws Exception {
                return this.a();
            }
        });
        if (this.v != null) {
            crashreport.g().a("Player Count", new Callable() {
                public String a() {
                    return MinecraftServer.this.v.getPlayerCount() + " / " + MinecraftServer.this.v.getMaxPlayers() + "; " + MinecraftServer.this.v.v();
                }

                public Object call() throws Exception {
                    return this.a();
                }
            });
        }

        return crashreport;
    }

    public static MinecraftServer getServer() {
        return MinecraftServer.l;
    }

    public boolean O() {
        return true; // CraftBukkit
    }

    public String getName() {
        return "Server";
    }

    public void sendMessage(IChatBaseComponent ichatbasecomponent) {
        Logger.info(ichatbasecomponent.c());
    }

    public boolean a(int i, String s) {
        return true;
    }

    public KeyPair Q() {
        return this.H;
    }

    public int R() {
        return this.u;
    }

    public void setPort(int i) {
        this.u = i;
    }

    public String S() {
        return this.I;
    }

    public void i(String s) {
        this.I = s;
    }

    public boolean T() {
        return this.I != null;
    }

    public String U() {
        return this.J;
    }

    public void setWorld(String s) {
        this.J = s;
    }

    public void a(KeyPair keypair) {
        this.H = keypair;
    }

    public void a(EnumDifficulty enumdifficulty) {
        // CraftBukkit start
        for (int i = 0; i < this.worlds.size(); ++i) {
            WorldServer worldserver = this.worlds.get(i);
            // CraftBukkit end

            if (worldserver != null) {
                if (worldserver.getWorldData().isHardcore()) {
                    worldserver.getWorldData().setDifficulty(EnumDifficulty.HARD);
                    worldserver.setSpawnFlags(true, true);
                } else if (this.T()) {
                    worldserver.getWorldData().setDifficulty(enumdifficulty);
                    worldserver.setSpawnFlags(worldserver.getDifficulty() != EnumDifficulty.PEACEFUL, true);
                } else {
                    worldserver.getWorldData().setDifficulty(enumdifficulty);
                    worldserver.setSpawnFlags(this.getSpawnMonsters(), this.spawnAnimals);
                }
            }
        }

    }

    protected boolean getSpawnMonsters() {
        return true;
    }

    public boolean X() {
        return this.demoMode;
    }

    public void b(boolean flag) {
        this.demoMode = flag;
    }

    public void c(boolean flag) {
        this.M = flag;
    }

    public Convertable getConvertable() {
        return this.convertable;
    }

    public void aa() {
        this.N = true;
        this.getConvertable().d();

        // CraftBukkit start
        for (int i = 0; i < this.worlds.size(); ++i) {
            WorldServer worldserver = this.worlds.get(i);
            // CraftBukkit end
            
            if (worldserver != null) {
                worldserver.saveLevel();
            }
        }

        this.getConvertable().e(this.worlds.get(0).getDataManager().g()); // CraftBukkit
        this.safeShutdown();
    }

    public String getResourcePack() {
        return this.O;
    }

    public String getResourcePackHash() {
        return this.P;
    }

    public void setResourcePack(String s, String s1) {
        this.O = s;
        this.P = s1;
    }

    public void a(MojangStatisticsGenerator mojangstatisticsgenerator) {
        mojangstatisticsgenerator.a("whitelist_enabled", Boolean.valueOf(false));
        mojangstatisticsgenerator.a("whitelist_count", Integer.valueOf(0));
        if (this.v != null) {
            mojangstatisticsgenerator.a("players_current", Integer.valueOf(this.I()));
            mojangstatisticsgenerator.a("players_max", Integer.valueOf(this.J()));
            mojangstatisticsgenerator.a("players_seen", Integer.valueOf(this.v.getSeenPlayers().length));
        }

        mojangstatisticsgenerator.a("uses_auth", Boolean.valueOf(this.onlineMode));
        mojangstatisticsgenerator.a("gui_state", this.as() ? "enabled" : "disabled");
        mojangstatisticsgenerator.a("run_time", Long.valueOf((az() - mojangstatisticsgenerator.g()) / 60L * 1000L));
        mojangstatisticsgenerator.a("avg_tick_ms", Integer.valueOf((int) (MathHelper.a(this.h) * 1.0E-6D)));
        int i = 0;

        if (this.worldServer != null) {
            // CraftBukkit start
            for (int j = 0; j < this.worlds.size(); ++j) {
                WorldServer worldserver = this.worlds.get(j);
                if (worldserver != null) {
                    // CraftBukkit end
                    WorldData worlddata = worldserver.getWorldData();

                    mojangstatisticsgenerator.a("world[" + i + "][dimension]", Integer.valueOf(worldserver.worldProvider.getDimension()));
                    mojangstatisticsgenerator.a("world[" + i + "][mode]", worlddata.getGameType());
                    mojangstatisticsgenerator.a("world[" + i + "][difficulty]", worldserver.getDifficulty());
                    mojangstatisticsgenerator.a("world[" + i + "][hardcore]", Boolean.valueOf(worlddata.isHardcore()));
                    mojangstatisticsgenerator.a("world[" + i + "][generator_name]", worlddata.getType().name());
                    mojangstatisticsgenerator.a("world[" + i + "][generator_version]", Integer.valueOf(worlddata.getType().getVersion()));
                    mojangstatisticsgenerator.a("world[" + i + "][height]", Integer.valueOf(this.F));
                    mojangstatisticsgenerator.a("world[" + i + "][chunks_loaded]", Integer.valueOf(worldserver.N().getLoadedChunks()));
                    ++i;
                }
            }
        }

        mojangstatisticsgenerator.a("worlds", Integer.valueOf(i));
    }

    public void b(MojangStatisticsGenerator mojangstatisticsgenerator) {
        mojangstatisticsgenerator.b("singleplayer", Boolean.valueOf(this.T()));
        mojangstatisticsgenerator.b("server_brand", this.getServerModName());
        mojangstatisticsgenerator.b("gui_supported", GraphicsEnvironment.isHeadless() ? "headless" : "supported");
        mojangstatisticsgenerator.b("dedicated", Boolean.valueOf(this.ae()));
    }

    public boolean getSnooperEnabled() {
        return true;
    }

    public abstract boolean ae();

    public boolean getOnlineMode() {
        return server.getOnlineMode(); // CraftBukkit
    }

    public void setOnlineMode(boolean flag) {
        this.onlineMode = flag;
    }

    public boolean getSpawnAnimals() {
        return this.spawnAnimals;
    }

    public void setSpawnAnimals(boolean flag) {
        this.spawnAnimals = flag;
    }

    public boolean getSpawnNPCs() {
        return this.spawnNPCs;
    }

    public abstract boolean ai();

    public void setSpawnNPCs(boolean flag) {
        this.spawnNPCs = flag;
    }

    public boolean getPVP() {
        return this.pvpMode;
    }

    public void setPVP(boolean flag) {
        this.pvpMode = flag;
    }

    public boolean getAllowFlight() {
        return this.allowFlight;
    }

    public void setAllowFlight(boolean flag) {
        this.allowFlight = flag;
    }

    public abstract boolean getEnableCommandBlock();

    public String getMotd() {
        return this.motd;
    }

    public void setMotd(String s) {
        this.motd = s;
    }

    public int getMaxBuildHeight() {
        return this.F;
    }

    public void c(int i) {
        this.F = i;
    }

    public boolean isStopped() {
        return this.isStopped;
    }

    public PlayerList getPlayerList() {
        return this.v;
    }

    public void a(PlayerList playerlist) {
        this.v = playerlist;
    }

    public void setGamemode(WorldSettings.EnumGamemode worldsettings_enumgamemode) {
        // CraftBukkit start
        for (int i = 0; i < this.worlds.size(); ++i) {
            getServer().worlds.get(i).getWorldData().setGameType(worldsettings_enumgamemode);
        }

    }

    // Spigot Start
    public ServerConnection getServerConnection()
    {
        return this.q;
    }
    // Spigot End
    public ServerConnection aq() {
        return this.q == null ? this.q = new ServerConnection(this) : this.q; // Spigot
    }

    public boolean as() {
        return false;
    }

    public abstract String a(WorldSettings.EnumGamemode worldsettings_enumgamemode, boolean flag);

    public int at() {
        return this.ticks;
    }

    public void au() {
        this.T = true;
    }

    public BlockPosition getChunkCoordinates() {
        return BlockPosition.ZERO;
    }

    public Vec3D d() {
        return new Vec3D(0.0D, 0.0D, 0.0D);
    }

    public World getWorld() {
        return this.worlds.get(0); // CraftBukkit
    }

    public Entity f() {
        return null;
    }

    public int getSpawnProtection() {
        return 16;
    }

    public boolean a(World world, BlockPosition blockposition, EntityHuman entityhuman) {
        return false;
    }

    public void setForceGamemode(boolean flag) {
        this.U = flag;
    }

    public boolean getForceGamemode() {
        return this.U;
    }

    public Proxy ay() {
        return this.e;
    }

    public static long az() {
        return System.currentTimeMillis();
    }

    public int getIdleTimeout() {
        return this.G;
    }

    public void setIdleTimeout(int i) {
        this.G = i;
    }

    public IChatBaseComponent getScoreboardDisplayName() {
        return new ChatComponentText(this.getName());
    }

    public boolean aB() {
        return true;
    }

    public MinecraftSessionService aD() {
        return this.W;
    }

    public GameProfileRepository getGameProfileRepository() {
        return this.Y;
    }

    public UserCache getUserCache() {
        return this.Z;
    }

    public ServerPing aG() {
        return this.r;
    }

    public void aH() {
        this.X = 0L;
    }

    public Entity a(UUID uuid) {
        WorldServer[] aworldserver = this.worldServer;
        int i = aworldserver.length;

        // CraftBukkit start
        for (int j = 0; j < worlds.size(); ++j) {
            WorldServer worldserver = worlds.get(j);
            // CraftBukkit end

            if (worldserver != null) {
                Entity entity = worldserver.getEntity(uuid);

                if (entity != null) {
                    return entity;
                }
            }
        }

        return null;
    }

    public boolean getSendCommandFeedback() {
        return getServer().worlds.get(0).getGameRules().getBoolean("sendCommandFeedback");
    }

    public int aI() {
        return 29999984;
    }

    public <V> ListenableFuture<V> a(Callable<V> callable) {
        Validate.notNull(callable);
        if (!this.isMainThread()) { // CraftBukkit && !this.isStopped()) {
            ListenableFutureTask listenablefuturetask = ListenableFutureTask.create(callable);
            Queue queue = this.j;

            // Spigot start
            this.j.add(listenablefuturetask);
            return listenablefuturetask;
            // Spigot end
        } else {
            try {
                return Futures.immediateFuture(callable.call());
            } catch (Exception exception) {
                return Futures.immediateFailedCheckedFuture(exception);
            }
        }
    }

    public ListenableFuture<Object> postToMainThread(Runnable runnable) {
        Validate.notNull(runnable);
        return this.a(Executors.callable(runnable));
    }

    public boolean isMainThread() {
        return Thread.currentThread() == this.serverThread;
    }

    public int aK() {
        return 256;
    }

    public long aL() {
        return this.ab;
    }

    public Thread aM() {
        return this.serverThread;
    }

    // PandaSpigot start - Modern tick loop
    public static long getMillis() {
        return getNanos() / 1000000L;
    }
    public static long getNanos() {
        return System.nanoTime();
    }
    // PandaSpigot start - Modern tick loop
    private boolean haveTime() {
        if (isOversleep) return canOversleep();
        return this.forceTicks || this.runningTask() || getMillis() < (this.mayHaveDelayedTasks ? this.delayedTasksMaxNextTickTime : this.nextTickTime);
    }

    private boolean canOversleep() {
        return this.mayHaveDelayedTasks && getMillis() < this.delayedTasksMaxNextTickTime;
    }

    private boolean canSleepForTickNoOversleep() {
        return this.forceTicks || this.runningTask() || getMillis() < this.nextTickTime;
    }

    protected void waitUntilNextTick() {
        this.controlTerminate(() -> !this.canSleepForTickNoOversleep());
    }

    protected TasksPerTick packUpRunnable(Runnable runnable) {
        // anything that does try to post to main during watchdog crash, run on watchdog
        if (this.hasStopped && Thread.currentThread().equals(shutdownThread)) {
            runnable.run();
            runnable = () -> {};
        }
        return new TasksPerTick(this.ticks, runnable);
    }

    protected boolean shouldRun(TasksPerTick task) {
        return task.getTick() + 3 < this.ticks || this.haveTime();
    }

    public boolean drawRunnable() {
        boolean flag = this.pollTaskInternal();

        this.mayHaveDelayedTasks = flag;
        return flag;
    }

    private boolean pollTaskInternal() {
        return super.drawRunnable();
    }

    public Thread getMainThread() {
        return serverThread;
    }
    // PandaSpigot end
}
