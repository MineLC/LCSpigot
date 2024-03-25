package net.minecraft.server.v1_8_R3;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;

public final class PacketDecoder extends ByteToMessageDecoder {

    private final EnumProtocolDirection c;
    private final PacketDataSerializer serializer = new PacketDataSerializer();
    private final NetworkManager manager;

    public PacketDecoder(EnumProtocolDirection enumprotocoldirection, NetworkManager manager) {
        this.c = enumprotocoldirection;
        this.manager = manager;
    }

    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        if (bytebuf.readableBytes() == 0) {
            return;
        }
        serializer.setBuffer(bytebuf);

        final int i = serializer.e();
        final Packet<?> packet = manager.protocol.createEmptyPacket(this.c, i);
        if (packet == null) {
            serializer.setBuffer(null);
            throw new IOException("Bad packet id " + i);
        }
        packet.a(serializer);

        if (serializer.readableBytes() > 0) {
            final int bytes = serializer.readableBytes();
            serializer.setBuffer(null);
            throw new IOException("Packet " + manager.protocol.a() + "/" + i + " (" + packet.getClass().getSimpleName() + ") was larger than I expected, found " + bytes + " bytes extra whilst reading packet " + i);
        }
        list.add(packet);
        serializer.setBuffer(null);
    }
}