package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class MapGeneratorUtils {

    public static <K, V> Map<K, V> b(Iterable<K> iterable, Iterable<V> iterable1) {
        return a(iterable, iterable1, Maps.newLinkedHashMap());
    }

    public static <K, V> Map<K, V> a(Iterable<K> iterable, Iterable<V> iterable1, Map<K, V> map) {
        Iterator<V> iterator = iterable1.iterator();
        Iterator<K> iterator1 = iterable.iterator();

        while (iterator1.hasNext()) {
            K object = iterator1.next();

            map.put(object, iterator.next());
        }

        if (iterator.hasNext()) {
            throw new NoSuchElementException();
        } else {
            return map;
        }
    }
}
