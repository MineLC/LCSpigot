package lc.lcspigot.configuration;

public final class LCConfig {
    private static LCConfig instance;

    private final int tickTime;

    LCConfig(int tickTime) {
        this.tickTime = tickTime;
    }

    public int getTickTime() {
        return this.tickTime;
    }

    public static LCConfig getConfig() {
        return instance;
    }

    static void setInstance(LCConfig config) {
        instance = config;
    }
}
