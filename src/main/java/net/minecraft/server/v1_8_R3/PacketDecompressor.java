package net.minecraft.server.v1_8_R3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import java.util.List;
import java.util.zip.Inflater;

public class PacketDecompressor extends ByteToMessageDecoder {

    private final Inflater a;
    private final PacketDataSerializer serializer = new PacketDataSerializer();
    private int b;

    public PacketDecompressor(int i) {
        this.b = i;
        this.a = new Inflater();
    }

    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        if (bytebuf.readableBytes() != 0) {
            serializer.setBuffer(bytebuf);
            int i = serializer.e();

            if (i == 0) {
                list.add(serializer.readBytes(serializer.readableBytes()));
            } else {
                if (i < this.b) {
                    throw new DecoderException("Badly compressed packet - size of " + i + " is below server threshold of " + this.b);
                }

                if (i > 2097152) {
                    throw new DecoderException("Badly compressed packet - size of " + i + " is larger than protocol maximum of " + 2097152);
                }

                final byte[] abyte = new byte[serializer.readableBytes()];

                serializer.readBytes(abyte);
                this.a.setInput(abyte);
                final byte[] abyte1 = new byte[i];

                this.a.inflate(abyte1);
                list.add(Unpooled.wrappedBuffer(abyte1));
                this.a.reset();
            }
            serializer.setBuffer(null);
        }
    }

    public void a(int i) {
        this.b = i;
    }
}
