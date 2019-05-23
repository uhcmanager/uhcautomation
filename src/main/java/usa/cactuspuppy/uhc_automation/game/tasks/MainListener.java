package usa.cactuspuppy.uhc_automation.game.tasks;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import usa.cactuspuppy.uhc_automation.event.game.player.PlayerJoinEvent;

import java.util.Objects;

public class MainListener implements Listener {
    //TODO: Implement join listening and world switch listening

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        //TODO: Handle player connection
    }

    @EventHandler
    public void onPlayerWorldSwitch(PlayerTeleportEvent event) {
        if (event.getTo() == null || Objects.equals(event.getFrom().getWorld(), event.getTo().getWorld())) {
            return;
        }

        World to = event.getTo().getWorld();
        World from = event.getFrom().getWorld();
    }
}
