package net.minecraft.server.v1_8_R3;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.zip.Deflater;

public class PacketCompressor extends MessageToByteEncoder<ByteBuf> {

    private final byte[] a = new byte[8192];
    private final Deflater b;
    private final PacketDataSerializer serializer = new PacketDataSerializer();
    private int c;

    public PacketCompressor(int i) {
        this.c = i;
        this.b = new Deflater();
    }

    protected void a(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, ByteBuf bytebuf1) throws Exception {
        int i = bytebuf.readableBytes();
        serializer.setBuffer(bytebuf1);

        if (i < this.c) {
            serializer.b(0);
            serializer.writeBytes(bytebuf);
        } else {
            byte[] abyte = new byte[i];

            bytebuf.readBytes(abyte);
            serializer.b(abyte.length);
            this.b.setInput(abyte, 0, i);
            this.b.finish();

            while (!this.b.finished()) {
                int j = this.b.deflate(this.a);

                serializer.writeBytes(this.a, 0, j);
            }

            this.b.reset();
        }
        serializer.setBuffer(null);
    }

    public void a(int i) {
        this.c = i;
    }

    protected void encode(ChannelHandlerContext channelhandlercontext, ByteBuf object, ByteBuf bytebuf) throws Exception {
        this.a(channelhandlercontext, (ByteBuf) object, bytebuf);
    }
}
