package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import org.tinylog.Logger;

public class ServerConnection {

    public static final LazyInitVar<NioEventLoopGroup> a = new LazyInitVar<NioEventLoopGroup>() {
        protected NioEventLoopGroup init() {
            return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Server IO #%d").setDaemon(true).build());
        }
    };
    public static final LazyInitVar<EpollEventLoopGroup> b = new LazyInitVar<EpollEventLoopGroup>() {
        protected EpollEventLoopGroup init() {
            return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build());
        }
    };
    public static final LazyInitVar<LocalEventLoopGroup> c = new LazyInitVar<LocalEventLoopGroup>() {
        protected LocalEventLoopGroup init() {
            return new LocalEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Local Server IO #%d").setDaemon(true).build());
        }
    };
    private final MinecraftServer f;
    public volatile boolean d;
    private final List<ChannelFuture> g = Collections.synchronizedList(Lists.<ChannelFuture>newArrayList());
    private final List<NetworkManager> h = Collections.synchronizedList(Lists.<NetworkManager>newArrayList());

    public ServerConnection(MinecraftServer minecraftserver) {
        this.f = minecraftserver;
        this.d = true;
    }

    public void a(InetAddress inetaddress, int i) throws IOException {
        synchronized (this.g) {
            LazyInitVar lazyinitvar;
            ServerBootstrap bootstrap = new ServerBootstrap();

            if (Epoll.isAvailable() && this.f.ai()) {
                bootstrap = bootstrap.channel(EpollServerSocketChannel.class);
                lazyinitvar = ServerConnection.b;
                Logger.info("Using epoll channel type");
            } else {
                bootstrap = bootstrap.channel(NioServerSocketChannel.class);
                lazyinitvar = ServerConnection.a;
                Logger.info("Using default channel type");
            }

            this.g.add((bootstrap.childHandler(new ChannelInitializer<Channel>() {
                protected void initChannel(Channel channel) throws Exception {
                    try {
                        channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                    } catch (ChannelException channelexception) {
                    }

                    final NetworkManager networkmanager = new NetworkManager();
    
                    channel.pipeline().addFirst(new io.netty.handler.flush.FlushConsolidationHandler()); // PandaSpigot
                    channel.pipeline()
                        .addLast("timeout", new ReadTimeoutHandler(30))
                        .addLast("legacy_query", new LegacyPingHandler(ServerConnection.this))
                        .addLast("splitter", new PacketSplitter())
                        .addLast("decoder", new PacketDecoder(EnumProtocolDirection.SERVERBOUND, networkmanager))
                        .addLast("prepender", new PacketPrepender())
                        .addLast("encoder", new PacketEncoder());

                    ServerConnection.this.h.add(networkmanager);

                    channel.pipeline().addLast("packet_handler", networkmanager);
                    networkmanager.a(new HandshakeListener(ServerConnection.this.f, networkmanager));
                }
            }).group((EventLoopGroup) lazyinitvar.c()).localAddress(inetaddress, i)).bind().syncUninterruptibly());
        }
    }

    public void b() {
        this.d = false;
        Iterator iterator = this.g.iterator();

        while (iterator.hasNext()) {
            ChannelFuture channelfuture = (ChannelFuture) iterator.next();

            try {
                channelfuture.channel().close().sync();
            } catch (InterruptedException interruptedexception) {
                Logger.error("Interrupted whilst closing channel");
            }
        }

    }

    public void c() {
        synchronized (this.h) {
            // Spigot Start
            // This prevents players from 'gaming' the server, and strategically relogging to increase their position in the tick order
            if ( org.spigotmc.SpigotConfig.playerShuffle > 0 && MinecraftServer.currentTick % org.spigotmc.SpigotConfig.playerShuffle == 0 )
            {
                Collections.shuffle( this.h );
            }
            // Spigot End
            final Iterator<NetworkManager> iterator = this.h.iterator();

            while (iterator.hasNext()) {
                final NetworkManager networkmanager = (NetworkManager) iterator.next();

                if (!networkmanager.h()) {
                    if (!networkmanager.g()) {
                        // Spigot Start
                        // Fix a race condition where a NetworkManager could be unregistered just before connection.
                        if (networkmanager.preparing) continue;
                        // Spigot End
                        iterator.remove();
                        networkmanager.l();
                    } else {
                        try {
                            networkmanager.a();
                        } catch (Exception exception) {
                            if (networkmanager.c()) {
                                CrashReport crashreport = CrashReport.a(exception, "Ticking memory connection");
                                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Ticking connection");

                                crashreportsystemdetails.a("Connection", new Callable<String>() {
                                    public String call() throws Exception {
                                        return networkmanager.toString();
                                    }
                                });
                                throw new ReportedException(crashreport);
                            }

                            Logger.warn("Failed to handle packet for " + networkmanager.getSocketAddress(), exception);
                            final ChatComponentText chatcomponenttext = new ChatComponentText("Internal server error");

                            networkmanager.a(new PacketPlayOutKickDisconnect(chatcomponenttext), new GenericFutureListener() {
                                public void operationComplete(Future future) throws Exception {
                                    networkmanager.close(chatcomponenttext);
                                }
                            }, new GenericFutureListener[0]);
                            networkmanager.k();
                        }
                    }
                }
            }

        }
    }

    public MinecraftServer d() {
        return this.f;
    }
}
