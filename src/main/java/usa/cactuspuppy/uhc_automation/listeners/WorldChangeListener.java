package usa.cactuspuppy.uhc_automation.listeners;

import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import usa.cactuspuppy.uhc_automation.InfoDisplayMode;
import usa.cactuspuppy.uhc_automation.InfoModeCache;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.task.InfoAnnouncer;
import usa.cactuspuppy.uhc_automation.UHCUtils;

@NoArgsConstructor
public class WorldChangeListener implements Listener {
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if (e.getFrom().getWorld().equals(e.getTo().getWorld())) {
            return;
        }
        if ((UHCUtils.worldEqualsExt(e.getTo().getWorld(), Main.getInstance().getGameInstance().getWorld()) && Main.getInstance().getConfig().getBoolean("extended-world-detection", true)) || e.getTo().getWorld().equals(Main.getInstance().getGameInstance().getWorld())) {
            if (Main.getInstance().getGameInstance().getActivePlayers().contains(e.getPlayer().getUniqueId())) return;
            if (InfoModeCache.getInstance().getPlayerPref(e.getPlayer().getUniqueId()).equals(InfoDisplayMode.SCOREBOARD)) {
                e.getPlayer().setScoreboard(InfoAnnouncer.getInstance().getTimeScoreboard());
            } else {
                Main.getInstance().getGameInstance().bindPlayertoScoreboard(e.getPlayer());
            }
            Main.getInstance().getGameInstance().registerPlayer(e.getPlayer());
        } else {
            e.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            Main.getInstance().getGameInstance().lostConnectPlayer(e.getPlayer());
        }
    }
}
