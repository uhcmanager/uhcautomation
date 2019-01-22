package usa.cactuspuppy.uhc_automation.game;

import lombok.AllArgsConstructor;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameManager {
    private static Map<Long, GameInstance> activeGames = new HashMap<>();
    private static final int MAX_ID_ATTEMPTS = 10000;

    public static HashMap<Long, GameInstance> getActiveGames() {
        return new HashMap<>(activeGames);
    }

    public static GameInstance getGame(long id) {
        return activeGames.get(id);
    }

    public static long registerGame(GameInstance instance) {
        if (instance.getGameID() != 0) return instance.getGameID();
        long id = new IDGenerator(new Random()).nextID();
        if (id != -1) activeGames.put(id, instance);
        return id;
    }

    @AllArgsConstructor
    public static class IDGenerator {
        private Random rng;

        long nextID() {
            for (int i = 0; i < MAX_ID_ATTEMPTS; i++) {
                long attempt = rng.nextLong();
                if (activeGames.keySet().contains(attempt)) continue;
                return attempt;
            }
            Logger.logWarning(this.getClass(), "Exceeded max generation attempts while generating new game ID");
            return -1;
        }
    }
}
