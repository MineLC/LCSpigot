package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutBlockBreakAnimation implements Packet<PacketListenerPlayOut> {

    private int a;
    private BlockPosition b;
    private int c;

    public PacketPlayOutBlockBreakAnimation() {}

    public PacketPlayOutBlockBreakAnimation(int i, BlockPosition blockposition, int j) {
        this.a = i;
        this.b = blockposition;
        this.c = j;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.e();
        this.b = packetdataserializer.c();
        this.c = packetdataserializer.readUnsignedByte();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.b(this.a);
        packetdataserializer.a(this.b);
        packetdataserializer.writeByte(this.c);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    @Override
    public int id() {
        return 37;
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }
    @Override
    public Packet<PacketListenerPlayOut> emptyCopy() {
        return new PacketPlayOutBlockBreakAnimation();
    }
}
