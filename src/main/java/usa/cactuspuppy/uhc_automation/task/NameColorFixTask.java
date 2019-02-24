package usa.cactuspuppy.uhc_automation.task;

import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scoreboard.Scoreboard;
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
        InfoAnnouncer inst = InfoAnnouncer.getInstance();
        if (inst != null) {
            Scoreboard timeBoard = inst.getTimeScoreboard();
            for (Team t : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
                Team mirror = timeBoard.getTeam(t.getName());
                if (mirror == null) {
                    mirror = timeBoard.registerNewTeam(t.getName());
                    copyTeam(mirror, t);
                }
            }
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

    private void copyTeam(Team dest, Team source) {
        dest.setAllowFriendlyFire(source.allowFriendlyFire());
        dest.setCanSeeFriendlyInvisibles(source.canSeeFriendlyInvisibles());
        dest.setColor(source.getColor());
        dest.setDisplayName(source.getName());
        dest.setPrefix(source.getPrefix());
        dest.setSuffix(source.getSuffix());
        for (String s : source.getEntries()) {
            if (dest.hasEntry(s)) {
                continue;
            }
            dest.addEntry(s);
        }
    }

    public void schedule() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(m, this, 0L, 20L);
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

        @EventHandler
        public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
            String message = e.getMessage();
            Bukkit.getLogger().info(e.getPlayer().getName() + " executed command '" + message + "'");
        }
    }
}
