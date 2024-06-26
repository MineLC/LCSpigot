package net.minecraft.server.v1_8_R3;

import java.io.IOException;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class PacketPlayOutTitle implements Packet<PacketListenerPlayOut> {

    public PacketPlayOutTitle.EnumTitleAction a;
    public IChatBaseComponent b;
    private int c;
    private int d;
    private int e;
    public BaseComponent[] components;
    public PacketPlayOutTitle() {}

    public PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction packetplayouttitle_enumtitleaction, IChatBaseComponent ichatbasecomponent) {
        this(packetplayouttitle_enumtitleaction, ichatbasecomponent, -1, -1, -1);
    }

    public PacketPlayOutTitle(int i, int j, int k) {
        this(PacketPlayOutTitle.EnumTitleAction.TIMES, (IChatBaseComponent) null, i, j, k);
    }

    public PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction packetplayouttitle_enumtitleaction, IChatBaseComponent ichatbasecomponent, int i, int j, int k) {
        this.a = packetplayouttitle_enumtitleaction;
        this.b = ichatbasecomponent;
        this.c = i;
        this.d = j;
        this.e = k;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = (PacketPlayOutTitle.EnumTitleAction) packetdataserializer.a(PacketPlayOutTitle.EnumTitleAction.class);
        if (this.a == PacketPlayOutTitle.EnumTitleAction.TITLE || this.a == PacketPlayOutTitle.EnumTitleAction.SUBTITLE) {
            this.b = packetdataserializer.d();
        }

        if (this.a == PacketPlayOutTitle.EnumTitleAction.TIMES) {
            this.c = packetdataserializer.readInt();
            this.d = packetdataserializer.readInt();
            this.e = packetdataserializer.readInt();
        }

    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a((Enum) this.a);
        if (this.a == PacketPlayOutTitle.EnumTitleAction.TITLE || this.a == PacketPlayOutTitle.EnumTitleAction.SUBTITLE) {
            if (this.components != null) {
                packetdataserializer.a(ComponentSerializer.toString(this.components));
            } else {
                packetdataserializer.a(this.b);
            }
        }

        if (this.a == PacketPlayOutTitle.EnumTitleAction.TIMES) {
            packetdataserializer.writeInt(this.c);
            packetdataserializer.writeInt(this.d);
            packetdataserializer.writeInt(this.e);
        }

    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }



    public static enum EnumTitleAction {

        TITLE, SUBTITLE, TIMES, CLEAR, RESET;

        private EnumTitleAction() {}

        public static PacketPlayOutTitle.EnumTitleAction a(String s) {
            PacketPlayOutTitle.EnumTitleAction[] apacketplayouttitle_enumtitleaction = values();
            int i = apacketplayouttitle_enumtitleaction.length;

            for (int j = 0; j < i; ++j) {
                PacketPlayOutTitle.EnumTitleAction packetplayouttitle_enumtitleaction = apacketplayouttitle_enumtitleaction[j];

                if (packetplayouttitle_enumtitleaction.name().equalsIgnoreCase(s)) {
                    return packetplayouttitle_enumtitleaction;
                }
            }

            return PacketPlayOutTitle.EnumTitleAction.TITLE;
        }

        public static String[] a() {
            String[] astring = new String[values().length];
            int i = 0;
            PacketPlayOutTitle.EnumTitleAction[] apacketplayouttitle_enumtitleaction = values();
            int j = apacketplayouttitle_enumtitleaction.length;

            for (int k = 0; k < j; ++k) {
                PacketPlayOutTitle.EnumTitleAction packetplayouttitle_enumtitleaction = apacketplayouttitle_enumtitleaction[k];

                astring[i++] = packetplayouttitle_enumtitleaction.name().toLowerCase();
            }

            return astring;
        }
    }
    
    @Override
    public int id() {
        return 69;
    }

    @Override
    public EnumProtocol getProtocol() {
        return EnumProtocol.PLAY;
    }
    
    @Override
    public Packet<PacketListenerPlayOut> emptyCopy() {
        return new PacketPlayOutTitle();
    }
}
