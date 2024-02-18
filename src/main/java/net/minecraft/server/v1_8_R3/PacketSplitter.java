package net.minecraft.server.v1_8_R3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;

import com.hpfxd.pandaspigot.network.VarIntByteDecoder;

public class PacketSplitter extends ByteToMessageDecoder {

    public PacketSplitter() {}

    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        if (!channelhandlercontext.channel().isActive()) {
            bytebuf.clear();
            return;
        } 
        VarIntByteDecoder reader = new VarIntByteDecoder();
        int varIntEnd = bytebuf.forEachByte(reader);
        if (varIntEnd == -1) {
            if (reader.getResult() == VarIntByteDecoder.DecodeResult.RUN_OF_ZEROES)
                bytebuf.clear(); 
            return;
        }
    
        switch (reader.getResult()) {
            case RUN_OF_ZEROES:
                bytebuf.readerIndex(varIntEnd);
                break;
            case SUCCESS:
                int readVarint = reader.getReadVarint();
                int bytesRead = reader.getBytesRead();
                if (readVarint < 0) {
                    bytebuf.clear();
                    throw new CorruptedFrameException("Bad packet length");
                } 
                if (readVarint == 0) {
                    bytebuf.readerIndex(varIntEnd + 1);
                } else {
                    int minimumRead = bytesRead + readVarint;
                    if (bytebuf.isReadable(minimumRead)) {
                        list.add(bytebuf.retainedSlice(varIntEnd + 1, readVarint));
                        bytebuf.skipBytes(minimumRead);
                    }
                }
                break;
            case TOO_BIG:
                bytebuf.clear();
                throw new CorruptedFrameException("VarInt too big");
            default:
                break;
        }
    }
}
