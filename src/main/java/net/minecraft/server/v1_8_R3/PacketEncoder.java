package net.minecraft.server.v1_8_R3;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.IOException;
import org.tinylog.Logger;

public class PacketEncoder extends MessageToByteEncoder<Packet> {
    private final PacketDataSerializer serializer = new PacketDataSerializer();

    protected void encode(final ChannelHandlerContext channelhandlercontext, final Packet packet, final ByteBuf bytebuf) throws Exception {
        if (packet.id() == -1) {
            throw new IOException("Can\'t serialize unregistered packet");
        }

        serializer.setBuffer(bytebuf);
        serializer.b(packet.id());

        try {
            packet.b(serializer);
        } catch (Throwable throwable) {
            Logger.error(throwable);
        } finally {
            serializer.setBuffer(null);
        }
    }
}