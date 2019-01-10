package usa.cactuspuppy.uhc_automation.game.games;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

@Getter
public class GameInfo implements Serializable {
    //BASIC INFO
    /** Unique identifier for this game */
    @Setter(AccessLevel.MODULE) private long gameID;
    /** What type of game this is */
    @Setter(AccessLevel.PACKAGE) private String gameType;
    /** Game name */
    @Setter private String name;
    /** Display-friendly game name */
    @Setter private String displayName;
    /** Whether the game should include server downtime in time elapsed calculations */
    @Setter private boolean realTime = false;
    /** Long representing start time via System.currentTimeMillis */
    @Setter private long startTime;

    //TEAM INFO
    /** Whether the game should consider teams */
    @Setter private boolean teamMode = false;
    /** Whether teams should be split up and slowly group up */
    @Setter private boolean groupMode = false;
    /** How big each initial group should be */
    @Setter private int initialGroupSize = 1;

    @Setter private boolean respectTeams = true;

    //WORLD INFO
    /** Name of the main world */
    @Setter private String worldName;
    /** List of names of other worlds linked to this game */
    private Set<String> otherWorldNames = new HashSet<>();

    //EPISODE INFO
    /** Index of current episode */
    @Setter private int currEp;
    /** Episode duration, in seconds */
    @Setter private int episodeDuration;

    //PLAY AREA INFO
    /** Delay before border shrinking is initiated, in seconds. */
    @Setter private long shrinkDelay = 432000;
    /** Initial radius of play area, in blocks */
    @Setter private double initialRadius = 2000.5;

    @Setter private double finalRadius = 50.5;

    @Setter private double initialShrinkSpeed = 1.0;

    @Setter private boolean dynamicShrinkSpeed = true;

    @Setter private boolean dynamicShrinkTimer = true;
    /** Current speed at which the border is shrinking, in blocks per second, on one side. */
    @Setter private double currentShrinkSpeed;
    /**
     * Min distance from center of map
     */
    @Setter private double sepDistance = 50;

    //PLAYER INFO
    /** Set of currently living players, by UUID. Includes offline players. */
    private Set<UUID> alivePlayers = new HashSet<>();
    /** Set of players currently not participating in the game. Includes offline players. */
    private Set<UUID> spectators = new HashSet<>();

    public GameInfo(long id) {
        gameID = id;
        name = "Game " + id;
        displayName = name;
    }

    public Set<UUID> getAllPlayers() {
        Set<UUID> rv = new HashSet<>(alivePlayers);
        rv.addAll(spectators);
        return rv;
    }
}
