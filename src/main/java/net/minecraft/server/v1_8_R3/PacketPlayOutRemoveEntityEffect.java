package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutRemoveEntityEffect implements Packet<PacketListenerPlayOut> {

    private int a;
    private int b;

    public PacketPlayOutRemoveEntityEffect() {}

    public PacketPlayOutRemoveEntityEffect(int i, MobEffect mobeffect) {
        this.a = i;
        this.b = mobeffect.getEffectId();
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.e();
        this.b = packetdataserializer.readUnsignedByte();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.b(this.a);
        packetdataserializer.writeByte(this.b);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    @Override
    public int id() {
        return 30;
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }
    @Override
    public Packet<PacketListenerPlayOut> emptyCopy() {
        return new PacketPlayOutRemoveEntityEffect();
    }
}
