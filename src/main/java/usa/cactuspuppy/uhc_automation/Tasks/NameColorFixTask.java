package usa.cactuspuppy.uhc_automation.Tasks;

import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scoreboard.Team;
import usa.cactuspuppy.uhc_automation.Main;

public class NameColorFixTask implements Runnable {
    private Main m;

    public NameColorFixTask(Main main) {
        m = main;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(p.getName());
            String prefix;
            if (team == null) {
                prefix = "";
            } else {
                prefix = team.getPrefix() + team.getColor();
            }
            if (p.getPlayerListName().equals(prefix + p.getName())) {
                return;
            }
            p.setPlayerListName(prefix + p.getName());
        }
        for (Team t : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
            t.setPrefix(t.getColor().toString());
        }
    }

    public void schedule() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(m, this, 0L, 2L);
        Bukkit.getServer().getPluginManager().registerEvents(new FixColoredNamesChatListener(), Main.getInstance());
    }

    @NoArgsConstructor
    public class FixColoredNamesChatListener implements Listener {

        /**
         * setFormat from https://github.com/quat1024/FixNameColors/releases/tag/v0.2
         * @param e chat event
         */
        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerChat(AsyncPlayerChatEvent e) {
            Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(e.getPlayer().getName());
            if (team == null) {
                System.out.println("No team found for player " + e.getPlayer().getName());
                return;
            }
            System.out.println("Player " + e.getPlayer().getName() + " has team " + team.getDisplayName());
            String prefix = team.getPrefix();
            String suffix = team.getSuffix();
            e.setFormat("<" + prefix + "%1$s" + suffix + ChatColor.RESET + "> %2$s");
        }
    }
}