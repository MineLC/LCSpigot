package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayInKeepAlive implements Packet<PacketListenerPlayIn> {

    private int a;

    public PacketPlayInKeepAlive() {}

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.e();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.b(this.a);
    }

    public int a() {
        return this.a;
    }

    @Override
    public int id() {
        return 0;
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }

    @Override
    public Packet<PacketListenerPlayIn> emptyCopy() {
        return new PacketPlayInKeepAlive();
    }
}
