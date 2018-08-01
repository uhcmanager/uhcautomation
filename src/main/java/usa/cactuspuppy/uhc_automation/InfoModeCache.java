package usa.cactuspuppy.uhc_automation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InfoModeCache {
    private Map<UUID, InfoDisplayMode> cache;

    private static InfoModeCache instance;

    public InfoModeCache() {
        cache = new HashMap<>();
        instance = this;
    }

    public static boolean isInstanced() {
        return instance != null;
    }

    public static InfoModeCache getInstance() {
        if (!isInstanced()) {
            new InfoModeCache();
        }
        return instance;
    }

    public void storePlayerPref(UUID u, InfoDisplayMode pref) {
        cache.put(u, pref);
        SQLAPI.getInstance().enqueuePlayerUpdate(u);
    }

    public void addAllToCache(Map<UUID, InfoDisplayMode> prefs) {
        for (UUID u : prefs.keySet()) {
            cache.put(u, prefs.get(u));
        }
    }

    public InfoDisplayMode getPlayerPref(UUID u) {
        if (cache.get(u) == null) {
            return InfoDisplayMode.CHAT;
        }
        return cache.get(u);
    }

    public Map<UUID, InfoDisplayMode> getCache() {
        return cache;
    }
}
