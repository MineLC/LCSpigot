package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Map;

public class PacketPlayOutStatistic implements Packet<PacketListenerPlayOut> {

    private Map<Object, Integer> a;

    public PacketPlayOutStatistic() {}

    public PacketPlayOutStatistic(Map<Object, Integer> map) {
        this.a = map;
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {

        this.a = Maps.newHashMap();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.b(this.a.size());

    }
    @Override
    public int id() {
        return 55;
    }
}
