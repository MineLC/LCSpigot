package net.minecraft.server.v1_8_R3;

import java.io.IOException;
import java.security.PrivateKey;
import javax.crypto.SecretKey;

public class PacketLoginInEncryptionBegin implements Packet<PacketLoginInListener> {

    private byte[] a = new byte[0];
    private byte[] b = new byte[0];

    public PacketLoginInEncryptionBegin() {}

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.a();
        this.b = packetdataserializer.a();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a);
        packetdataserializer.a(this.b);
    }

    public void a(PacketLoginInListener packetlogininlistener) {
        packetlogininlistener.a(this);
    }

    public SecretKey a(PrivateKey privatekey) {
        return MinecraftEncryption.a(privatekey, this.a);
    }

    public byte[] b(PrivateKey privatekey) {
        return privatekey == null ? this.b : MinecraftEncryption.b(privatekey, this.b);
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
    public Packet<PacketLoginInListener> emptyCopy() {
        return new PacketLoginInEncryptionBegin();
    }
}
