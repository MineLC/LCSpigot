package net.minecraft.server.v1_8_R3;

public class BiomeTheEnd extends BiomeBase {

    public BiomeTheEnd(int i) {
        super(i);
        this.aw.clear();
        this.ak = Blocks.DIRT.getBlockData();
        this.al = Blocks.DIRT.getBlockData();
        this.as = new BiomeTheEndDecorator();
    }
}
