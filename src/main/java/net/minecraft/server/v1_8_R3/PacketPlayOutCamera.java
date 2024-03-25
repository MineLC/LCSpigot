package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutCamera implements Packet<PacketListenerPlayOut> {

    public int a;

    public PacketPlayOutCamera() {}

    public PacketPlayOutCamera(Entity entity) {
        this.a = entity.getId();
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.e();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.b(this.a);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
    @Override
    public int id() {
        return 67;
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }
    @Override
    public Packet<PacketListenerPlayOut> emptyCopy() {
        return new PacketPlayOutCamera();
    }
}
