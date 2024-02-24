package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Maps;
import java.util.Map;
import org.tinylog.Logger;

public class WorldGenFactory {

    private static Map<String, Class<? extends StructureStart>> b = Maps.newHashMap();
    private static Map<Class<? extends StructureStart>, String> c = Maps.newHashMap();
    private static Map<String, Class<? extends StructurePiece>> d = Maps.newHashMap();
    private static Map<Class<? extends StructurePiece>, String> e = Maps.newHashMap();

    private static void b(Class<? extends StructureStart> oclass, String s) {
        WorldGenFactory.b.put(s, oclass);
        WorldGenFactory.c.put(oclass, s);
    }

    static void a(Class<? extends StructurePiece> oclass, String s) {
        WorldGenFactory.d.put(s, oclass);
        WorldGenFactory.e.put(oclass, s);
    }

    public static String a(StructureStart structurestart) {
        return (String) WorldGenFactory.c.get(structurestart.getClass());
    }

    public static String a(StructurePiece structurepiece) {
        return (String) WorldGenFactory.e.get(structurepiece.getClass());
    }

    public static StructureStart a(NBTTagCompound nbttagcompound, World world) {
        StructureStart structurestart = null;

        try {
            Class oclass = (Class) WorldGenFactory.b.get(nbttagcompound.getString("id"));

            if (oclass != null) {
                structurestart = (StructureStart) oclass.newInstance();
            }
        } catch (Exception exception) {
            Logger.warn("Failed Start with id " + nbttagcompound.getString("id"));
            exception.printStackTrace();
        }

        if (structurestart != null) {
            structurestart.a(world, nbttagcompound);
        } else {
            Logger.warn("Skipping Structure with id " + nbttagcompound.getString("id"));
        }

        return structurestart;
    }

    public static StructurePiece b(NBTTagCompound nbttagcompound, World world) {
        StructurePiece structurepiece = null;

        try {
            Class oclass = (Class) WorldGenFactory.d.get(nbttagcompound.getString("id"));

            if (oclass != null) {
                structurepiece = (StructurePiece) oclass.newInstance();
            }
        } catch (Exception exception) {
            Logger.warn("Failed Piece with id " + nbttagcompound.getString("id"));
            exception.printStackTrace();
        }

        if (structurepiece != null) {
            structurepiece.a(world, nbttagcompound);
        } else {
            Logger.warn("Skipping Piece with id " + nbttagcompound.getString("id"));
        }

        return structurepiece;
    }

    static {
        b(WorldGenMineshaftStart.class, "Mineshaft");
        b(WorldGenVillage.WorldGenVillageStart.class, "Village");
        b(WorldGenNether.WorldGenNetherStart.class, "Fortress");
        b(WorldGenStronghold.WorldGenStronghold2Start.class, "Stronghold");
        b(WorldGenLargeFeature.WorldGenLargeFeatureStart.class, "Temple");
        b(WorldGenMonument.WorldGenMonumentStart.class, "Monument");
        WorldGenMineshaftPieces.a();
        WorldGenVillagePieces.a();
        WorldGenNetherPieces.a();
        WorldGenStrongholdPieces.a();
        WorldGenRegistration.a();
        WorldGenMonumentPieces.a();
    }
}