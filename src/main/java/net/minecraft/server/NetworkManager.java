package net.minecraft.server;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.crypto.SecretKey;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.tinylog.Logger;

public class NetworkManager extends SimpleChannelInboundHandler<Packet> {

    // PandaSpigot start - allow controlled flushing
    volatile boolean canFlush = true;
    private final java.util.concurrent.atomic.AtomicInteger packetWrites = new java.util.concurrent.atomic.AtomicInteger();
    private int flushPacketsStart;
    private final Object flushLock = new Object();
    
    public void disableAutomaticFlush() {
        synchronized (this.flushLock) {
            this.flushPacketsStart = this.packetWrites.get(); // must be volatile and before canFlush = false
            this.canFlush = false;
        }
    }
    
    public void enableAutomaticFlush() {
        synchronized (this.flushLock) {
            this.canFlush = true;
            if (this.packetWrites.get() != this.flushPacketsStart) { // must be after canFlush = true
                this.flush(); // only make the flush call if we need to
            }
        }
    }
    
    private void flush() {
        if (this.channel.eventLoop().inEventLoop()) {
            this.channel.flush();
        } else {
            this.channel.eventLoop().execute(() -> {
                this.channel.flush();
            });
        }
    }
    // PandaSpigot end

    public static final AttributeKey<EnumProtocol> c = AttributeKey.valueOf("protocol");
    public static final LazyInitVar<NioEventLoopGroup> d = new LazyInitVar<NioEventLoopGroup>() {
        protected NioEventLoopGroup a() {
            return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Client IO #%d").setDaemon(true).build());
        }

        protected NioEventLoopGroup init() {
            return this.a();
        }
    };
    public static final LazyInitVar<EpollEventLoopGroup> e = new LazyInitVar<EpollEventLoopGroup>() {
        protected EpollEventLoopGroup a() {
            return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());
        }

