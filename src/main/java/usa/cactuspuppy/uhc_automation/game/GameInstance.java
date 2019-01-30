package usa.cactuspuppy.uhc_automation.game;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.World;

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
    private long gameID;

    /**
     * Marker of when the game is initiated (i.e. when players are spread) as given by {@code System.currentTimeMillis()}
     */
    @Setter(AccessLevel.PACKAGE)
    private long initTime;

    /**
     * Marker of when the game begins (i.e. when players are released) as given by {@code System.currentTimeMillis()}
     */
    @Setter(AccessLevel.PACKAGE)
    private long startTime;

    /**
     * Name for this game. May or may not be unique; should not be used to identify this game.
     */
    private String name;

    /**
     * Unique ID of main game world as given by {@code World::getUID}.
     */
    private UUID mainWorld;

    /**
     * Other worlds that players may travel to during the course of the game without being considered as leaving the game. Examples include the main world's Nether and End dimension.
     */
    private Set<UUID> otherWorlds;

    // [=== PLAYER INFO ===]
    /**
     * Set of UUIDs for players that have not yet been eliminated
     */
    @Setter(AccessLevel.NONE)
    private Set<UUID> players;

    /**
     * Number of players at the start of the game
     */
    @Setter(AccessLevel.PACKAGE)
    private int initNumPlayers;

    /**
     * Set of UUIDs for players that are spectating
     */
    @Setter(AccessLevel.NONE)
    private Set<UUID> spectators;

    // [=== EPISODE INFO ===]
    /**
     * Length of episodes, in seconds
     */
    private long epLength;

    public GameInstance(String name, World world) {
        this.name = name;
        gameID = 0;
        gameID = GameManager.registerGame(this);
        mainWorld = world.getUID();
    }

    public Set<UUID> getAllPlayers() {
        Set<UUID> rv = new HashSet<>(players);
        rv.addAll(spectators);
        return rv;
    }

    public abstract boolean init();

    public abstract boolean start();

    public abstract boolean pause();

    public abstract boolean win();

    public abstract boolean stop();
}
