package net.minecraft.server.v1_8_R3;

import java.io.IOException;
import java.security.PublicKey;

public class PacketLoginOutEncryptionBegin implements Packet<PacketLoginOutListener> {

    private String a;
    private PublicKey b;
    private byte[] c;

    public PacketLoginOutEncryptionBegin() {}

    public PacketLoginOutEncryptionBegin(String s, PublicKey publickey, byte[] abyte) {
        this.a = s;
        this.b = publickey;
        this.c = abyte;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.c(20);
        this.b = MinecraftEncryption.a(packetdataserializer.a());
        this.c = packetdataserializer.a();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a);
        packetdataserializer.a(this.b.getEncoded());
        packetdataserializer.a(this.c);
    }

    public void a(PacketLoginOutListener packetloginoutlistener) {
        packetloginoutlistener.a(this);
    }
    @Override
    public int id() {
        return 1;
    }
    
    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.LOGIN;
    }

    @Override
    public Packet<PacketLoginOutListener> emptyCopy() {
        return new PacketLoginOutEncryptionBegin();
    }
}
