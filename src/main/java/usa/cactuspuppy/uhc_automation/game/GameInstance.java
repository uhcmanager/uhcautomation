package usa.cactuspuppy.uhc_automation.game;

import com.sun.istack.internal.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.World;
import usa.cactuspuppy.uhc_automation.entity.util.ScoreboardSet;
import usa.cactuspuppy.uhc_automation.utils.GameUtils;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.io.Serializable;
import java.util.*;

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
    protected UUID mainWorld;

    /**
     * Other worlds that players may travel to during the course of the game without being considered as leaving the game. Examples include the main world's Nether and End dimension.
     */
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    protected Set<UUID> otherWorlds;

    public Set<UUID> getOtherWorlds() {
        return new HashSet<>(otherWorlds);
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

    public GameInstance(World world) {
        if (world == null) {
            new GameUtils(this).log(Logger.Level.SEVERE, this.getClass(), "CONSTRUCTOR: Main world cannot be null");
            return;
        }
        gameID = 0;
        gameID = GameManager.registerGame(this);
        name = "Game " + gameID;
        mainWorld = world.getUID();
        GameManager.registerWorldGame(world.getUID(), this);
        //TODO: Set defaults
    }

    /**
     * Sets the given world to be the new main world. Game must be in lobby mode for success.
     * @param world World to set as the new main world
     * @param keepOldWorld Whether to set the old main world as a linked world or drop it all together
     * @return False if main world was not set, otherwise true to indicate success.
     */
    public boolean setMainWorld(World world, boolean keepOldWorld) {
        if (world == null) {
            new GameUtils(this).log(Logger.Level.SEVERE, this.getClass(), "CONSTRUCTOR: Main world cannot be null");
            return false;
        }
        if (gameState != GameState.LOBBY) {
            getUtils().log(Logger.Level.INFO, this.getClass(), "Call to set main world for game " + name + " (ID " + gameID + ") while not in lobby mode, rejecting");
            return false;
        }
        if (keepOldWorld) {
            otherWorlds.add(mainWorld);
        } else {
            GameManager.unregisterWorldGame(mainWorld);
        }
        mainWorld = world.getUID();
        reset();
        return true;
    }

    /**
     * Updates the state of the game, calling the appropriate handlers
     * @param e The game state event being requested
     * @return Whether any update to the game state occurred
     */
    public boolean updateState(GameStateEvent e) {
        if (e == null) {
            getUtils().log(Logger.Level.SEVERE, this.getClass(), "State update function was passed null");
        }
        switch (e) {
            case RESET:
                reset();
                return true;
            case INIT:
                if (gameState == GameState.LOBBY) {
                    init();
                    return true;
                }
                return false;
            case START:
                if (gameState == GameState.INITIATING) {
                    start();
                    return true;
                }
                return false;
            case PAUSE:
                if (gameState == GameState.ACTIVE) {
                    pause();
                    return true;
                }
                return false;
            case RESUME:
                if (gameState == GameState.PAUSED) {
                    resume();
                    return true;
                }
                return false;
            case END:
                if (gameState == GameState.ACTIVE) {
                    end();
                    return true;
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
     */
    protected abstract void reset();

    /**
     * Initiate the game, bringing the game out of lobby mode and doing pre-game tasks such as spreading players around the map and beginning the pregame countdown.
     * If desired, this method can directly call start if all game-start methods can be called in the same tick.
     */
    protected abstract void init();

    /**
     * Start the game. This is the point at which players have full control.
     */
    protected abstract void start();

    /**
     * Called if the game is interrupted by a server or plugin restart. Most games should not need to worry about this.
     */
    protected abstract void pause();

    /**
     * Called once the game is reactivated after a server or plugin restart. Most games should not need to worry about this.
     */
    protected abstract void resume();

    /**
     * Called when the game reaches a victory or game-end condition. Resets to lobby SHOULD NOT call this method.
     */
    protected abstract void end();

    //Game utils
    protected GameUtils utils = new GameUtils(this);

    public GameUtils getUtils() {
        if (utils == null) {
            utils = new GameUtils(this);
        }
        return utils;
    }
}
