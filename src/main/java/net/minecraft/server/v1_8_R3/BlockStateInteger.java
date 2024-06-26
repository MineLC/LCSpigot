package net.minecraft.server.v1_8_R3;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;

public class BlockStateInteger extends BlockState<Integer> {

    private final ImmutableSet<Integer> a;

    protected BlockStateInteger(String s, int i, int j) {
        super(s, Integer.class);
        if (i < 0) {
            throw new IllegalArgumentException("Min value of " + s + " must be 0 or greater");
        } else if (j <= i) {
            throw new IllegalArgumentException("Max value of " + s + " must be greater than min (" + i + ")");
        } else {
            HashSet hashset = Sets.newHashSet();

            for (int k = i; k <= j; ++k) {
                hashset.add(Integer.valueOf(k));
            }

            this.a = ImmutableSet.copyOf(hashset);
        }
    }

    public Collection<Integer> c() {
        return this.a;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            if (!super.equals(object)) {
                return false;
            } else {
                BlockStateInteger blockstateinteger = (BlockStateInteger) object;

                return this.a.equals(blockstateinteger.a);
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int i = super.hashCode();

        i = 31 * i + this.a.hashCode();
        return i;
    }

    public static BlockStateInteger of(String s, int i, int j) {
        return new BlockStateInteger(s, i, j);
    }

    public String a(Integer integer) {
        return integer.toString();
    }
}