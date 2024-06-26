package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayInEntityAction implements Packet<PacketListenerPlayIn> {

    private int a;
    private PacketPlayInEntityAction.EnumPlayerAction animation;
    private int c;

    public PacketPlayInEntityAction() {}

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.e();
        this.animation = (PacketPlayInEntityAction.EnumPlayerAction) packetdataserializer.a(PacketPlayInEntityAction.EnumPlayerAction.class);
        this.c = packetdataserializer.e();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.b(this.a);
        packetdataserializer.a((Enum) this.animation);
        packetdataserializer.b(this.c);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public PacketPlayInEntityAction.EnumPlayerAction b() {
        return this.animation;
    }

    public int c() {
        return this.c;
    }

    @Override
    public int id() {
        return 11;
    }
    public static enum EnumPlayerAction {

        START_SNEAKING, STOP_SNEAKING, STOP_SLEEPING, START_SPRINTING, STOP_SPRINTING, RIDING_JUMP, OPEN_INVENTORY;

        private EnumPlayerAction() {}
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }

    @Override
    public Packet<PacketListenerPlayIn> emptyCopy() {
        return new PacketPlayInEntityAction();
    }
}
