package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutHeldItemSlot implements Packet<PacketListenerPlayOut> {

    private int a;

    public PacketPlayOutHeldItemSlot() {}

    public PacketPlayOutHeldItemSlot(int i) {
        this.a = i;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.readByte();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeByte(this.a);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    @Override
    public int id() {
        return 9;
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }
    @Override
    public Packet<PacketListenerPlayOut> emptyCopy() {
        return new PacketPlayOutHeldItemSlot();
    }
}
