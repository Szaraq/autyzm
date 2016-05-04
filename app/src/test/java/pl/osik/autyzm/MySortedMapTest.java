package pl.osik.autyzm;

import junit.framework.TestCase;

import java.util.Map;
import java.util.TreeMap;

import pl.osik.autyzm.helpers.MySortedMap;

/**
 * Created by m.osik2 on 2016-05-04.
 */
public class MySortedMapTest extends TestCase {

    MySortedMap map;

    protected void setUp() {
        map = new MySortedMap();
    }

    public void testAdd() {
        map.put("klucz1", 1);
        assertEquals(1, map.size());
        assertEquals("klucz1", map.get(0));
        assertEquals(1, map.get("klucz1"));
    }

    public void testSort() {
        String[] input = { "aKlucz", "bKlucz", "cKlucz", "dKlucz" };
        int[] inputNum = { 15, 14, 12, 13};
        map.put(input[0], inputNum[0]);
        map.put(input[2], inputNum[2]);
        map.put(input[1], inputNum[1]);
        map.put(input[3], inputNum[3]);
        assertEquals(input.length, map.size());

        int count = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            assertEquals(input[count], entry.getKey());
            assertEquals(input[count], map.get(count));
            assertEquals(inputNum[count], map.get(input[count]));
            count++;
        }
    }

    public void testRemoveByName() {
        String[] input = { "aKlucz", "bKlucz", "dKlucz", "eKlucz" };
        int[] inputNum = { 15, 14, 12, 13};
        map.put(input[0], inputNum[0]);
        map.put(input[2], inputNum[2]);
        map.put("cKlucz", 11);
        map.put(input[1], inputNum[1]);
        map.put(input[3], inputNum[3]);
        map.remove("cKlucz");
        assertEquals(input.length, map.size());

        int count = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            assertEquals(input[count], entry.getKey());
            assertEquals(input[count], map.get(count));
            count++;
        }
    }

    public void testRemoveByPosition() {
        String[] input = { "aKlucz", "bKlucz", "dKlucz", "eKlucz" };
        int[] inputNum = { 15, 14, 12, 13};
        map.put(input[0], inputNum[0]);
        map.put(input[2], inputNum[2]);
        map.put("cKlucz", 11);
        map.put(input[1], inputNum[1]);
        map.put(input[3], inputNum[3]);
        map.remove(2);
        assertEquals(input.length, map.size());

        int count = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            assertEquals(input[count], entry.getKey());
            assertEquals(input[count], map.get(count));
            count++;
        }
    }

    public void testCreateFromMap() {
        String[] input = { "aKlucz", "bKlucz", "dKlucz", "eKlucz" };
        int[] inputNum = { 15, 14, 12, 13};
        TreeMap<String, Integer> tree = new TreeMap<>();
        tree.put(input[0], inputNum[0]);
        tree.put(input[2], inputNum[2]);
        tree.put(input[1], inputNum[1]);
        tree.put(input[3], inputNum[3]);
        map = new MySortedMap(tree);
        assertEquals(input.length, map.size());

        int count = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            assertEquals(input[count], entry.getKey());
            assertEquals(input[count], map.get(count));
            count++;
        }
    }
}
