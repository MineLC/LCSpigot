package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutEntityEquipment implements Packet<PacketListenerPlayOut> {

    private int a;
    private int b;
    private ItemStack c;

    public PacketPlayOutEntityEquipment() {}

    public PacketPlayOutEntityEquipment(int i, int j, ItemStack itemstack) {
        this.a = i;
        this.b = j;
        this.c = itemstack == null ? null : itemstack.cloneItemStack();
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.e();
        this.b = packetdataserializer.readShort();
        this.c = packetdataserializer.i();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.b(this.a);
        packetdataserializer.writeShort(this.b);
        packetdataserializer.a(this.c);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    @Override
    public int id() {
        return 4;
    }


    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }
    @Override
    public Packet<PacketListenerPlayOut> emptyCopy() {
        return new PacketPlayOutEntityEquipment();
    }
}
