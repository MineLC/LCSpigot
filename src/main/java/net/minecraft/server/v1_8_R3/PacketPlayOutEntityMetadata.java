package net.minecraft.server.v1_8_R3;

import java.io.IOException;
import java.util.List;

public class PacketPlayOutEntityMetadata implements Packet<PacketListenerPlayOut> {

    private int a;
    private List<DataWatcher.WatchableObject> b;

    public PacketPlayOutEntityMetadata() {}

    public PacketPlayOutEntityMetadata(int i, DataWatcher datawatcher, boolean flag) {
        this.a = i;
        if (flag) {
            this.b = datawatcher.c();
        } else {
            this.b = datawatcher.b();
        }

    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.e();
        this.b = DataWatcher.b(packetdataserializer);
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.b(this.a);
        DataWatcher.a(this.b, packetdataserializer);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    @Override
    public int id() {
        return 28;
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }
    @Override
    public Packet<PacketListenerPlayOut> emptyCopy() {
        return new PacketPlayOutEntityMetadata();
    }
}
