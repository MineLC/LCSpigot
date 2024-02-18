package com.hpfxd.pandaspigot.network;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CorruptedFrameException;

public class VarIntUtil {
    private static final int[] VARINT_EXACT_BYTE_LENGTHS = new int[33];
    
    static {
        for (int i = 0; i <= 32; i++)
        VARINT_EXACT_BYTE_LENGTHS[i] = (int)Math.ceil((31.0D - (i - 1)) / 7.0D); 
        VARINT_EXACT_BYTE_LENGTHS[32] = 1;
    }
    
    public static int readVarInt(ByteBuf buf) {
        int read = readVarIntSafely(buf);
        if (read == Integer.MIN_VALUE)
            throw new CorruptedFrameException("Bad VarInt decoded"); 
        return read;
    }
    
    public static int readVarIntSafely(ByteBuf buf) {
        int i = 0;
        int maxRead = Math.min(5, buf.readableBytes());
        for (int j = 0; j < maxRead; j++) {
            int k = buf.readByte();
            i |= (k & 0x7F) << j * 7;
            if ((k & 0x80) != 128)
                return i; 
        } 
        return Integer.MIN_VALUE;
    }
    
    public static int varIntBytes(int value) {
        return VARINT_EXACT_BYTE_LENGTHS[Integer.numberOfLeadingZeros(value)];
    }
    
    public static void writeVarInt(ByteBuf buf, int value) {
        if ((value & 0xFFFFFF80) == 0) {
            buf.writeByte(value);
        } else if ((value & 0xFFFFC000) == 0) {
            int w = (value & 0x7F | 0x80) << 8 | value >>> 7;
            buf.writeShort(w);
        } else {
            writeVarIntFull(buf, value);
        } 
    }
    
    private static void writeVarIntFull(ByteBuf buf, int value) {
        if ((value & 0xFFFFFF80) == 0) {
            buf.writeByte(value);
        } else if ((value & 0xFFFFC000) == 0) {
            int w = (value & 0x7F | 0x80) << 8 | value >>> 7;
            buf.writeShort(w);
        } else if ((value & 0xFFE00000) == 0) {
            int w = (value & 0x7F | 0x80) << 16 | (value >>> 7 & 0x7F | 0x80) << 8 | value >>> 14;
            buf.writeMedium(w);
        } else if ((value & 0xF0000000) == 0) {
            int w = (value & 0x7F | 0x80) << 24 | (value >>> 7 & 0x7F | 0x80) << 16 | (value >>> 14 & 0x7F | 0x80) << 8 | value >>> 21;
            buf.writeInt(w);
        } else {
            int w = (value & 0x7F | 0x80) << 24 | (value >>> 7 & 0x7F | 0x80) << 16 | (value >>> 14 & 0x7F | 0x80) << 8 | value >>> 21 & 0x7F | 0x80;
            buf.writeInt(w);
            buf.writeByte(value >>> 28);
        } 
    }
    
    public static void write21BitVarInt(ByteBuf buf, int value) {
        int w = (value & 0x7F | 0x80) << 16 | (value >>> 7 & 0x7F | 0x80) << 8 | value >>> 14;
        buf.writeMedium(w);
    }
}
