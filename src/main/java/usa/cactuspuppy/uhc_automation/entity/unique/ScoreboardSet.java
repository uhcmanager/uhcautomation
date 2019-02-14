package usa.cactuspuppy.uhc_automation.entity.unique;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;

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
        scoreboards.put(playerUid, newScoreboard);
        return newScoreboard;
    }

    public Scoreboard getPlayerScoreboard(UUID playerUid) {
        return scoreboards.get(playerUid);
    }
}
