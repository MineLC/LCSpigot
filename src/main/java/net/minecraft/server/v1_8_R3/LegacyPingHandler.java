package net.minecraft.server.v1_8_R3;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.net.InetSocketAddress;
import org.tinylog.Logger;

public class LegacyPingHandler extends ChannelInboundHandlerAdapter {

    private ServerConnection b;

    public LegacyPingHandler(ServerConnection serverconnection) {
        this.b = serverconnection;
    }

    public void channelRead(ChannelHandlerContext channelhandlercontext, Object object) throws Exception {
        ByteBuf bytebuf = (ByteBuf) object;

        bytebuf.markReaderIndex();
        boolean flag = true;

        try {
            if (bytebuf.readUnsignedByte() == 254) {
                InetSocketAddress inetsocketaddress = (InetSocketAddress) channelhandlercontext.channel().remoteAddress();
                MinecraftServer minecraftserver = this.b.d();
                int i = bytebuf.readableBytes();
                String s;

                switch (i) {
                case 0:
                    Logger.debug("Ping: (<1.3.x) from {}:{}", new Object[] { inetsocketaddress.getAddress(), Integer.valueOf(inetsocketaddress.getPort())});
                    s = String.format("%s\u00a7%d\u00a7%d", new Object[] { minecraftserver.getMotd(), Integer.valueOf(minecraftserver.I()), Integer.valueOf(minecraftserver.J())});
                    this.a(channelhandlercontext, this.a(s));
                    break;

                case 1:
                    if (bytebuf.readUnsignedByte() != 1) {
                        return;
                    }

                    Logger.debug("Ping: (1.4-1.5.x) from {}:{}", new Object[] { inetsocketaddress.getAddress(), Integer.valueOf(inetsocketaddress.getPort())});
                    s = String.format("\u00a71\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", new Object[] { Integer.valueOf(127), minecraftserver.getVersion(), minecraftserver.getMotd(), Integer.valueOf(minecraftserver.I()), Integer.valueOf(minecraftserver.J())});
                    this.a(channelhandlercontext, this.a(s));
                    break;

                default:
                    boolean flag1 = bytebuf.readUnsignedByte() == 1;

                    flag1 &= bytebuf.readUnsignedByte() == 250;
                    flag1 &= "MC|PingHost".equals(new String(bytebuf.readBytes(bytebuf.readShort() * 2).array(), Charsets.UTF_16BE));
                    int j = bytebuf.readUnsignedShort();

                    flag1 &= bytebuf.readUnsignedByte() >= 73;
                    flag1 &= 3 + bytebuf.readBytes(bytebuf.readShort() * 2).array().length + 4 == j;
                    flag1 &= bytebuf.readInt() <= '\uffff';
                    flag1 &= bytebuf.readableBytes() == 0;
                    if (!flag1) {
                        return;
                    }

                    Logger.debug("Ping: (1.6) from {}:{}", new Object[] { inetsocketaddress.getAddress(), Integer.valueOf(inetsocketaddress.getPort())});
                    String s1 = String.format("\u00a71\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", new Object[] { Integer.valueOf(127), minecraftserver.getVersion(), minecraftserver.getMotd(), Integer.valueOf(minecraftserver.I()), Integer.valueOf(minecraftserver.J())});
                    ByteBuf bytebuf1 = this.a(s1);

                    try {
                        this.a(channelhandlercontext, bytebuf1);
                    } finally {
                        bytebuf1.release();
                    }
                }

                bytebuf.release();
                flag = false;
                return;
            }
        } catch (RuntimeException runtimeexception) {
            return;
        } finally {
            if (flag) {
                bytebuf.resetReaderIndex();
                channelhandlercontext.channel().pipeline().remove("legacy_query");
                channelhandlercontext.fireChannelRead(object);
            }

        }

    }

    private void a(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf) {
        channelhandlercontext.pipeline().firstContext().writeAndFlush(bytebuf).addListener(ChannelFutureListener.CLOSE);
    }

    private ByteBuf a(String s) {
        ByteBuf bytebuf = Unpooled.buffer();

        bytebuf.writeByte(255);
        char[] achar = s.toCharArray();

        bytebuf.writeShort(achar.length);
        char[] achar1 = achar;
        int i = achar.length;

        for (int j = 0; j < i; ++j) {
            char c0 = achar1[j];

            bytebuf.writeChar(c0);
        }

        return bytebuf;
    }
}
