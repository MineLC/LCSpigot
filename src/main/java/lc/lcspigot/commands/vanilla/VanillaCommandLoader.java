package lc.lcspigot.commands.vanilla;

import lc.lcspigot.commands.CommandStorage;

public final class VanillaCommandLoader {

    public void load() {
        CommandStorage.register(new ClearCommand(), "clear");
        CommandStorage.register(new DeopCommand(), "deop");
        CommandStorage.register(new OpCommand(), "op");
        CommandStorage.register(new GamemodeCommand(), "gamemode", "gm");
        CommandStorage.register(new PluginsCommand(), "plugins", "pl");
        CommandStorage.register(new StopCommand(), "stop");
        CommandStorage.register(new GiveCommand(), "give");
        CommandStorage.register(new WorldBorderCommand(), "worldborder");
    }
}