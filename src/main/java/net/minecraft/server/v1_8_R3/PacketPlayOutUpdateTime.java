package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutUpdateTime implements Packet<PacketListenerPlayOut> {

    private long a;
    private long b;

    public PacketPlayOutUpdateTime() {}

    public PacketPlayOutUpdateTime(long i, long j, boolean flag) {
        this.a = i;
        this.b = j;
        if (!flag) {
            this.b = -this.b;
            if (this.b == 0L) {
                this.b = -1L;
            }
        }

    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.readLong();
        this.b = packetdataserializer.readLong();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeLong(this.a);
        packetdataserializer.writeLong(this.b);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    @Override
    public int id() {
        return 3;
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }
    @Override
    public Packet<PacketListenerPlayOut> emptyCopy() {
        return new PacketPlayOutUpdateTime();
    }
}
