package net.minecraft.server.v1_8_R3;

public class BiomeTheEndDecorator extends BiomeDecorator {

    protected WorldGenerator M;

    public BiomeTheEndDecorator() {
        this.M = new WorldGenEnder(Blocks.END_STONE);
    }

    protected void a(BiomeBase biomebase) {
        this.a();
        if (this.b.nextInt(5) == 0) {
            int i = this.b.nextInt(16) + 8;
            int j = this.b.nextInt(16) + 8;

            this.M.generate(this.a, this.b, this.a.r(this.c.a(i, 0, j)));
        }

    }
}
