package net.minecraft.server.v1_8_R3;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

    private final EnumProtocolDirection c;

    public PacketDecoder(EnumProtocolDirection enumprotocoldirection) {
        this.c = enumprotocoldirection;
    }

    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        if (bytebuf.readableBytes() == 0) {
            return;
        }
        final PacketDataSerializer packetdataserializer = new PacketDataSerializer(bytebuf);
        final int i = packetdataserializer.e();
        final Packet<?> packet = ((EnumProtocol) channelhandlercontext.channel().attr(NetworkManager.c).get()).a(this.c, i);

        if (packet == null) {
            throw new IOException("Bad packet id " + i);
        }
        packet.a(packetdataserializer);
        if (packetdataserializer.readableBytes() > 0) {
            throw new IOException("Packet " + ((EnumProtocol) channelhandlercontext.channel().attr(NetworkManager.c).get()).a() + "/" + i + " (" + packet.getClass().getSimpleName() + ") was larger than I expected, found " + packetdataserializer.readableBytes() + " bytes extra whilst reading packet " + i);
        }
        list.add(packet);
    }
}
