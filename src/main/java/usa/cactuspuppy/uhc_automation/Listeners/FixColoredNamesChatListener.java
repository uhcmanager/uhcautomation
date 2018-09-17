package usa.cactuspuppy.uhc_automation.Listeners;

import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scoreboard.Team;

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
        ChatColor color = team.getColor();
        String prefix = team.getPrefix();
        String suffix = team.getSuffix();
        e.setFormat("<" + prefix + color + "%1$s" + suffix + ChatColor.RESET + "> %2$s");
    }
}
