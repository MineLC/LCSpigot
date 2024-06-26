package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayInClientCommand implements Packet<PacketListenerPlayIn> {

    private PacketPlayInClientCommand.EnumClientCommand a;

    public PacketPlayInClientCommand() {}

    public PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand packetplayinclientcommand_enumclientcommand) {
        this.a = packetplayinclientcommand_enumclientcommand;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = (PacketPlayInClientCommand.EnumClientCommand) packetdataserializer.a(PacketPlayInClientCommand.EnumClientCommand.class);
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a((Enum) this.a);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public PacketPlayInClientCommand.EnumClientCommand a() {
        return this.a;
    }

    @Override
    public int id() {
        return 22;
    }

    public static enum EnumClientCommand {

        PERFORM_RESPAWN, REQUEST_STATS, OPEN_INVENTORY_ACHIEVEMENT;

        private EnumClientCommand() {}
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }

    @Override
    public Packet<PacketListenerPlayIn> emptyCopy() {
        return new PacketPlayInClientCommand();
    }
}
