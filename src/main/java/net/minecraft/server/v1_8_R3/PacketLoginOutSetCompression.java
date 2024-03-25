package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketLoginOutSetCompression implements Packet<PacketLoginOutListener> {

    private int a;

    public PacketLoginOutSetCompression() {}

    public PacketLoginOutSetCompression(int i) {
        this.a = i;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.e();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.b(this.a);
    }

    public void a(PacketLoginOutListener packetloginoutlistener) {
        packetloginoutlistener.a(this);
    }
    @Override
    public int id() {
        return 3;
    }
    
    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.LOGIN;
    }

    @Override
    public Packet<PacketLoginOutListener> emptyCopy() {
        return new PacketLoginOutSetCompression();
    }
}
