package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayInHeldItemSlot implements Packet<PacketListenerPlayIn> {

    private int itemInHandIndex;

    public PacketPlayInHeldItemSlot() {}

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.itemInHandIndex = packetdataserializer.readShort();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeShort(this.itemInHandIndex);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public int a() {
        return this.itemInHandIndex;
    }
    @Override
    public int id() {
        return 9;
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }
}
