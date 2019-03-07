package usa.cactuspuppy.uhc_automation.game;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class GameManager {
    private static Map<Long, GameInstance> activeGames = new HashMap<>();
    private static Map<UUID, Long> playerMap = new HashMap<>();
    private static Map<UUID, Long> worldMap = new HashMap<>();
    private static final int MAX_ID_ATTEMPTS = 10000;

    /**
     * @return a copy of the current list of active games
     */
    public static HashMap<Long, GameInstance> getActiveGames() {
        return new HashMap<>(activeGames);
    }

    public static GameInstance getGame(long id) {
        return activeGames.get(id);
    }

    public static GameInstance getPlayerGame(UUID u) {
        return getGameInstance(u, playerMap);
    }

    public static GameInstance getWorldGame(UUID u) {
        return getGameInstance(u, worldMap);
    }

    public static long registerGame(GameInstance instance) {
        long id = instance.getGameID();
        if (id == 0) { //Not registered, get new ID
            id = new IDGenerator().nextID();
        }
        if (id == -1) { //Couldn't get a new ID
            Logger.logError(GameManager.class, "Could not obtain an ID for Game: " + instance.getName());
            return 0;
        }
        instance.setGameID(id);
        activeGames.put(id, instance);
        return id;
    }

    /**
     * Registers player to game if game is registered
     * @param u UUID of player to register
     * @param instance Instance to register player to
     * @return Whether the player was registered to the game instance
     */
    public static boolean registerPlayerGame(UUID u, GameInstance instance) {
        if (instance.getGameID() == 0) return false; //Game not registered
        playerMap.put(u, instance.getGameID());
        return true;
    }

    /**
     * Registers world to game if the game is registered
     * @param u UUID of world to register
     * @param instance Instance to register world to
     * @return Whether the world was registered to the game instance
     */
    public static boolean registerWorldGame(UUID u, GameInstance instance) {
        if (instance.getGameID() == 0) return false; //Game not registered
        worldMap.put(u, instance.getGameID());
        return true;
    }

    /**
     * Unregisters a player from the specified game instance
     * @param u UUID of player
     * @param instance GameInstance to unregister from
     * @return Whether the player was unregistered successfully
     */
    public static boolean unregisterPlayerGame(UUID u, GameInstance instance) {
        return playerMap.remove(u, instance.getGameID());
    }

    /**
     * Unregisters a world from the specified game instance
     * @param u UUID of world
     * @param instance GameInstance to unregister from
     * @return Whether the world was unregistered successfully
     */
    public static boolean unregisterWorldGame(UUID u, GameInstance instance) {
        if (instance.getMainWorld().equals(u)) {
            return false;
        }
        return worldMap.remove(u, instance.getGameID());
    }

    /**
     * Helper function to pull GameInstance from appropriate map
     * @param u UUID of player/world to pull
     * @param map Corresponding map of world/players
     * @return GameInstance of active game, or null if no active game instance exists
     */
    private static GameInstance getGameInstance(UUID u, Map<UUID, Long> map) {
        if (!map.containsKey(u)) return null;
        long gameID = map.get(u);
        if (!activeGames.containsKey(gameID)) {
            map.remove(u);
            return null;
        }
        return activeGames.get(gameID);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class IDGenerator {
        private Random rng = new Random();

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
