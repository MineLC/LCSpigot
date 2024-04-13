package lc.lcspigot.commands.custom;

import lc.lcspigot.commands.CommandStorage;

public final class CustomCommandLoader {

    public void load() {
        CommandStorage.register(new VersionCommand(), "version", "ver");
        CommandStorage.register(new TpsCommand(), "tps");
        CommandStorage.register(new KnockbackCommand(), "kb");
        CommandStorage.register(new BroadcastCommand(), "bc");
        CommandStorage.register(new PingCommand(), "ping");
    }
}
