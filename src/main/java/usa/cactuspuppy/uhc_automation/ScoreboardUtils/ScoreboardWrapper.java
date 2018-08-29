package usa.cactuspuppy.uhc_automation.ScoreboardUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Scoreboard;

import java.io.Serializable;
import java.util.Set;

@Getter @AllArgsConstructor
public class ScoreboardWrapper implements Serializable {
    private Scoreboard scoreboard;
}
