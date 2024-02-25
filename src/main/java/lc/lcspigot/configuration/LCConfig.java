package lc.lcspigot.configuration;

import lc.lcspigot.configuration.sections.ConfigKnockback;

public final class LCConfig {
    private static LCConfig instance;

    public final String unknownCommand;
    public final int tickTime;
    public final int containerUpdateDelay;
    public final int tickNextTickCap;
    public final ConfigKnockback knockback;

    LCConfig(String unknownCommand, int tickTime, int containerUpdateDelay, int tickNextTickCap, ConfigKnockback knockback) {
        this.unknownCommand = unknownCommand;
        this.tickTime = tickTime;
        this.containerUpdateDelay = containerUpdateDelay;
        this.tickNextTickCap = tickNextTickCap;
        this.knockback = knockback;
    }

    public static LCConfig getConfig() {
        return instance;
    }

    static void setInstance(LCConfig config) {
        instance = config;
    }
}
