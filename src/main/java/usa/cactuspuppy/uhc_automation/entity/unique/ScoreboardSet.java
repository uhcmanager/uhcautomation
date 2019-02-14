package usa.cactuspuppy.uhc_automation.entity.unique;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents the set of scoreboards (one per player) for a GameInstance
 */
public class ScoreboardSet {
    private Map<UUID, Scoreboard> scoreboards = new HashMap<>();

    public Scoreboard addPlayer(UUID playerUid) {
        Scoreboard exists =  getPlayerScoreboard(playerUid);
        if (exists != null) {
            return exists;
        }
        Scoreboard newScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        if (newScoreboard == null) {
            Logger.logError(this.getClass(), "", new RuntimeException("Failed to get new scoreboard from Bukkit"));
        }
        scoreboards.put(playerUid, newScoreboard);
        return newScoreboard;
    }

    public Scoreboard getPlayerScoreboard(UUID playerUid) {
        return scoreboards.get(playerUid);
    }
}
