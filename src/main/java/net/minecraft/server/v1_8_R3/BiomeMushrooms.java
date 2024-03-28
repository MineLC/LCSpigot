package net.minecraft.server.v1_8_R3;

public class BiomeMushrooms extends BiomeBase {

    public BiomeMushrooms(int i) {
        super(i);
        this.as.A = -100;
        this.as.B = -100;
        this.as.C = -100;
        this.as.E = 1;
        this.as.K = 1;
        this.ak = Blocks.MYCELIUM.getBlockData();
    }
}
