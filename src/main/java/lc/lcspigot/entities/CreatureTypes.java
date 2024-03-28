package lc.lcspigot.entities;

import java.util.HashMap;
import java.util.Map;

public final class CreatureTypes {

    private static final Map<String, Integer> PER_NAME = new HashMap<>();

    static {
        PER_NAME.put("Mob", 48);
        PER_NAME.put("Monster", 49);
        PER_NAME.put("Creeper", 50);
        PER_NAME.put("Skeleton", 51);
        PER_NAME.put("Spider", 52);
        PER_NAME.put("Giant", 53);
        PER_NAME.put("Zombie", 54);
        PER_NAME.put("Slime", 55);
        PER_NAME.put("Ghast", 56);
        PER_NAME.put("PigZombie", 57);
        PER_NAME.put("Enderman", 58);
        PER_NAME.put("CaveSpider", 59);
        PER_NAME.put("Silverfish", 60);
        PER_NAME.put("Blaze", 61);
        PER_NAME.put("LavaSlime", 62);
        PER_NAME.put("EnderDragon", 63);
        PER_NAME.put("WitherBoss", 64);
        PER_NAME.put("Bat", 65);
        PER_NAME.put("Witch", 66);
        PER_NAME.put("Endermite", 67);
        PER_NAME.put("Guardian", 68);

        PER_NAME.put("Pig", 90);
        PER_NAME.put("Sheep", 91);
        PER_NAME.put("Cow", 92);
        PER_NAME.put("Chicken", 93);
        PER_NAME.put("Squid", 94);
        PER_NAME.put("Wolf", 95);
        PER_NAME.put("MushroomCow", 96);
        PER_NAME.put("SnowMan", 97);
        PER_NAME.put("Ozelot", 98);
        PER_NAME.put("VillagerGolem", 99);
        PER_NAME.put("EntityHorse", 100);
        PER_NAME.put("Rabbit", 101);
        PER_NAME.put("Villager", 120);
    }

    public static Integer getEntityType(final String name) {
        return PER_NAME.get(name);
    }
}