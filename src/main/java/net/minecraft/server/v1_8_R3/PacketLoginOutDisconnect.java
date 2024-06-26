package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketLoginOutDisconnect implements Packet<PacketLoginOutListener> {

    private IChatBaseComponent a;

    public PacketLoginOutDisconnect() {}

    public PacketLoginOutDisconnect(IChatBaseComponent ichatbasecomponent) {
        this.a = ichatbasecomponent;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.d();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a);
    }

    public void a(PacketLoginOutListener packetloginoutlistener) {
        packetloginoutlistener.a(this);
    }

    @Override
    public int id() {
        return 0;
    }
    
    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.LOGIN;
    }

    @Override
    public Packet<PacketLoginOutListener> emptyCopy() {
        return new PacketLoginOutDisconnect();
    }
}
