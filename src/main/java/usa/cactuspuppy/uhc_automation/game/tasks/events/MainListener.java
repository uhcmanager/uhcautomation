package usa.cactuspuppy.uhc_automation.game.tasks.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import usa.cactuspuppy.uhc_automation.game.GameManager;

public class MainListener implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        PlayerFreezer.onPlayerMove(e);
    }

    @EventHandler
    public void onPlayerConnect(PlayerJoinEvent e) {
        PlayerManager.onPlayerJoin(e);
    }

    @EventHandler
    public void worldChange(PlayerTeleportEvent e) {
        if (e.getFrom().getWorld().equals(e.getTo().getWorld())) return;
        PlayerManager.onWorldChange(e, e.getFrom().getWorld(), e.getTo().getWorld());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (GameManager.getPlayerGame(e.getEntity().getUniqueId()) == null) return;
    }
}
