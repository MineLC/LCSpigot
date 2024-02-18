package org.spigotmc;

import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.tinylog.Logger;

public class WatchdogThread extends Thread
{

    private static WatchdogThread instance;
    private final long timeoutTime;
    private final boolean restart;
    private volatile long lastTick;
    private volatile boolean stopping;

    private WatchdogThread(long timeoutTime, boolean restart)
    {
        super( "Spigot Watchdog Thread" );
        this.timeoutTime = timeoutTime;
        this.restart = restart;
    }

    public static void doStart(int timeoutTime, boolean restart)
    {
        if ( instance == null )
        {
            instance = new WatchdogThread( timeoutTime * 1000L, restart );
            instance.start();
        }
    }

    public static void tick()
    {
        instance.lastTick = System.currentTimeMillis();
    }

    public static void doStop()
    {
        if ( instance != null )
        {
            instance.stopping = true;
        }
    }

    @Override
    public void run()
    {
        while ( !stopping )
        {
            //
            if ( lastTick != 0 && System.currentTimeMillis() > lastTick + timeoutTime )
            {
                Logger.error("The server has stopped responding!" );
                Logger.error("Please report this to http://www.spigotmc.org/" );
                Logger.error("Be sure to include ALL relevant console errors and Minecraft crash reports" );
                Logger.error("Spigot version: " + Bukkit.getServer().getVersion() );
                //
                if(net.minecraft.server.v1_8_R3.World.haveWeSilencedAPhysicsCrash)
                {
                    Logger.error("------------------------------" );
                    Logger.error("During the run of the server, a physics stackoverflow was supressed" );
                    Logger.error("near " + net.minecraft.server.v1_8_R3.World.blockLocation);
                }
                //
                Logger.error("------------------------------" );
                Logger.error("Server thread dump (Look for plugins here before reporting to Spigot!):" );
                dumpThread( ManagementFactory.getThreadMXBean().getThreadInfo( MinecraftServer.getServer().primaryThread.getId(), Integer.MAX_VALUE ));
                Logger.error("------------------------------" );
                //
                Logger.error("Entire Thread Dump:" );
                ThreadInfo[] threads = ManagementFactory.getThreadMXBean().dumpAllThreads( true, true );
                for ( ThreadInfo thread : threads )
                {
                    dumpThread( thread);
                }
                Logger.error("------------------------------" );

                if ( restart )
                {
                    RestartCommand.restart();
                }
                break;
            }

            try
            {
                sleep( 10000 );
            } catch ( InterruptedException ex )
            {
                interrupt();
            }
        }
    }

    private static void dumpThread(ThreadInfo thread)
    {
        Logger.error("------------------------------" );
        //
        Logger.error("Current Thread: " + thread.getThreadName() );
        Logger.error("\tPID: " + thread.getThreadId()
                + " | Suspended: " + thread.isSuspended()
                + " | Native: " + thread.isInNative()
                + " | State: " + thread.getThreadState() );
        if ( thread.getLockedMonitors().length != 0 )
        {
            Logger.error("\tThread is waiting on monitor(s):" );
            for ( MonitorInfo monitor : thread.getLockedMonitors() )
            {
                Logger.error("\t\tLocked on:" + monitor.getLockedStackFrame() );
            }
        }
        Logger.error("\tStack:" );
        //
        for ( StackTraceElement stack : thread.getStackTrace() )
        {
            Logger.error("\t\t" + stack );
        }
    }
}
