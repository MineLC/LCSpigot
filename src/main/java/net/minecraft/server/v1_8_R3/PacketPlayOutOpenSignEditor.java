package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutOpenSignEditor implements Packet<PacketListenerPlayOut> {

    private BlockPosition a;

    public PacketPlayOutOpenSignEditor() {}

    public PacketPlayOutOpenSignEditor(BlockPosition blockposition) {
        this.a = blockposition;
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.c();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a);
    }

    @Override
    public int id() {
        return 54;
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }
    @Override
    public Packet<PacketListenerPlayOut> emptyCopy() {
        return new PacketPlayOutOpenSignEditor();
    }
}
