package usa.cactuspuppy.uhc_automation.utils;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class MojangAPIHookTest {
    @Before
    public void clearCache() {
        MojangAPIHook.clearCache();
    }

    @Test
    public void getUUID() {
        long start = System.nanoTime();
        UUID u = MojangAPIHook.getUUID("CactusPuppy");
        long elapsedNoCache = System.nanoTime() - start;
        assertNotNull(u);
        assertEquals("c0681344-b5a8-428f-a57c-98704e760170", u.toString());
        //Hit Hook again to check for caching
        start = System.nanoTime();
        u = MojangAPIHook.getUUID("CactusPuppy");
        long elapsedCached = System.nanoTime() - start;
        assertNotNull(u);
        assertEquals("c0681344-b5a8-428f-a57c-98704e760170", u.toString());
        assertTrue(elapsedCached < elapsedNoCache);
        //Check another player
        u = MojangAPIHook.getUUID("PotatoSlayer9001");
        assertNotNull(u);
        assertEquals("f73058c9-54ae-4f44-b4db-78552cb56cef", u.toString());
    }

    @Test
    public void nameFromUUID() {
        long start = System.nanoTime();
        String name = MojangAPIHook.nameFromUUID(UUID.fromString("c0681344-b5a8-428f-a57c-98704e760170"));
        long elapsedNoCache = System.nanoTime() - start;
        assertNotNull(name);
        assertNotEquals("", name);
        assertEquals("CactusPuppy", name);
        //Hit Hook again to check for caching
        start = System.nanoTime();
        name = MojangAPIHook.nameFromUUID(UUID.fromString("c0681344-b5a8-428f-a57c-98704e760170"));
        long elapsedCached = System.nanoTime() - start;
        assertNotNull(name);
        assertNotEquals("", name);
        assertEquals("CactusPuppy", name);
        assertTrue(elapsedCached < elapsedNoCache);
    }

    @Test
    public void validUsername() {

    }
}