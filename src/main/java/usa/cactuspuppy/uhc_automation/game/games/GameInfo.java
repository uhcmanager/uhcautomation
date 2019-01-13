package usa.cactuspuppy.uhc_automation.game.games;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import usa.cactuspuppy.uhc_automation.game.GameManager;

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

    @Setter private boolean initiated = false;
    /** Long representing start time via System.currentTimeMillis */
    @Setter private long startTime = -1;

    //TEAM INFO
    /** Whether the game should consider teams */
    @Setter(AccessLevel.PROTECTED) private boolean teamMode = false;
    /** Whether teams should be split up and slowly group up */
    @Setter(AccessLevel.PROTECTED) private boolean groupMode = false;
    /** Whether teams should be respected on spreadplayers */
    @Setter private boolean respectTeams = true;

    //WORLD INFO
    /** UUID of the main world */
    private UUID worldID;
    /** List of UUIDs of other worlds linked to this game */
    private Set<UUID> otherWorldIDs = new HashSet<>();

    public void setWorldID(UUID u) {
        GameManager.unregisterWorld(worldID);
        worldID = u;
        GameManager.registerWorld(worldID, gameID);
    }

    public void addAltWorld(UUID u) {
        GameManager.registerWorld(u, gameID);
        otherWorldIDs.add(u);
    }

    public void removeAltWorld(UUID u) {
        GameManager.unregisterWorld(u);
        otherWorldIDs.remove(u);
    }

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

    public void addAlivePlayer(UUID u) {
        alivePlayers.add(u);
    }

    public void removeAlivePlayer(UUID u) { alivePlayers.remove(u); }

    public void addSpectator(UUID u) { spectators.add(u); }

    public void removeSpectator(UUID u) { spectators.remove(u); }

    public void removePlayer(UUID u) {
        removeAlivePlayer(u);
        removeSpectator(u);
    }

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
