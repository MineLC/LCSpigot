package com.hpfxd.pandaspigot.network;

import io.netty.util.ByteProcessor;

public class VarIntByteDecoder implements ByteProcessor {
    private int readVarint;
    
    private int bytesRead;
    
    private DecodeResult result = DecodeResult.TOO_SHORT;
    
    public boolean process(byte k) {
        if (k == 0 && this.bytesRead == 0) {
            this.result = DecodeResult.RUN_OF_ZEROES;
            return true;
        } 
        if (this.result == DecodeResult.RUN_OF_ZEROES)
            return false; 
        this.readVarint |= (k & Byte.MAX_VALUE) << this.bytesRead++ * 7;
        if (this.bytesRead > 3) {
            this.result = DecodeResult.TOO_BIG;
            return false;
        } 
        if ((k & 0x80) != 128) {
            this.result = DecodeResult.SUCCESS;
            return false;
        } 
        return true;
    }
    
    public int getReadVarint() {
        return this.readVarint;
    }
    
    public int getBytesRead() {
        return this.bytesRead;
    }
    
    public DecodeResult getResult() {
        return this.result;
    }
    
    public enum DecodeResult {
        SUCCESS, TOO_SHORT, TOO_BIG, RUN_OF_ZEROES;
    }
}
