package lc.lcspigot.roblox;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;

import net.minecraft.server.v1_8_R3.EntityPlayer;

public final class RobloxData {

    private static RobloxData instance;

    private final Set<BlockData> blocks = new HashSet<>();
    private final Set<EntityPlayer> joins = new HashSet<>();
    private final Set<EntityPlayer> quits = new HashSet<>();

    public void playerQuit(final EntityPlayer player) {
        quits.add(player);
    }

    public void playerJoin(final EntityPlayer player) {
        joins.add(player);
    }

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

    public void clear() {
        blocks.clear();
        if (!quits.isEmpty()) {
            quits.clear();
        }
        if (!joins.isEmpty()) {
            joins.clear();
        }
    }

    public Set<BlockData> getBlocks() {
        return blocks;
    }

    public Set<EntityPlayer> getJoins() {
        return joins;
    }

    public Set<EntityPlayer> getQuits() {
        return quits;
    }

    public static RobloxData getInstance() {
        return instance;
    }

    public static void start() {
        instance = new RobloxData();
    }
}