package usa.cactuspuppy.uhc_automation.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scoreboard.Team;
import usa.cactuspuppy.uhc_automation.Main;

public class FixColoredNamesChatListener implements Listener {
    private Main main;

    public FixColoredNamesChatListener(Main m) {
        main = m;
    }

    /**
     * setFormat from https://github.com/quat1024/FixNameColors/releases/tag/v0.2
     * @param e chat event
     */
    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(e.getPlayer());
        if (team == null) {
            return;
        }
        String prefix = team.getPrefix();
        String suffix = team.getSuffix();
        e.setFormat("<" + prefix + "%1$s" + suffix + "> %2$s");
    }
}
