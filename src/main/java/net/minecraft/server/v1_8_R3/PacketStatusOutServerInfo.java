package net.minecraft.server.v1_8_R3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;

public class PacketStatusOutServerInfo implements Packet<PacketStatusOutListener> {

    private static final Gson a = (new GsonBuilder()).registerTypeAdapter(ServerPing.ServerData.class, new ServerPing.ServerData.ServerData$Serializer()).registerTypeAdapter(ServerPing.ServerPingPlayerSample.class, new ServerPing.ServerPingPlayerSample.ServerPingPlayerSample$Serializer()).registerTypeAdapter(ServerPing.class, new ServerPing.Serializer()).registerTypeHierarchyAdapter(IChatBaseComponent.class, new IChatBaseComponent.ChatSerializer()).registerTypeHierarchyAdapter(ChatModifier.class, new ChatModifier.ChatModifierSerializer()).registerTypeAdapterFactory(new ChatTypeAdapterFactory()).create();
    private ServerPing b;

    public PacketStatusOutServerInfo() {}

    public PacketStatusOutServerInfo(ServerPing serverping) {
        this.b = serverping;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.b = (ServerPing) PacketStatusOutServerInfo.a.fromJson(packetdataserializer.c(32767), ServerPing.class);
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(PacketStatusOutServerInfo.a.toJson(this.b));
    }

    public void a(PacketStatusOutListener packetstatusoutlistener) {
        packetstatusoutlistener.a(this);
    }
    @Override
    public int id() {
        return 0;
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.STATUS;
    }
    @Override
    public Packet<PacketStatusOutListener> emptyCopy() {
        return new PacketStatusOutServerInfo();
    }
}
