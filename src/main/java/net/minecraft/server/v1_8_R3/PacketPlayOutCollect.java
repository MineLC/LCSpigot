package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutCollect implements Packet<PacketListenerPlayOut> {

    private int a;
    private int b;

    public PacketPlayOutCollect() {}

    public PacketPlayOutCollect(int i, int j) {
        this.a = i;
        this.b = j;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.e();
        this.b = packetdataserializer.e();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.b(this.a);
        packetdataserializer.b(this.b);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    @Override
    public int id() {
        return 13;
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }
    @Override
    public Packet<PacketListenerPlayOut> emptyCopy() {
        return new PacketPlayOutCollect();
    }
}
