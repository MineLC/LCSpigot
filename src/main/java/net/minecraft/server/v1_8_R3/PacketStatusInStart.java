package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketStatusInStart implements Packet<PacketStatusInListener> {

    public PacketStatusInStart() {}

    public void a(PacketDataSerializer packetdataserializer) throws IOException {}

    public void b(PacketDataSerializer packetdataserializer) throws IOException {}

    public void a(PacketStatusInListener packetstatusinlistener) {
        packetstatusinlistener.a(this);
    }
    @Override
    public int id() {
        return 0;
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.STATUS;
    }
    @Override
    public Packet<PacketStatusInListener> emptyCopy() {
        return new PacketStatusInStart();
    }
}
