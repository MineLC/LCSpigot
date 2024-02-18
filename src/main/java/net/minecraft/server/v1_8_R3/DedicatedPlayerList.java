package net.minecraft.server.v1_8_R3;

import com.mojang.authlib.GameProfile;

import org.tinylog.Logger;

public class DedicatedPlayerList extends PlayerList {

    public DedicatedPlayerList(DedicatedServer dedicatedserver) {
        super(dedicatedserver);
        this.a(dedicatedserver.a("view-distance", 10));
        this.maxPlayers = dedicatedserver.a("max-players", 20);
        this.setHasWhitelist(dedicatedserver.a("white-list", false));

        this.A();
        this.C();
        this.B();
        if (!this.getWhitelist().c().exists()) {
            this.D();
        }

    }

    public void setHasWhitelist(boolean flag) {
        super.setHasWhitelist(flag);
        this.getServer().a("white-list", (Object) Boolean.valueOf(flag));
        this.getServer().a();
    }

    public void addOp(GameProfile gameprofile) {
        super.addOp(gameprofile);
        this.B();
    }

    public void removeOp(GameProfile gameprofile) {
        super.removeOp(gameprofile);
        this.B();
    }

    public void removeWhitelist(GameProfile gameprofile) {
        super.removeWhitelist(gameprofile);
        this.D();
    }

    public void addWhitelist(GameProfile gameprofile) {
        super.addWhitelist(gameprofile);
        this.D();
    }

    public void reloadWhitelist() {
        this.C();
    }


    private void A() {
        try {
            this.getOPs().load();
        } catch (Exception exception) {
            Logger.warn("Failed to load operators list: ", exception);
        }

    }

    private void B() {
        try {
            this.getOPs().save();
        } catch (Exception exception) {
            Logger.warn("Failed to save operators list: ", exception);
        }

    }

    private void C() {
        try {
            this.getWhitelist().load();
        } catch (Exception exception) {
            Logger.warn("Failed to load white-list: ", exception);
        }

    }

    private void D() {
        try {
            this.getWhitelist().save();
        } catch (Exception exception) {
            Logger.warn("Failed to save white-list: ", exception);
        }

    }

    public boolean isWhitelisted(GameProfile gameprofile) {
        return !this.getHasWhitelist() || this.isOp(gameprofile) || this.getWhitelist().isWhitelisted(gameprofile);
    }

    public DedicatedServer getServer() {
        return (DedicatedServer) super.getServer();
    }

    public boolean f(GameProfile gameprofile) {
        return this.getOPs().b(gameprofile);
    }
}
