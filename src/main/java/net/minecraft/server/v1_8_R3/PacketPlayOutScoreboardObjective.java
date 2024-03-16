package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutScoreboardObjective implements Packet<PacketListenerPlayOut> {

    public String a;
    public String b;
    public IScoreboardCriteria.EnumScoreboardHealthDisplay c;
    public int d;

    public PacketPlayOutScoreboardObjective() {}

    public PacketPlayOutScoreboardObjective(ScoreboardObjective scoreboardobjective, int i) {
        this.a = scoreboardobjective.getName();
        this.b = scoreboardobjective.getDisplayName();
        this.c = scoreboardobjective.getCriteria().c();
        this.d = i;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.c(16);
        this.d = packetdataserializer.readByte();
        if (this.d == 0 || this.d == 2) {
            this.b = packetdataserializer.c(32);
            this.c = IScoreboardCriteria.EnumScoreboardHealthDisplay.a(packetdataserializer.c(16));
        }

    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a);
        packetdataserializer.writeByte(this.d);
        if (this.d == 0 || this.d == 2) {
            packetdataserializer.a(this.b);
            packetdataserializer.a(this.c.a());
        }

    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    @Override
    public int id() {
        return 59;
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }
}
