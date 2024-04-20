package lc.lcspigot.roblox;

import org.bukkit.Material;

public final class BlockData {

    private final int hashCode, x, y, z;
    private final Material material;

    BlockData(int hashcode, int x, int y, int z, Material material) {
        this.hashCode = hashcode;
        this.x = x;
        this.y = y;
        this.z = z;
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int z() {
        return z;
    }
    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof BlockData data) ? (data.x == this.x && data.y == this.y && data.z == this.z) : false;
    }
}