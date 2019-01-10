package usa.cactuspuppy.uhc_automation.game;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameManager {
    private static Map<Long, GameInstance> activeGames = new HashMap<>();
    private static Map<UUID, Long> playerGameIDMap = new HashMap<>();

    public static void registerGame(GameInstance g) {
        activeGames.put(g.getGameInfo().getGameID(), g);
        for (UUID u : g.getGameInfo().getAlivePlayers()) playerJoinGame(u, g.getGameInfo().getGameID());
        for (UUID u : g.getGameInfo().getSpectators()) playerJoinGame(u, g.getGameInfo().getGameID());
    }

    public static Map<Long, GameInstance> getActiveGames() {
        return new HashMap<>(activeGames);
    }

    public static void unregisterGame(GameInstance g) {
        activeGames.remove(g.getGameInfo().getGameID(), g);
        for (UUID u : g.getGameInfo().getAlivePlayers()) playerLeaveGame(u);
        for (UUID u : g.getGameInfo().getSpectators()) playerLeaveGame(u);
    }

    public static GameInstance getGame(long id) {
        return activeGames.get(id);
    }

    public static void playerJoinGame(UUID u, long gameID) {
        playerGameIDMap.put(u, gameID);
    }

    public static void playerLeaveGame(UUID u) {
        playerGameIDMap.remove(u);
    }

    public static GameInstance getPlayerGame(UUID u) {
        return getGame(playerGameIDMap.get(u));
    }
}
