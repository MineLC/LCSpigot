package net.minecraft.server.v1_8_R3;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;

public class PacketLoginOutSuccess implements Packet<PacketLoginOutListener> {

    private GameProfile a;

    public PacketLoginOutSuccess() {}

    public PacketLoginOutSuccess(GameProfile gameprofile) {
        this.a = gameprofile;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        String s = packetdataserializer.c(36);
        String s1 = packetdataserializer.c(16);
        UUID uuid = UUID.fromString(s);

        this.a = new GameProfile(uuid, s1);
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        UUID uuid = this.a.getId();

        packetdataserializer.a(uuid == null ? "" : uuid.toString());
        packetdataserializer.a(this.a.getName());
    }

    public void a(PacketLoginOutListener packetloginoutlistener) {
        packetloginoutlistener.a(this);
    }

    @Override
    public int id() {
        return 2;
    }
    
    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.LOGIN;
    }

    @Override
    public Packet<PacketLoginOutListener> emptyCopy() {
        return new PacketLoginOutSuccess();
    }
}
