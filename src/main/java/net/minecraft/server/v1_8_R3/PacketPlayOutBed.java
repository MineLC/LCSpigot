package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutBed implements Packet<PacketListenerPlayOut> {

    private int a;
    private BlockPosition b;

    public PacketPlayOutBed() {}

    public PacketPlayOutBed(EntityHuman entityhuman, BlockPosition blockposition) {
        this.a = entityhuman.getId();
        this.b = blockposition;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.e();
        this.b = packetdataserializer.c();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.b(this.a);
        packetdataserializer.a(this.b);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    @Override
    public int id() {
        return 10;
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }
    @Override
    public Packet<PacketListenerPlayOut> emptyCopy() {
        return new PacketPlayOutBed();
    }
}
