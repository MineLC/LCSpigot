package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutKickDisconnect implements Packet<PacketListenerPlayOut> {

    private IChatBaseComponent a;

    public PacketPlayOutKickDisconnect() {}

    public PacketPlayOutKickDisconnect(IChatBaseComponent ichatbasecomponent) {
        this.a = ichatbasecomponent;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.d();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
    @Override
    public int id() {
        return 64;
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }
    @Override
    public Packet<PacketListenerPlayOut> emptyCopy() {
        return new PacketPlayOutKickDisconnect();
    }
}
