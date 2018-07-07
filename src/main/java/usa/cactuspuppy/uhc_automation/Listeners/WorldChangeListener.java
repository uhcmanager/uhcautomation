package usa.cactuspuppy.uhc_automation.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

public class WorldChangeListener implements Listener {
    private Main m;

    public WorldChangeListener(Main main) {
        m = main;
    }
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if (e.getFrom().getWorld().equals(e.getTo().getWorld())) {
            return;
        }
        if (!UHCUtils.worldEqualsExt(e.getTo().getWorld(), m.gi.getWorld())) {
            e.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            m.gi.lostConnectPlayer(e.getPlayer());
        } else {
            m.gi.bindPlayertoScoreboard(e.getPlayer());
        }
    }
}
