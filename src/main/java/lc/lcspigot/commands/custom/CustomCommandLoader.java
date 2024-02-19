package lc.lcspigot.commands.custom;

import lc.lcspigot.commands.CommandStorage;

public final class CustomCommandLoader {

    public void load() {
        CommandStorage.register(new VersionCommand(), "version");
        CommandStorage.register(new TpsCommand(), "tps");
    }
}
