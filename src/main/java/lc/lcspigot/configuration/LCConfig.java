package lc.lcspigot.configuration;

import lc.lcspigot.configuration.sections.ConfigKnockback;

public final class LCConfig {
    private static LCConfig instance;

    public final String unknownCommand;
    public final int tickTime;
    public final int containerUpdateDelay;
    public final int tickNextTickCap;
    public final ConfigKnockback knockback;
    public final boolean canSaveWorlds;
    public final boolean logCommands;

    LCConfig(String unknownCommand, int tickTime, int containerUpdateDelay, int tickNextTickCap, boolean canSaveWorlds, ConfigKnockback knockback, final boolean logCommands) {
        this.unknownCommand = unknownCommand;
        this.tickTime = tickTime;
        this.containerUpdateDelay = containerUpdateDelay;
        this.tickNextTickCap = tickNextTickCap;
        this.canSaveWorlds = canSaveWorlds;
        this.knockback = knockback;
        this.logCommands = logCommands;
    }

    public static LCConfig getConfig() {
        return instance;
    }

    static void setInstance(LCConfig config) {
        instance = config;
    }
}
