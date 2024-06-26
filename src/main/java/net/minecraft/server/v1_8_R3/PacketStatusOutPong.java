package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketStatusOutPong implements Packet<PacketStatusOutListener> {

    private long a;

    public PacketStatusOutPong() {}

    public PacketStatusOutPong(long i) {
        this.a = i;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.readLong();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeLong(this.a);
    }

    public void a(PacketStatusOutListener packetstatusoutlistener) {
        packetstatusoutlistener.a(this);
    }
    @Override
    public int id() {
        return 1;
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.STATUS;
    }
    @Override
    public Packet<PacketStatusOutListener> emptyCopy() {
        return new PacketStatusOutPong();
    }
}
