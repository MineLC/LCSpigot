package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayInEnchantItem implements Packet<PacketListenerPlayIn> {

    private int a;
    private int b;

    public PacketPlayInEnchantItem() {}

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.readByte();
        this.b = packetdataserializer.readByte();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeByte(this.a);
        packetdataserializer.writeByte(this.b);
    }

    public int a() {
        return this.a;
    }

    public int b() {
        return this.b;
    }
    @Override
    public int id() {
        return 17;
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }

    @Override
    public Packet<PacketListenerPlayIn> emptyCopy() {
        return new PacketPlayInEnchantItem();
    }
}
