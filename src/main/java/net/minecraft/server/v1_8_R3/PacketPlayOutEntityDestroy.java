package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutEntityDestroy implements Packet<PacketListenerPlayOut> {

    private int[] a;

    public PacketPlayOutEntityDestroy() {}

    public PacketPlayOutEntityDestroy(int... aint) {
        this.a = aint;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = new int[packetdataserializer.e()];

        for (int i = 0; i < this.a.length; ++i) {
            this.a[i] = packetdataserializer.e();
        }

    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.b(this.a.length);

        for (int i = 0; i < this.a.length; ++i) {
            packetdataserializer.b(this.a[i]);
        }

    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    @Override
    public int id() {
        return 19;
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }
    @Override
    public Packet<PacketListenerPlayOut> emptyCopy() {
        return new PacketPlayOutEntityDestroy();
    }
}