        protected EpollEventLoopGroup init() {
            return this.a();
        }
    };
    public static final LazyInitVar<LocalEventLoopGroup> f = new LazyInitVar<LocalEventLoopGroup>() {
        protected LocalEventLoopGroup a() {
            return new LocalEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Local Client IO #%d").setDaemon(true).build());
        }

        protected LocalEventLoopGroup init() {
            return this.a();
        }
    };
    private final EnumProtocolDirection h;
    private final Queue<NetworkManager.QueuedPacket> i = Queues.newConcurrentLinkedQueue();
    public Channel channel;  // CraftBukkit - public
    // Spigot Start
    public SocketAddress l;
    public java.util.UUID spoofedUUID;
    public com.mojang.authlib.properties.Property[] spoofedProfile;
    public boolean preparing = true;
    // Spigot End
    private PacketListener m;
    private IChatBaseComponent n;
    private boolean o;
    private boolean p;

    // PandaSpigot start - Optimize network
    public boolean isPending = true;
    public boolean queueImmunity = false;
    public EnumProtocol protocol;
    // PandaSpigot end

    public NetworkManager(EnumProtocolDirection enumprotocoldirection) {
        this.h = enumprotocoldirection;
    }

    public void channelActive(ChannelHandlerContext channelhandlercontext) throws Exception {
        super.channelActive(channelhandlercontext);
        this.channel = channelhandlercontext.channel();
        this.l = this.channel.remoteAddress();
        // Spigot Start
        this.preparing = false;
        // Spigot End

        try {
            this.a(EnumProtocol.HANDSHAKING);
        } catch (Throwable throwable) {
            Logger.error(throwable);
        }

    }

    public void a(EnumProtocol enumprotocol) {
        this.protocol = enumprotocol;
        this.channel.attr(NetworkManager.c).set(enumprotocol);
        this.channel.config().setAutoRead(true);
    }

    public void channelInactive(ChannelHandlerContext channelhandlercontext) throws Exception {
        this.close(new ChatMessage("disconnect.endOfStream", new Object[0]));
    }

    public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) throws Exception {
        ChatMessage chatmessage;

        if (throwable instanceof TimeoutException) {
            chatmessage = new ChatMessage("disconnect.timeout", new Object[0]);
        } else {
            chatmessage = new ChatMessage("disconnect.genericReason", new Object[] { "Internal Exception: " + throwable});
        }

        this.close(chatmessage);
        if (MinecraftServer.getServer().isDebugging()) throwable.printStackTrace(); // Spigot
    }

    protected void a(ChannelHandlerContext channelhandlercontext, Packet packet) throws Exception {
        if (this.channel.isOpen()) {
            try {
                packet.a(this.m);
            } catch (CancelledPacketHandleException cancelledpackethandleexception) {
                ;
            }
        }

    }

    public void a(PacketListener packetlistener) {
        Validate.notNull(packetlistener, "packetListener", new Object[0]);
        this.m = packetlistener;
    }

    public void handle(Packet packet) {
        this.a(packet, null, (GenericFutureListener<? extends Future<? super Void>>) null); // PandaSpigot
    }

    public void a(Packet packet, GenericFutureListener<? extends Future<? super Void>> genericfuturelistener, GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener) {
        // PandaSpigot start - handle oversized packets better
        GenericFutureListener<? extends Future<? super Void>>[] listeners = null;
        if (genericfuturelistener != null || agenericfuturelistener != null) { // cannot call ArrayUtils.add with both null arguments
            listeners = ArrayUtils.add(agenericfuturelistener, 0, genericfuturelistener);
        }
        boolean connected = this.isConnected();
        if (!connected && !preparing) {
            return; // Do nothing
        }
        packet.onPacketDispatch(getPlayer());
        if (connected && (InnerUtil.canSendImmediate(this, packet) || (
            MinecraftServer.getServer().isMainThread() && packet.isReady() && this.i.isEmpty() &&
                        (packet.getExtraPackets() == null || packet.getExtraPackets().isEmpty())
        ))) {
            this.dispatchPacket(packet, listeners);
            return;
        }
        // write the packets to the queue, then flush - antixray hooks there already
        java.util.List<Packet> extraPackets = InnerUtil.buildExtraPackets(packet);
        boolean hasExtraPackets = extraPackets != null && !extraPackets.isEmpty();
        if (!hasExtraPackets) {
            this.i.add(new NetworkManager.QueuedPacket(packet, listeners));
        } else {
            java.util.List<NetworkManager.QueuedPacket> packets = new java.util.ArrayList<>(1 + extraPackets.size());
            packets.add(new NetworkManager.QueuedPacket(packet, (GenericFutureListener<? extends Future<? super Void>>) null)); // delay the future listener until the end of the extra packets
            for (int i = 0, len = extraPackets.size(); i < len;) {
                Packet extra = extraPackets.get(i);
                boolean end = ++i == len;
                packets.add(new NetworkManager.QueuedPacket(extra, end ? listeners : null)); // append listener to the end
            }
            this.i.addAll(packets);
        }
        this.sendPacketQueue();
    }

    private void dispatchPacket(Packet<?> packet, final GenericFutureListener<? extends Future<? super Void>>[] agenericfuturelistener) { this.a(packet, agenericfuturelistener); } // PandaSpigot - OBFHELPER

    private void a(final Packet packet, final GenericFutureListener<? extends Future<? super Void>>[] agenericfuturelistener) {
        // PandaSpigot start - add flush parameter
        this.writePacket(packet, agenericfuturelistener, Boolean.TRUE);
    }

    private void writePacket(Packet packet, final GenericFutureListener<? extends Future<? super Void>>[] agenericfuturelistener, Boolean flushConditional) {
        this.packetWrites.getAndIncrement(); // must be before using canFlush
        boolean effectiveFlush = flushConditional == null ? this.canFlush : flushConditional;
        final boolean flush = effectiveFlush || packet instanceof PacketPlayOutKeepAlive || packet instanceof PacketPlayOutKickDisconnect; // no delay for certain packets
        // PandaSpigot end - add flush parameter
        final EnumProtocol enumprotocol = EnumProtocol.a(packet);
        final EnumProtocol enumprotocol1 = (EnumProtocol) this.channel.attr(NetworkManager.c).get();

        if (enumprotocol1 != enumprotocol) {
            Logger.debug("Disabled auto read");
            this.channel.config().setAutoRead(false);
        }
        EntityPlayer player = getPlayer();
        if (this.channel.eventLoop().inEventLoop()) {
            if (enumprotocol != enumprotocol1) {
                this.a(enumprotocol);
            }
            // PandaSpigot start
            if (!isConnected()) {
                packet.onPacketDispatchFinish(player, null);
                return;
            }
            try {
                // PandaSpigot end
                ChannelFuture channelfuture = (flush) ? this.channel.writeAndFlush(packet) : this.channel.write(packet); // PandaSpigot - add flush parameter
    
                if (agenericfuturelistener != null) {
                    channelfuture.addListeners(agenericfuturelistener);
                }
                // PandaSpigot start
                if (packet.hasFinishListener()) {
                    channelfuture.addListener((ChannelFutureListener) channelFuture -> packet.onPacketDispatchFinish(player, channelFuture));
                }
                // PandaSpigot end
                channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                // PandaSpigot start
            } catch (Exception e) {
                Logger.error("NetworkException: " + player, e);
                close(new ChatMessage("disconnect.genericReason", "Internal Exception: " + e.getMessage()));;
                packet.onPacketDispatchFinish(player, null);
            }
            // PandaSpigot end
        } else {
            Runnable command = () -> {
                if (enumprotocol != enumprotocol1) {
                    NetworkManager.this.a(enumprotocol);
                }
                // PandaSpigot start
                if (!isConnected()) {
                    packet.onPacketDispatchFinish(player, null);
                    return;
                }
                try {
                    // PandaSpigot end
                    ChannelFuture channelfuture = (flush) ? NetworkManager.this.channel.writeAndFlush(packet) : NetworkManager.this.channel.write(packet); // PandaSpigot - add flush parameter

                    if (agenericfuturelistener != null) {
                        channelfuture.addListeners(agenericfuturelistener);
                    }

                    // PandaSpigot start
                    if (packet.hasFinishListener()) {
                        channelfuture.addListener((ChannelFutureListener) channelFuture -> packet.onPacketDispatchFinish(player, channelFuture));
                    }
                    // PandaSpigot end
                    channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                    // PandaSpigot start
                } catch (Exception e) {
                    Logger.error("NetworkException: " + player, e);
                    close(new ChatMessage("disconnect.genericReason", "Internal Exception: " + e.getMessage()));;
                    packet.onPacketDispatchFinish(player, null);
                }
                // PandaSpigot end
            };
            if (!flush) {
                io.netty.util.concurrent.AbstractEventExecutor.LazyRunnable run = command::run;
                this.channel.eventLoop().execute(run);
            } else {
                this.channel.eventLoop().execute(command);
            }
            // PandaSpigot end
        }
    }

    private boolean sendPacketQueue() { return this.m(); }

    private boolean m() {
        if (!isConnected()) {
            return true;
        }
        if (MinecraftServer.getServer().isMainThread()) {
            return processQueue();
        } else if (isPending) {
            // Should only happen during login/status stages
            synchronized (this.i) {
                return this.processQueue();
            }
        }
        return false;
    }

    private boolean processQueue() {
        if (this.i.isEmpty()) return true;
        // If we are on main, we are safe here in that nothing else should be processing queue off main anymore
        // But if we are not on main due to login/status, the parent is synchronized on packetQueue
        java.util.Iterator<QueuedPacket> iterator = this.i.iterator();
        while (iterator.hasNext()) {
            NetworkManager.QueuedPacket queued = iterator.next(); // poll -> peek
            // Fix NPE (Spigot bug caused by handleDisconnection())
            if (false && queued == null) {
                return true;
                     }
         
            Packet<?> packet = queued.getPacket();
            if (!packet.isReady()) {
                return false;
            } else {
                iterator.remove();
                this.dispatchPacket(packet, queued.getGenericFutureListeners());
            }
        }
        return true;
    }
    // PandaSpigot start
    public void clearPacketQueue() {
        EntityPlayer player = getPlayer();
        i.forEach(queuedPacket -> {
            Packet<?> packet = queuedPacket.getPacket();
            if (packet.hasFinishListener()) {
                packet.onPacketDispatchFinish(player, null);
            }
        });
        i.clear();
    } // PandaSpigot end

    public void a() {
        this.m();
        if (this.m instanceof IUpdatePlayerListBox) {
            ((IUpdatePlayerListBox) this.m).c();
        }

        this.channel.flush();
    }

    public SocketAddress getSocketAddress() {
        return this.l;
    }

    public void close(IChatBaseComponent ichatbasecomponent) {
        // Spigot Start
        this.preparing = false;
        clearPacketQueue();
        // Spigot End
        if (this.channel.isOpen()) {
            this.channel.close(); // We can't wait as this may be called from an event loop.
            this.n = ichatbasecomponent;
        }

    }
    public boolean isConnected() { return this.g(); }
    public boolean c() {
        return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
    }

    public void a(SecretKey secretkey) {
        this.o = true;
        this.channel.pipeline().addBefore("splitter", "decrypt", new PacketDecrypter(MinecraftEncryption.a(2, secretkey)));
        this.channel.pipeline().addBefore("prepender", "encrypt", new PacketEncrypter(MinecraftEncryption.a(1, secretkey)));
    }

    public boolean g() {
        return this.channel != null && this.channel.isOpen();
    }

    public boolean h() {
        return this.channel == null;
    }

    public PacketListener getPacketListener() {
        return this.m;
    }

    public IChatBaseComponent j() {
        return this.n;
    }

    public void k() {
        this.channel.config().setAutoRead(false);
    }

    public void a(int i) {
        if (i >= 0) {
            if (this.channel.pipeline().get("decompress") instanceof PacketDecompressor) {
                ((PacketDecompressor) this.channel.pipeline().get("decompress")).a(i);
            } else {
                this.channel.pipeline().addBefore("decoder", "decompress", new PacketDecompressor(i));
            }

            if (this.channel.pipeline().get("compress") instanceof PacketCompressor) {
                ((PacketCompressor) this.channel.pipeline().get("decompress")).a(i);
            } else {
                this.channel.pipeline().addBefore("encoder", "compress", new PacketCompressor(i));
            }
        } else {
            if (this.channel.pipeline().get("decompress") instanceof PacketDecompressor) {
                this.channel.pipeline().remove("decompress");
            }

            if (this.channel.pipeline().get("compress") instanceof PacketCompressor) {
                this.channel.pipeline().remove("compress");
            }
        }

    }

    public void l() {
        if (this.channel != null && !this.channel.isOpen()) {
            if (!this.p) {
                this.p = true;
                if (this.j() != null) {
                    this.getPacketListener().a(this.j());
                } else if (this.getPacketListener() != null) {
                    this.getPacketListener().a(new ChatComponentText("Disconnected"));
                }
                clearPacketQueue(); // PandaSpigot
            } else {
                Logger.warn("handleDisconnection() called twice"); // PandaSpigot - Do not log useless message
            }

        }
    }

    protected void channelRead0(ChannelHandlerContext channelhandlercontext, Packet object) throws Exception { // CraftBukkit - fix decompile error
        this.a(channelhandlercontext, (Packet) object);
    }

    static class QueuedPacket {
        private final Packet a; 
        private final Packet<?> getPacket() { return this.a; } // PandaSpigot - OBFHELPER
        private final GenericFutureListener<? extends Future<? super Void>>[] b; 
        private final GenericFutureListener<? extends Future<? super Void>>[] getGenericFutureListeners() { return this.b; } // PandaSpigot - OBFHELPER
        

        public QueuedPacket(Packet packet, GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener) {
            this.a = packet;
            this.b = agenericfuturelistener;
        }
    }

    // Spigot Start
    public SocketAddress getRawAddress(){
        return this.channel.remoteAddress();
    }
    // Spigot End

    
    // PandaSpigot start
    public EntityPlayer getPlayer() {
        if (this.m instanceof PlayerConnection) {
            return ((PlayerConnection) this.m).player;
        }
        return null;
    }
    private static class InnerUtil { // Attempt to hide these methods from ProtocolLib so it doesn't accidently pick them up.
        private static java.util.List<Packet> buildExtraPackets(Packet packet) {
            java.util.List<Packet> extra = packet.getExtraPackets();
            if (extra == null || extra.isEmpty()) {
            return null;
                }
            java.util.List<Packet> ret = new java.util.ArrayList<>(1 + extra.size());
            buildExtraPackets0(extra, ret);
            return ret;
            }
    
        private static void buildExtraPackets0(java.util.List<Packet> extraPackets, java.util.List<Packet> into) {
            for (Packet extra : extraPackets) {
                into.add(extra);
                java.util.List<Packet> extraExtra = extra.getExtraPackets();
                if (extraExtra != null && !extraExtra.isEmpty()) {
                    buildExtraPackets0(extraExtra, into);
                }
            }
        }
        private static boolean canSendImmediate(NetworkManager networkManager, Packet<?> packet) {
            return networkManager.isPending || networkManager.protocol != EnumProtocol.PLAY ||
                    packet instanceof PacketPlayOutKeepAlive ||
                    packet instanceof PacketPlayOutChat ||
                    packet instanceof PacketPlayOutTabComplete ||
                    packet instanceof PacketPlayOutTitle;
        }
    }
    // PandaSpigot end
}
