package lc.lcspigot.configuration;

import lc.lcspigot.configuration.sections.ConfigKnockback;

public final class LCConfig {
    private static LCConfig instance;

    public final String unknownCommand;
    public final int tickTime;
    public final int defaultTime;
    public final int containerUpdateDelay;
    public final int tickNextTickCap;
    public final ConfigKnockback knockback;

    public final boolean canSaveWorlds;
    public final boolean logCommands;
    public final boolean allowConsole;
    public final boolean disableLogging;
    public final boolean allowWeather;

    LCConfig(String unknownCommand, int tickTime, int defaultTime, int containerUpdateDelay, int tickNextTickCap, boolean canSaveWorlds, ConfigKnockback knockback, boolean logCommands, boolean allowConsole, boolean disableLogging, boolean allowWeather) {
        this.unknownCommand = unknownCommand;
        this.tickTime = tickTime;
        this.containerUpdateDelay = containerUpdateDelay;
        this.defaultTime = defaultTime;
        this.tickNextTickCap = tickNextTickCap;
        this.canSaveWorlds = canSaveWorlds;
        this.knockback = knockback;
        this.logCommands = logCommands;
        this.allowConsole = allowConsole;
        this.disableLogging = disableLogging;
        this.allowWeather = allowWeather;
    }

    public static LCConfig getConfig() {
        return instance;
    }

    static void setInstance(LCConfig config) {
        instance = config;
    }
}
