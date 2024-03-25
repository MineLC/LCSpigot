package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutUpdateEntityNBT implements Packet<PacketListenerPlayOut> {

    private int a;
    private NBTTagCompound b;

    public PacketPlayOutUpdateEntityNBT() {}

    public PacketPlayOutUpdateEntityNBT(int i, NBTTagCompound nbttagcompound) {
        this.a = i;
        this.b = nbttagcompound;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.e();
        this.b = packetdataserializer.h();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.b(this.a);
        packetdataserializer.a(this.b);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    @Override
    public int id() {
        return 73;
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }
    @Override
    public Packet<PacketListenerPlayOut> emptyCopy() {
        return new PacketPlayOutUpdateEntityNBT();
    }
}
