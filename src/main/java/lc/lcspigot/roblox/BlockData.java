package lc.lcspigot.roblox;

import org.bukkit.Material;

public final class BlockData {

    final int hashCode, x, y, z;
    final Material material;

    BlockData(int hashcode, int x, int y, int z, Material material) {
        this.hashCode = hashcode;
        this.x = x;
        this.y = y;
        this.z = z;
        this.material = material;
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