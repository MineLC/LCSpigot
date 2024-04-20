package lc.lcspigot.roblox;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;


public final class RobloxData {

    private static RobloxData instance;

    Set<BlockData> blocks = new HashSet<>();

    public void changedBlock(final Material material, final int x, final int y, final int z) {
        final BlockData data = new BlockData(createHash(x, y, z), x, y, z, material);
        blocks.remove(data);
        blocks.add(data);
    }

    private int createHash(final int x, final int y, final int z) {
        int hash = 3;

        hash = 19 * hash + (int) (Double.doubleToLongBits(x) ^ (Double.doubleToLongBits(x) >>> 32));
        hash = 19 * hash + (int) (Double.doubleToLongBits(y) ^ (Double.doubleToLongBits(y) >>> 32));
        hash = 19 * hash + (int) (Double.doubleToLongBits(z) ^ (Double.doubleToLongBits(z) >>> 32));

        return hash;
    }

    public static RobloxData getInstance() {
        return instance;
    }

    static void start() {
        instance = new RobloxData();
    }
}