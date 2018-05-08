package usa.cactuspuppy.uhc_automation;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class WorldChangeListener implements Listener {
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        World from = e.getFrom().getWorld();
        World to = e.getTo().getWorld();
        if (!from.equals(to)) {
            //Player changed worlds
            e.getPlayer().sendRawMessage("You changed worlds!");
        }
    }
}
