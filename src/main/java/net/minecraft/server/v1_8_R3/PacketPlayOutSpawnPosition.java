package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutSpawnPosition implements Packet<PacketListenerPlayOut> {

    public BlockPosition position; // CraftBukkit - public

    public PacketPlayOutSpawnPosition() {}

    public PacketPlayOutSpawnPosition(BlockPosition blockposition) {
        this.position = blockposition;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.position = packetdataserializer.c();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.position);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    @Override
    public int id() {
        return 5;
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }
    @Override
    public Packet<PacketListenerPlayOut> emptyCopy() {
        return new PacketPlayOutSpawnPosition();
    }
}
