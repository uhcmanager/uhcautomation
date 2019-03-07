package usa.cactuspuppy.uhc_automation.game;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.World;
import usa.cactuspuppy.uhc_automation.entity.util.ScoreboardSet;
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
    @Setter(AccessLevel.PACKAGE)
    protected long gameID;

    /**
     * Marker of when the game is initiated (i.e. when players are spread) as given by {@code System.currentTimeMillis()}
     */
    @Setter(AccessLevel.PACKAGE)
    protected long initTime;

    /**
     * Marker of when the game begins (i.e. when players are released) as given by {@code System.currentTimeMillis()}
     */
    @Setter(AccessLevel.PACKAGE)
    protected long startTime;

    /**
     * Name for this game. Effort should be put into making this unique, but it is not enforced.
     */
    protected String name;

    /**
     * Unique ID of main game world as given by {@code World::getUID}.
     */
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
    protected GameState gameState;

    @Setter(AccessLevel.NONE)
    protected ScoreboardSet scoreboardSet = new ScoreboardSet(this);

    // [=== PLAYER INFO ===]
    /**
     * Set of UUIDs for players that have not yet been eliminated
     */
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<UUID> players;

    public Set<UUID> getPlayers() {
        return new HashSet<>(players);
    }

    /**
     * Number of players at the start of the game
     */
    @Setter(AccessLevel.PACKAGE)
    private int initNumPlayers;

    /**
     * Set of UUIDs for players that are spectating
     */
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<UUID> spectators;

    public Set<UUID> getSpectators() {
        return new HashSet<>(spectators);
    }

    public Set<UUID> getAllPlayers() {
        Set<UUID> rv = new HashSet<>(players);
        rv.addAll(spectators);
        return rv;
    }

    // [=== EPISODE INFO ===]
    /**
     * Length of episodes, in seconds
     */
    private long epLength;

    public GameInstance(World world) {
        gameID = 0;
        gameID = GameManager.registerGame(this);
        name = "Game " + gameID;
        mainWorld = world.getUID();
        GameManager.registerWorldGame(world.getUID(), this);
    }

    /**
     * Updates the state of the game, calling the appropriate handlers
     * @param e The game state event being requested
     * @return Whether any update to the game state occurred
     */
    public boolean updateState(GameStateEvent e) {
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
                Logger.logWarning(this.getClass(), "Invalid GameStateEvent passed to GameInstance #" + gameID);
                return false;
        }
    }

    protected abstract void reset();

    protected abstract void init();

    protected abstract void start();

    protected abstract void pause();

    protected abstract void resume();

    protected abstract void end();
}
