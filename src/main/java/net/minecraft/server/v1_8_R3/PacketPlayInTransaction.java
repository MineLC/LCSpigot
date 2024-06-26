package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayInTransaction implements Packet<PacketListenerPlayIn> {

    private int a;
    private short b;
    private boolean c;

    public PacketPlayInTransaction() {}

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.readByte();
        this.b = packetdataserializer.readShort();
        this.c = packetdataserializer.readByte() != 0;
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeByte(this.a);
        packetdataserializer.writeShort(this.b);
        packetdataserializer.writeByte(this.c ? 1 : 0);
    }

    public int a() {
        return this.a;
    }

    public short b() {
        return this.b;
    }
    @Override
    public int id() {
        return 15;
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }
    @Override
    public Packet<PacketListenerPlayIn> emptyCopy() {
        return new PacketPlayInTransaction();
    }
}
