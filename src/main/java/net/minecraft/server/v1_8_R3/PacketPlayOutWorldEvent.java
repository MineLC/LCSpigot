package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutWorldEvent implements Packet<PacketListenerPlayOut> {

    private int a;
    private BlockPosition b;
    private int c;
    private boolean d;

    public PacketPlayOutWorldEvent() {}

    public PacketPlayOutWorldEvent(int i, BlockPosition blockposition, int j, boolean flag) {
        this.a = i;
        this.b = blockposition;
        this.c = j;
        this.d = flag;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.readInt();
        this.b = packetdataserializer.c();
        this.c = packetdataserializer.readInt();
        this.d = packetdataserializer.readBoolean();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeInt(this.a);
        packetdataserializer.a(this.b);
        packetdataserializer.writeInt(this.c);
        packetdataserializer.writeBoolean(this.d);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    @Override
    public int id() {
        return 40;
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }
    @Override
    public Packet<PacketListenerPlayOut> emptyCopy() {
        return new PacketPlayOutWorldEvent();
    }
}
