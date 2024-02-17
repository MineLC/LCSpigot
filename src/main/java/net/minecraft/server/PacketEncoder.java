package net.minecraft.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.IOException;
import org.tinylog.Logger;

public class PacketEncoder extends MessageToByteEncoder<Packet> {

    private final EnumProtocolDirection c;

    public PacketEncoder(EnumProtocolDirection enumprotocoldirection) {
        this.c = enumprotocoldirection;
    }

    protected void encode(final ChannelHandlerContext channelhandlercontext, final Packet packet, final ByteBuf bytebuf) throws Exception {
        if (packet.id() == -1) {
            throw new IOException("Can\'t serialize unregistered packet");
        }

        final PacketDataSerializer packetdataserializer = new PacketDataSerializer(bytebuf);
        packetdataserializer.b(packet.id());

        try {
            packet.b(packetdataserializer);
         } catch (Throwable throwable) {
            Logger.error(throwable);
        }
    }
}