package net.minecraft.server.v1_8_R3;

public class BiomeBeach extends BiomeBase {

    public BiomeBeach(int i) {
        super(i);
        this.ak = Blocks.SAND.getBlockData();
        this.al = Blocks.SAND.getBlockData();
        this.as.A = -999;
        this.as.D = 0;
        this.as.F = 0;
        this.as.G = 0;
    }
}
