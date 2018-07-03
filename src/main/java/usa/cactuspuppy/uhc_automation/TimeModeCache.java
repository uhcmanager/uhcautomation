package usa.cactuspuppy.uhc_automation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimeModeCache {
    private Map<UUID, TimeDisplayMode> cache;

    private static TimeModeCache instance;

    public TimeModeCache() {
        cache = new HashMap<>();
        instance = this;
    }

    public static boolean isInstanced() {
        return instance != null;
    }

    public static TimeModeCache getInstance() {
        if (!isInstanced()) {
            new TimeModeCache();
        }
        return instance;
    }

    public void storePlayerPref(UUID u, TimeDisplayMode pref) {
        cache.put(u, pref);
    }

    public void addAllToCache(Map<UUID, TimeDisplayMode> prefs) {
        for (UUID u : prefs.keySet()) {
            cache.put(u, prefs.get(u));
        }
    }

    public TimeDisplayMode getPlayerPref(UUID u) {
        return cache.get(u);
    }

    public Map<UUID, TimeDisplayMode> getCache() {
        return cache;
    }
}
