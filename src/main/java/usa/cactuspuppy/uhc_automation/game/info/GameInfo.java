package usa.cactuspuppy.uhc_automation.game.info;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

@Getter
public class GameInfo implements Serializable {
    //BASIC INFO
    /** Unique identifier for this game */
    private long gameID;
    /** Game name */
    @Setter private String name;
    /** Display-friendly game name */
    @Setter private String displayName;
    /** Whether the game should include server downtime in time elapsed calculations */
    @Setter private boolean realTime;
    /** Long representing start time via System.currentTimeMillis */
    @Setter private long startTime;

    //WORLD INFO
    /** Name of the main world */
    @Setter private String worldName;
    /** List of names of other worlds linked to this game */
    private Set<String> otherWorldNames = new HashSet<>();

    //EPISODE INFO

    //PLAY AREA INFO
    /** Delay before border shrinking is initiated, in seconds. */
    @Setter private long shrinkDelay;

    @Setter private int initialSize;

    @Setter private int finalSize;

    @Setter private double initialShrinkSpeed;

    @Setter private boolean dynamicShrinkSpeed;

    @Setter private boolean dynamicShrinkTimer;
    /** Current speed at which the border is shrinking, in blocks per second, on one side. */
    @Setter private double currentShrinkSpeed;

    //PLAYER INFO
    /** Set of currently living players, by UUID. Includes offline players. */
    private Set<UUID> alivePlayers;
    /** Set of players currently not participating in the game. Includes offline players. */
    private Set<UUID> spectators;

    public GameInfo(long id) {
        gameID = id;
        name = "Game #" + id;
        displayName = name;
    }
}
