package usa.cactuspuppy.uhc_automation.task;

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

import java.util.UUID;

public class NameColorFixTask implements Runnable {
    private Main m;

    public NameColorFixTask(Main main) {
        m = main;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void run() {
        for (Team t : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
            t.setPrefix(t.getColor().toString());
        }
        for (UUID u : Main.getInstance().getGameInstance().getActivePlayers()) {
            Player p = Bukkit.getPlayer(u);
            Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(p.getName());
            String prefix;
            ChatColor color;
            if (team == null) {
                prefix = "";
                color = ChatColor.WHITE;
            } else {
                prefix = team.getPrefix() + team.getColor();
                color = team.getColor();
            }
            if (p.getPlayerListName().equals(prefix + p.getName())) {
                return;
            }
            p.setPlayerListName(prefix + color + p.getName());
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
            if (team == null) return;
            ChatColor color = team.getColor();
            String prefix = team.getPrefix();
            String suffix = team.getSuffix();
            e.setFormat("<" + prefix + color + "%1$s" + suffix + ChatColor.RESET + "> %2$s");
        }
    }
}
