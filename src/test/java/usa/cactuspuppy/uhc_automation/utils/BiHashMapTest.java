package usa.cactuspuppy.uhc_automation.utils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BiHashMapTest {
    private BiHashMap<String, Integer> testMap = new BiHashMap<>();

    @Before
    public void setUp() throws Exception {
        testMap = new BiHashMap<>();
    }

    @Test
    public void size() {
        assertEquals(0, testMap.size());
        testMap.put("asdf", 0);
        assertEquals(1, testMap.size());
        testMap.put("asdf", 0);
        testMap.put("asdf1", 2);
        assertEquals(2, testMap.size());
    }

    @Test
    public void isEmpty() {
        assertTrue(testMap.isEmpty());
        testMap.put("test", 123);
        assertFalse(testMap.isEmpty());
    }

    @Test
    public void containsKey() {
        assertFalse(testMap.containsKey("test"));
        testMap.put("test", 123);
        assertTrue(testMap.containsKey("test"));
        testMap.put("notTest", 123);
        assertFalse(testMap.containsKey("test"));
    }

    @Test
    public void containsValue() {
        assertFalse(testMap.containsValue(123));
        testMap.put("test", 123);
        assertTrue(testMap.containsValue(123));
        testMap.put("test", 234);
        assertFalse(testMap.containsValue(123));
    }


    //TODO
    @Test
    public void get() {
    }

    @Test
    public void getKey() {
    }

    @Test
    public void put() {
    }

    @Test
    public void remove() {
    }

    @Test
    public void removeValue() {
    }

    @Test
    public void clear() {
    }
}