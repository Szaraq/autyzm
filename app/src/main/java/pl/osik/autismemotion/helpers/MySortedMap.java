package pl.osik.autismemotion.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by m.osik2 on 2016-05-04.
 */
public class MySortedMap {
    private final TreeMap<String, Integer> map;
    private final ArrayList<String> keys;
    private final Comparator<String> comparator = new Comparator<String>() {
        @Override
        public int compare(String lhs, String rhs) {
            return lhs.compareTo(rhs);
        }
    };

    public MySortedMap() {
        map = new TreeMap<>(comparator);
        keys = new ArrayList<>();
    }

    public MySortedMap(TreeMap<String, Integer> oldMap) {
        map = new TreeMap<>(comparator);
        keys = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : oldMap.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
            keys.add(entry.getKey());
        }
        sort();
    }

    public void put(String key, int value) {
        map.put(key, value);
        if(!keys.contains(key)) {
            keys.add(key);
            sort();
        }
    }

    public String get(int position) {
        return keys.get(position);
    }

    public int get(String name) {
        return map.get(name);
    }

    public void remove(int position) {
        String name = keys.get(position);
        keys.remove(position);
        map.remove(name);
    }

    public void remove(String name) {
        keys.remove(name);
        map.remove(name);
    }

    private void sort() {
        Collections.sort(keys, comparator);
    }

    public int size() {
        return map.size();
    }

    public Set<Map.Entry<String, Integer>> entrySet() {
        return map.entrySet();
    }
}
