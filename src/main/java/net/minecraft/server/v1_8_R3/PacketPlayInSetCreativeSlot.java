package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayInSetCreativeSlot implements Packet<PacketListenerPlayIn> {

    private int slot;
    private ItemStack b;

    public PacketPlayInSetCreativeSlot() {}

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.slot = packetdataserializer.readShort();
        this.b = packetdataserializer.i();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeShort(this.slot);
        packetdataserializer.a(this.b);
    }

    public int a() {
        return this.slot;
    }

    public ItemStack getItemStack() {
        return this.b;
    }

    @Override
    public int id() {
        return 16;
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }

    @Override
    public Packet<PacketListenerPlayIn> emptyCopy() {
        return new PacketPlayInSetCreativeSlot();
    }
}
