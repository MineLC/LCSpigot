package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutGameStateChange implements Packet<PacketListenerPlayOut> {

    public static final String[] a = new String[] { "tile.bed.notValid"};
    private int b;
    private float c;

    public PacketPlayOutGameStateChange() {}

    public PacketPlayOutGameStateChange(int i, float f) {
        this.b = i;
        this.c = f;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.b = packetdataserializer.readUnsignedByte();
        this.c = packetdataserializer.readFloat();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeByte(this.b);
        packetdataserializer.writeFloat(this.c);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    @Override
    public int id() {
        return 43;
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }
    @Override
    public Packet<PacketListenerPlayOut> emptyCopy() {
        return new PacketPlayOutGameStateChange();
    }
}
