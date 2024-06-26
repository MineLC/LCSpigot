package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayInResourcePackStatus implements Packet<PacketListenerPlayIn> {

    private String a;
    private PacketPlayInResourcePackStatus.EnumResourcePackStatus b;

    public PacketPlayInResourcePackStatus() {}

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.c(40);
        this.b = (PacketPlayInResourcePackStatus.EnumResourcePackStatus) packetdataserializer.a(PacketPlayInResourcePackStatus.EnumResourcePackStatus.class);
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a);
        packetdataserializer.a((Enum) this.b);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    @Override
    public int id() {
        return 25;
    }

    public static enum EnumResourcePackStatus {

        SUCCESSFULLY_LOADED, DECLINED, FAILED_DOWNLOAD, ACCEPTED;

        private EnumResourcePackStatus() {}
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }

    @Override
    public Packet<PacketListenerPlayIn> emptyCopy() {
        return new PacketPlayInResourcePackStatus();
    }
}
