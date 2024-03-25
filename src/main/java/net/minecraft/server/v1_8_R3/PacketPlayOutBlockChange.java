package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutBlockChange implements Packet<PacketListenerPlayOut> {

    private BlockPosition a;
    public IBlockData block; // CraftBukkit - public

    public PacketPlayOutBlockChange() {}

    public PacketPlayOutBlockChange(World world, BlockPosition blockposition) {
        this.a = blockposition;
        this.block = world.getType(blockposition);
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.c();
        this.block = (IBlockData) Block.d.a(packetdataserializer.e());
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a);
        packetdataserializer.b(Block.d.b(this.block));
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
    @Override
    public int id() {
        return 35;
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }
    @Override
    public Packet<PacketListenerPlayOut> emptyCopy() {
        return new PacketPlayOutBlockChange();
    }
}
