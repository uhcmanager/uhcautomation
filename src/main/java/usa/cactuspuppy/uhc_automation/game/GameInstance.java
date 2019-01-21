package usa.cactuspuppy.uhc_automation.game;

import lombok.Getter;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Getter
public abstract class GameInstance implements Serializable {

    // [=== MAIN INFO ===]

    /**
     * Unique identifier for this game instance. This should be set once upon game creation and never changed until the game ends and/or is deleted.
     */
    private long gameID;

    /**
     * Marker of when the game begins (i.e. when players are released) as given by {@code System.currentTimeMillis()}
     */
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

    private Set<UUID> alivePlayers;

    private int initNumPlayers;

    private Set<UUID> spectators;

    private long epLength;
}
