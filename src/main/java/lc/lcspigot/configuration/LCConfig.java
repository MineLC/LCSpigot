package lc.lcspigot.configuration;

public final class LCConfig {
    private static LCConfig instance;

    private final String unknownCommand;
    private final int tickTime;
    private final int containerUpdateDelay;
    private final int tickNextTickCap;

    LCConfig(String unknownCommand, int tickTime, int containerUpdateDelay, int tickNextTickCap) {
        this.unknownCommand = unknownCommand;
        this.tickTime = tickTime;
        this.containerUpdateDelay = containerUpdateDelay;
        this.tickNextTickCap = tickNextTickCap;
    }

    public String getUnknownCommand() {
        return unknownCommand;
    }

    public int getTickTime() {
        return this.tickTime;
    }

    public int getContainerUpdateDelay() {
        return containerUpdateDelay;
    }

    public int getTickNextTickCap() {
        return tickNextTickCap;
    }

    public static LCConfig getConfig() {
        return instance;
    }

    static void setInstance(LCConfig config) {
        instance = config;
    }
}
