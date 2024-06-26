package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.Validate;
import org.tinylog.Logger;

public class RegistrySimple<K, V> implements IRegistry<K, V> {

    protected final Map<K, V> c = this.b();

    public RegistrySimple() {}

    protected Map<K, V> b() {
        return Maps.newHashMap();
    }

    public V get(K k0) {
        return this.c.get(k0);
    }

    public void a(K k0, V v0) {
        Validate.notNull(k0);
        Validate.notNull(v0);
        if (this.c.containsKey(k0)) {
            Logger.debug("Adding duplicate key \'" + k0 + "\' to registry");
        }

        this.c.put(k0, v0);
    }

    public Set<K> keySet() {
        return Collections.unmodifiableSet(this.c.keySet());
    }

    public boolean d(K k0) {
        return this.c.containsKey(k0);
    }

    public Iterator<V> iterator() {
        return this.c.values().iterator();
    }
}
