package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tinylog.Logger;

public class EntityTypes {

    private static final Map<String, Class<? extends Entity>> c = Maps.newHashMap();
    private static final Map<Class<? extends Entity>, String> d = Maps.newHashMap();
    private static final Map<Integer, Class<? extends Entity>> e = Maps.newHashMap();
    private static final Map<Class<? extends Entity>, Integer> f = Maps.newHashMap();
    private static final Map<String, Integer> g = Maps.newHashMap();
    public static final Map<Integer, EntityTypes.MonsterEggInfo> eggInfo = Maps.newLinkedHashMap();

    private static void a(Class<? extends Entity> oclass, String s, int i) {
        if (EntityTypes.c.containsKey(s)) {
            throw new IllegalArgumentException("ID is already registered: " + s);
        } else if (EntityTypes.e.containsKey(Integer.valueOf(i))) {
            throw new IllegalArgumentException("ID is already registered: " + i);
        } else if (i == 0) {
            throw new IllegalArgumentException("Cannot register to reserved id: " + i);
        } else if (oclass == null) {
            throw new IllegalArgumentException("Cannot register null clazz for id: " + i);
        } else {
            EntityTypes.c.put(s, oclass);
            EntityTypes.d.put(oclass, s);
            EntityTypes.e.put(Integer.valueOf(i), oclass);
            EntityTypes.f.put(oclass, Integer.valueOf(i));
            EntityTypes.g.put(s, Integer.valueOf(i));
        }
    }

    private static void a(Class<? extends Entity> oclass, String s, int i, int j, int k) {
        a(oclass, s, i);
        EntityTypes.eggInfo.put(Integer.valueOf(i), new EntityTypes.MonsterEggInfo(i, j, k));
    }

    public static Entity createEntityByName(String s, World world) {
        Entity entity = null;

        try {
            Class oclass = (Class) EntityTypes.c.get(s);

            if (oclass != null) {
                entity = (Entity) oclass.getConstructor(new Class[] { World.class}).newInstance(new Object[] { world});
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return entity;
    }

    public static Entity a(NBTTagCompound nbttagcompound, World world) {
        Entity entity = null;

        if ("Minecart".equals(nbttagcompound.getString("id"))) {
            nbttagcompound.setString("id", EntityMinecartAbstract.EnumMinecartType.a(nbttagcompound.getInt("Type")).b());
            nbttagcompound.remove("Type");
        }

        try {
            Class oclass = (Class) EntityTypes.c.get(nbttagcompound.getString("id"));

            if (oclass != null) {
                entity = (Entity) oclass.getConstructor(new Class[] { World.class}).newInstance(new Object[] { world});
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (entity != null) {
            entity.f(nbttagcompound);
        } else {
            Logger.warn("Skipping Entity with id " + nbttagcompound.getString("id"));
        }

        return entity;
    }

    public static Entity a(int i, World world) {
        Entity entity = null;

        try {
            Class oclass = a(i);

            if (oclass != null) {
                entity = (Entity) oclass.getConstructor(new Class[] { World.class}).newInstance(new Object[] { world});
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (entity == null) {
            Logger.warn("Skipping Entity with id " + i);
        }

        return entity;
    }

    public static int a(Entity entity) {
        Integer integer = (Integer) EntityTypes.f.get(entity.getClass());

        return integer == null ? 0 : integer.intValue();
    }

    public static Class<? extends Entity> a(int i) {
        return (Class) EntityTypes.e.get(Integer.valueOf(i));
    }

    public static String b(Entity entity) {
        return (String) EntityTypes.d.get(entity.getClass());
    }

    public static String b(int i) {
        return (String) EntityTypes.d.get(a(i));
    }

    public static void a() {}

    public static List<String> b() {
        Set set = EntityTypes.c.keySet();
        ArrayList arraylist = Lists.newArrayList();
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            Class oclass = (Class) EntityTypes.c.get(s);

            if ((oclass.getModifiers() & 1024) != 1024) {
                arraylist.add(s);
            }
        }

        arraylist.add("LightningBolt");
        return arraylist;
    }

    public static boolean a(Entity entity, String s) {
        String s1 = b(entity);

        if (s1 == null && entity instanceof EntityHuman) {
            s1 = "Player";
        } else if (s1 == null && entity instanceof EntityLightning) {
            s1 = "LightningBolt";
        }

        return s.equals(s1);
    }

    public static boolean b(String s) {
        return "Player".equals(s) || b().contains(s);
    }

    static {
        a(EntityItem.class, "Item", 1);
        a(EntityExperienceOrb.class, "XPOrb", 2);
        a(EntityEgg.class, "ThrownEgg", 7);
        a(EntityLeash.class, "LeashKnot", 8);
        a(EntityPainting.class, "Painting", 9);
        a(EntityArrow.class, "Arrow", 10);
        a(EntitySnowball.class, "Snowball", 11);
        a(EntityLargeFireball.class, "Fireball", 12);
        a(EntitySmallFireball.class, "SmallFireball", 13);
        a(EntityEnderPearl.class, "ThrownEnderpearl", 14);
        a(EntityEnderSignal.class, "EyeOfEnderSignal", 15);
        a(EntityPotion.class, "ThrownPotion", 16);
        a(EntityThrownExpBottle.class, "ThrownExpBottle", 17);
        a(EntityItemFrame.class, "ItemFrame", 18);
        a(EntityWitherSkull.class, "WitherSkull", 19);
        a(EntityTNTPrimed.class, "PrimedTnt", 20);
        a(EntityFallingBlock.class, "FallingSand", 21);
        a(EntityFireworks.class, "FireworksRocketEntity", 22);
        a(EntityArmorStand.class, "ArmorStand", 30);
        a(EntityBoat.class, "Boat", 41);
        a(EntityMinecartRideable.class, EntityMinecartAbstract.EnumMinecartType.RIDEABLE.b(), 42);
        a(EntityMinecartChest.class, EntityMinecartAbstract.EnumMinecartType.CHEST.b(), 43);
        a(EntityMinecartFurnace.class, EntityMinecartAbstract.EnumMinecartType.FURNACE.b(), 44);
        a(EntityMinecartTNT.class, EntityMinecartAbstract.EnumMinecartType.TNT.b(), 45);
        a(EntityMinecartHopper.class, EntityMinecartAbstract.EnumMinecartType.HOPPER.b(), 46);
        a(EntityMinecartMobSpawner.class, EntityMinecartAbstract.EnumMinecartType.SPAWNER.b(), 47);
        a(EntityMinecartCommandBlock.class, EntityMinecartAbstract.EnumMinecartType.COMMAND_BLOCK.b(), 40);
    }

    public static class MonsterEggInfo {

        public final int a;
        public final int b;
        public final int c;

        public MonsterEggInfo(int i, int j, int k) {
            this.a = i;
            this.b = j;
            this.c = k;
        }
    }
}
