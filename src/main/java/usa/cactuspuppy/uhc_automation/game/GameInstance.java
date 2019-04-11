package usa.cactuspuppy.uhc_automation.game;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import usa.cactuspuppy.uhc_automation.game.entity.util.ScoreboardSet;
import usa.cactuspuppy.uhc_automation.game.tasks.timers.SuggestPack;
import usa.cactuspuppy.uhc_automation.utils.GameUtils;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public abstract class GameInstance implements Serializable {

    // [=== MAIN INFO ===]
    /**
     * Unique identifier for this game instance. This should be set once upon game creation and never changed until the game ends and/or is deleted.
     */
    @Setter(AccessLevel.PROTECTED)
    protected long gameID;

    /**
     * Marker of when the game is initiated (i.e. when players are spread) as given by {@code System.currentTimeMillis()}
     */
    @Setter(AccessLevel.PROTECTED)
    protected long initTime;

    /**
     * Marker of when the game begins (i.e. when players are released) as given by {@code System.currentTimeMillis()}
     */
    @Setter(AccessLevel.PROTECTED)
    protected long startTime;

    /**
     * Name for this game. Effort should be put into making this unique, but it is not enforced.
     */
    protected String name;

    /**
     * Unique ID of main game world as given by {@code World::getUID}.
     */
    @Setter(AccessLevel.NONE)
    @NonNull
    protected UUID mainWorldUID;

    public World getMainWorld() {
        World main = Bukkit.getWorld(mainWorldUID);
        if (main == null) {
            getUtils().log(Logger.Level.WARNING, this.getClass(), "Null main world UID, cannot resolve");
            return null;
        }
        return main;
    }

    /**
     * Other worlds that players may travel to during the course of the game without being considered as leaving the game. Examples include the main world's Nether and End dimension.
     */
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    protected Set<UUID> otherWorlds = new HashSet<>();

    public Set<UUID> getOtherWorlds() {
        return new HashSet<>(otherWorlds);
    }

    public void addOtherWorld(@NotNull World world) {
        otherWorlds.add(world.getUID());
    }

    @Setter(AccessLevel.NONE)
    protected GameState gameState = GameState.LOBBY;

    @Setter(AccessLevel.NONE)
    protected ScoreboardSet scoreboardSet = new ScoreboardSet(this);

    // [=== PLAYER INFO ===]
    /**
     * Set of UUIDs for players that have not yet been eliminated
     */
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<UUID> players = new HashSet<>();

    /**
     * Gets the set of players in the game who are not spectating
     * @return A copy of the set of active players
     */
    public Set<UUID> getAlivePlayers() {
        return new HashSet<>(players);
    }

    /**
     * Number of players at the start of the game
     */
    @Setter(AccessLevel.PROTECTED)
    protected int initNumPlayers;

    /**
     * Set of UUIDs for players that are spectating
     */
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<UUID> spectators = new HashSet<>();

    public Set<UUID> getSpectators() {
        return new HashSet<>(spectators);
    }

    public Set<UUID> getAllPlayers() {
        Set<UUID> rv = new HashSet<>(players);
        rv.addAll(spectators);
        return rv;
    }

    public void addPlayer(UUID uuid) {
        players.add(uuid);
        GameManager.registerPlayerGame(uuid, this);
        Player p = Bukkit.getPlayer(uuid);
        if (p == null) {
            return;
        }
        getUtils().broadcastChat(ChatColor.WHITE.toString() + ChatColor.BOLD + "[" + ChatColor.GOLD + "INFO" + ChatColor.WHITE + "] " + ChatColor.GREEN + p.getDisplayName() + ChatColor.WHITE + " has joined the game");
        if (!getAllPlayers().contains(uuid)) {
            new SuggestPack(this, uuid).init();
        }
    }

    public void addSpectator(UUID uuid) {
        spectators.add(uuid);
        GameManager.registerPlayerGame(uuid, this);
        Player p = Bukkit.getPlayer(uuid);
        if (p == null) {
            return;
        }
        getUtils().broadcastChat(ChatColor.WHITE.toString() + ChatColor.BOLD + "[" + ChatColor.GOLD + "INFO" + ChatColor.WHITE + "] " + ChatColor.GREEN + p.getDisplayName() + ChatColor.WHITE + " is now spectating");
        if (!getAllPlayers().contains(uuid)) {
            new SuggestPack(this, uuid).init();
        }
    }

    public void removePlayer(UUID uuid) {
        players.remove(uuid);
        spectators.remove(uuid);
        GameManager.unregisterPlayerGame(uuid);
    }

    public void removeSpectator(UUID uuid, boolean unreg) {
        spectators.remove(uuid);
        if (unreg) {
            GameManager.unregisterPlayerGame(uuid);
        }
    }

    /**
     * Moves an alive player to spectators. <br></br>
     * Will do nothing if the UUID is not an alive player
     * @param uuid UUID of player to move
     */
    public void moveAliveToSpec(UUID uuid) {
        if (!players.contains(uuid)) {
            return;
        }
        players.remove(uuid);
        spectators.add(uuid);
    }

    public GameInstance(@NotNull World world) {
        gameID = 0;
        gameID = GameManager.registerGame(this);
        name = "Game " + gameID;
        mainWorldUID = world.getUID();
        GameManager.registerWorldGame(world.getUID(), this);
        //TODO: Set defaults
    }

    /**
     * Sets the given world to be the new main world. Game must be in lobby mode for success.
     * @param world World to set as the new main world
     * @param keepOldWorld Whether to set the old main world as a linked world or drop it all together
     * @return False if main world was not set, otherwise true to indicate success.
     */
    public boolean setMainWorldUID(@NotNull World world, boolean keepOldWorld) {
        if (gameState != GameState.LOBBY) {
            getUtils().log(Logger.Level.INFO, this.getClass(), "Call to set main world for game " + name + " (ID " + gameID + ") while not in lobby mode, rejecting");
            return false;
        }
        if (keepOldWorld) {
            otherWorlds.add(mainWorldUID);
        } else {
            GameManager.unregisterWorldGame(mainWorldUID);
        }
        GameManager.registerWorldGame(world.getUID(), this);
        mainWorldUID = world.getUID();
        reset();
        return true;
    }

    /**
     * Updates the state of the game, calling the appropriate handlers.
     * Note that any RESET call should succeed regardless of GameState
     * @param e The game state event being requested
     * @return Whether any update to the game state occurred
     */
    public final boolean updateState(GameStateEvent e) {
        if (e == null) {
            getUtils().log(Logger.Level.WARNING, this.getClass(), "State update function was passed null");
            return false;
        }
        switch (e) {
            case RESET:
                reset();
                gameState = GameState.LOBBY;
                return true;
            case INIT:
                if (gameState == GameState.LOBBY) {
                    if (init()) {
                        gameState = GameState.INITIATING;
                        return true;
                    }
                }
                return false;
            case START:
                if (gameState == GameState.INITIATING) {
                    if (start()) {
                        gameState = GameState.ACTIVE;
                        return true;
                    }
                }
                return false;
            case PAUSE:
                if (gameState == GameState.ACTIVE) {
                    if (pause()) {
                        gameState = GameState.PAUSED;
                        return true;
                    }
                }
                return false;
            case RESUME:
                if (gameState == GameState.PAUSED) {
                    if (resume()) {
                        gameState = GameState.ACTIVE;
                        return true;
                    }
                }
                return false;
            case END:
                if (gameState == GameState.ACTIVE) {
                    if (end()) {
                        gameState = GameState.ENDED;
                        return true;
                    }
                }
                return false;
            default:
                getUtils().log(Logger.Level.WARNING, this.getClass(), "Invalid GameStateEvent passed to GameInstance #" + gameID
                        + "\nThis is probably a bug! Please report it to https://github.com/uhcmanager/uhcautomation");
                return false;
        }
    }

    /**
     * Reset the game worlds back to lobby, whether that be a clean generated world set or from any point within the game
     * @return Whether the reset was successful
     */
    protected abstract boolean reset();

    /**
     * Initiate the game, bringing the game out of lobby mode and doing pre-game tasks such as spreading players around the map and beginning the pregame countdown.
     * If desired, this method can directly call start if all game-start tasks can be called in the same tick.
     * @return Whether initiation was successful
     */
    protected abstract boolean init();

    /**
     * Start the game. This is the point at which players have full control.
     * @return Whether the start was successful
     */
    protected abstract boolean start();

    /**
     * Called if the game is interrupted by a server or plugin restart.
     * @return Whether the pause was successful
     */
    protected abstract boolean pause();

    /**
     * Called once the game is reactivated after a server or plugin restart.
     * @return Whether resuming was successful
     */
    protected abstract boolean resume();

    /**
     * Called when the game reaches a victory or game-end condition. Resets to lobby SHOULD NOT call this method.
     * @return Whether ending the game was successful
     */
    protected abstract boolean end();

    /**
     * Check if a victory condition has been met. <br>
     * Note that this does NOT trigger the {@link #end()} function
     * @return Whether a victory condition has been met
     */
    public boolean isVictory() {
        return getAlivePlayers().size() <= 1;
    }

    //Game utils
    protected GameUtils utils = new GameUtils(this);

    public GameUtils getUtils() {
        if (utils == null) {
            utils = new GameUtils(this);
        }
        return utils;
    }
}
