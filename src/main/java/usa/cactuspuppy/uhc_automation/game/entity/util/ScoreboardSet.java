package usa.cactuspuppy.uhc_automation.game.entity.util;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.entity.unique.UHCTeam;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.io.Serializable;
import java.util.*;

/**
 * Represents the set of scoreboards (one per player) for a GameInstance
 */
public class ScoreboardSet implements Serializable {
    @Getter
    private GameInstance parent;
    private transient Map<UUID, Scoreboard> scoreboards = new HashMap<>();
    private Set<UHCTeam> teams = new HashSet<>();
    private InfoObjective infoObjective;

    public ScoreboardSet(GameInstance parent) {
        this.parent = parent;
    }

    public Scoreboard addPlayer(UUID playerUid) {
        Scoreboard exists = getPlayerScoreboard(playerUid);
        if (exists != null) {
            return exists;
        }
        Scoreboard newScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        if (newScoreboard == null) {
            Logger.logSevere(this.getClass(), "", new RuntimeException("Failed to get new scoreboard from Bukkit"));
            return null;
        }
        scoreboards.put(playerUid, newScoreboard);
        //TODO: Bring scoreboard set up to speed
        return newScoreboard;
    }

    public Scoreboard getPlayerScoreboard(UUID playerUid) {
        if (!scoreboards.containsKey(playerUid)) {
            return addPlayer(playerUid);
        }
        return scoreboards.get(playerUid);
    }

    public Set<Scoreboard> getAllScoreboards() {
        return new HashSet<>(scoreboards.values());
    }
}
