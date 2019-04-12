package usa.cactuspuppy.uhc_automation.game;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class GameManager {
    private static HashMap<Long, GameInstance> activeGames = new HashMap<>();
    private static HashMap<UUID, Long> playerMap = new HashMap<>();
    private static HashMap<UUID, Long> worldMap = new HashMap<>();
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

    @Nullable
    public static GameInstance getPlayerGame(UUID u) {
        return getGameInstance(u, playerMap);
    }

    @Nullable
    public static GameInstance getWorldGame(UUID u) {
        return getGameInstance(u, worldMap);
    }

    public static boolean isNameActive(String gameName) {
        Set<GameInstance> activeInstances = new HashSet<>(activeGames.values());
        Set<String> names = activeInstances.stream().map(GameInstance::getName).collect(Collectors.toSet());
        return names.contains(gameName);
    }

    public static void reset() {
        activeGames.clear();
        playerMap.clear();
        worldMap.clear();
    }

    public static long registerGame(GameInstance instance) {
        long id = instance.getGameID();
        if (id == 0) { //Not registered, get new ID
            id = new IDGenerator().nextID();
        }
        if (id == -1) { //Couldn't get a new ID
            Logger.logSevere(GameManager.class, "Could not obtain an ID for Game: " + instance.getName());
            return 0;
        }
        instance.setGameID(id);
        activeGames.put(id, instance);
        return id;
    }

    /**
     * Registers player to game if game is registered
     *
     * @param u        UUID of player to register
     * @param instance Instance to register player to
     * @return Whether the player was registered to the game instance
     */
    static boolean registerPlayerGame(UUID u, GameInstance instance) {
        if (instance.getGameID() == 0) return false; //Game not registered
        playerMap.put(u, instance.getGameID());
        return true;
    }

    /**
     * Registers world to game if the game is registered
     *
     * @param u        UUID of world to register
     * @param instance Instance to register world to
     * @return Whether the world was registered to the game instance
     */
    static boolean registerWorldGame(UUID u, GameInstance instance) {
        if (instance.getGameID() == 0) return false; //Game not registered
        worldMap.put(u, instance.getGameID());
        return true;
    }

    /**
     * Unregisters a player from the specified game instance
     *
     * @param u UUID of player
     * @return Whether the player was unregistered successfully
     */
    static void unregisterPlayerGame(UUID u) {
        playerMap.remove(u);
    }

    /**
     * Unregisters a world from the specified game instance
     *
     * @param u UUID of world
     * @return Whether the world was unregistered successfully
     */
    static boolean unregisterWorldGame(UUID u) {
        GameInstance instance = activeGames.get(worldMap.get(u));
        if (instance != null && instance.getMainWorldUID().equals(u)) {
            return false;
        }
        worldMap.remove(u);
        return true;
    }

    /**
     * Unregister the game listed, optionally removing all players from the game
     *
     * @param instance Instance to unregsiter
     * @param shed     Whether to shed all players
     * @return
     */
    public static boolean unregisterGame(GameInstance instance, boolean shed) {
        if (!activeGames.values().contains(instance)) return false;
        long gameID = instance.getGameID();
        activeGames.remove(gameID);
        if (shed) {
            playerMap.keySet().removeAll(instance.getAllPlayers());
        }
        return true;
    }

    /**
     * Helper function to pull GameInstance from appropriate map
     *
     * @param u   UUID of player/world to pull
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
