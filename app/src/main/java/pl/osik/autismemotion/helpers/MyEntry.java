package pl.osik.autismemotion.helpers;

import java.util.Map;

/**
 * Created by m.osik2 on 2016-06-22.
 */
public class MyEntry<K, V> implements Map.Entry<K, V> {

    final K key;
    V value;

    public MyEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V object) {
        value = object;
        return value;
    }
}
