package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutScoreboardDisplayObjective implements Packet<PacketListenerPlayOut> {

    private int a;
    private String b;

    public PacketPlayOutScoreboardDisplayObjective() {}

    public PacketPlayOutScoreboardDisplayObjective(int i, ScoreboardObjective scoreboardobjective) {
        this.a = i;
        if (scoreboardobjective == null) {
            this.b = "";
        } else {
            this.b = scoreboardobjective.getName();
        }

    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.readByte();
        this.b = packetdataserializer.c(16);
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeByte(this.a);
        packetdataserializer.a(this.b);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    @Override
    public int id() {
        return 61;
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }
    @Override
    public Packet<PacketListenerPlayOut> emptyCopy() {
        return new PacketPlayOutScoreboardDisplayObjective();
    }
}
