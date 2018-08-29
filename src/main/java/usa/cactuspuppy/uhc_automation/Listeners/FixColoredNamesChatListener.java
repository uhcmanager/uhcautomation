package usa.cactuspuppy.uhc_automation.Listeners;

import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scoreboard.Team;
import usa.cactuspuppy.uhc_automation.Main;

@NoArgsConstructor
public class FixColoredNamesChatListener implements Listener {

    /**
     * setFormat from https://github.com/quat1024/FixNameColors/releases/tag/v0.2
     * @param e chat event
     */
    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Team team = Main.getInstance().getGameInstance().getScoreboard().getEntryTeam(e.getPlayer().getName());
        if (team == null) {
            return;
        }
        String prefix = team.getPrefix();
        String suffix = team.getSuffix();
        e.setFormat("<" + prefix + "%1$s" + suffix + ChatColor.RESET + "> %2$s");
    }
}
