package usa.cactuspuppy.uhc_automation;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

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
        if (UHCUtils.worldEqualsExt(e.getTo().getWorld(), m.gi.getWorld())) {
            m.gi.unRegisterPlayer(e.getPlayer());
        }
    }
}
