package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutEntityHeadRotation implements Packet<PacketListenerPlayOut> {

    private int a;
    private byte b;

    public PacketPlayOutEntityHeadRotation() {}

    public PacketPlayOutEntityHeadRotation(Entity entity, byte b0) {
        this.a = entity.getId();
        this.b = b0;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.e();
        this.b = packetdataserializer.readByte();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.b(this.a);
        packetdataserializer.writeByte(this.b);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    @Override
    public int id() {
        return 25;
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }
    @Override
    public Packet<PacketListenerPlayOut> emptyCopy() {
        return new PacketPlayOutEntityHeadRotation();
    }
}
