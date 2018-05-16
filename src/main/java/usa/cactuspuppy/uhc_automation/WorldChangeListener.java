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
        String to = e.getTo().getWorld().getName();
        String gameWorld = m.gi.getWorld().getName();
        String gameWorldNether = gameWorld + "_nether";
        String gameWorldEnd = gameWorld + "_the_end";
        if (!(to.equals(gameWorld) || to.equals(gameWorldNether) || to.equals(gameWorldEnd))) {
            m.gi.unRegisterPlayer(e.getPlayer());
        }
    }
}
