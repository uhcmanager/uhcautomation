package usa.cactuspuppy.uhc_automation.Listeners;

import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

@NoArgsConstructor
public class WorldChangeListener implements Listener {
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if (e.getFrom().getWorld().equals(e.getTo().getWorld())) {
            return;
        }
        if ((UHCUtils.worldEqualsExt(e.getTo().getWorld(), Main.getInstance().getGameInstance().getWorld()) && Main.getInstance().getConfig().getBoolean("extended-world-detection", true)) || e.getTo().getWorld().equals(Main.getInstance().getGameInstance().getWorld())) {
            Main.getInstance().getGameInstance().bindPlayertoScoreboard(e.getPlayer());
            Main.getInstance().getGameInstance().registerPlayer(e.getPlayer());
        } else {
            e.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            Main.getInstance().getGameInstance().lostConnectPlayer(e.getPlayer());
        }
    }
}
