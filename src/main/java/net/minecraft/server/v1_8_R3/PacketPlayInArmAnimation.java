package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayInArmAnimation implements Packet<PacketListenerPlayIn> {

    public long timestamp; // Spigot

    public PacketPlayInArmAnimation() {}

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        timestamp = System.currentTimeMillis(); // Spigot
    }
    @Override
    public int id() {
        return 10;
    }
    public void b(PacketDataSerializer packetdataserializer) throws IOException {}

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }
}
