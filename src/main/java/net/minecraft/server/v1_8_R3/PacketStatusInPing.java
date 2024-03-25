package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketStatusInPing implements Packet<PacketStatusInListener> {

    private long a;

    public PacketStatusInPing() {}

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.readLong();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeLong(this.a);
    }

    public void a(PacketStatusInListener packetstatusinlistener) {
        packetstatusinlistener.a(this);
    }
    @Override
    public int id() {
        return 1;
    }
    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.STATUS;
    }
    public long a() {
        return this.a;
    }
    @Override
    public Packet<PacketStatusInListener> emptyCopy() {
        return new PacketStatusInPing();
    }
}
