package net.minecraft.server.v1_8_R3;

import com.hpfxd.pandaspigot.network.VarIntUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketPrepender extends MessageToByteEncoder<ByteBuf> {

    public PacketPrepender() {}

    protected void a(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, ByteBuf bytebuf1) throws Exception {
        VarIntUtil.writeVarInt(bytebuf1, bytebuf.readableBytes());
        bytebuf1.writeBytes(bytebuf);
    }

    protected void encode(ChannelHandlerContext channelhandlercontext, ByteBuf object, ByteBuf bytebuf) throws Exception {
        this.a(channelhandlercontext, (ByteBuf) object, bytebuf);
    }

    protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, ByteBuf msg, boolean preferDirect) throws Exception {
        int anticipatedRequiredCapacity = VarIntUtil.varIntBytes(msg.readableBytes()) + msg.readableBytes();
        return ctx.alloc().directBuffer(anticipatedRequiredCapacity);
    }
}
