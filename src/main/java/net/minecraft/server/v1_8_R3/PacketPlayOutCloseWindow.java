package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutCloseWindow implements Packet<PacketListenerPlayOut> {

    private int a;

    public PacketPlayOutCloseWindow() {}

    public PacketPlayOutCloseWindow(int i) {
        this.a = i;
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.readUnsignedByte();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeByte(this.a);
    }
    @Override
    public int id() {
        return 46;
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }
    @Override
    public Packet<PacketListenerPlayOut> emptyCopy() {
        return new PacketPlayOutCloseWindow();
    }
}
