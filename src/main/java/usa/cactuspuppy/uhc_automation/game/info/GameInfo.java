package usa.cactuspuppy.uhc_automation.game.info;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.WorldBorder;

import java.io.Serializable;
import java.util.*;

@Getter
public class GameInfo implements Serializable {
    //BASIC INFO
    /** Unique identifier for this game */
    @Setter(AccessLevel.MODULE) private long gameID;
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
    @Setter private boolean teamMode;
    /** Whether teams should be split up and slowly group up */
    @Setter private boolean groupMode;

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
    @Setter private long shrinkDelay;
    /** Initial radius of play area, in blocks */
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
