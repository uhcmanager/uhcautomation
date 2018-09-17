package usa.cactuspuppy.uhc_automation.Tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import usa.cactuspuppy.uhc_automation.Main;

public class FixTabNameTask implements Runnable {
    private Main m;

    public FixTabNameTask(Main main) {
        m = main;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(p.getName());
            String prefix;
            if (team == null) {
                prefix = "";
            } else {
                prefix = team.getPrefix();
            }
            if (p.getPlayerListName().equals(prefix + p.getName())) {
                return;
            }
            p.setPlayerListName(prefix + p.getName());
        }
    }

    public void schedule() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(m, this, 0L, 2L);
    }
}
