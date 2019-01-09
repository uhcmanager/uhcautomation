package usa.cactuspuppy.uhc_automation.game;

import java.util.HashMap;
import java.util.Map;

public class GameManager {
    private static Map<Long, GameInstance> activeGames = new HashMap<>();

    public static void registerGame(GameInstance g) {
        activeGames.put(g.getGameInfo().getGameID(), g);
    }

    public static void unregisterGame(GameInstance g) {
        activeGames.remove(g.getGameInfo().getGameID(), g);
    }

    public static GameInstance getGame(long id) {
        return activeGames.get(id);
    }
}
