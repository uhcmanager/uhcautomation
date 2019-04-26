package usa.cactuspuppy.uhc_automation.game.tasks.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.utils.Logger;

public class PVPCanceller extends ListenerTask {
    public PVPCanceller(GameInstance gameInstance) {
        super(gameInstance);
    }

    @EventHandler
    public void onPVP(EntityDamageByEntityEvent e) {
        //Check that this is PVP
        if (!e.getEntity().getType().equals(EntityType.PLAYER) || !e.getDamager().getType().equals(EntityType.PLAYER)) {
            return;
        }
        //Check that both players are in the game
        Player victim;
        Player damager;
        try {
            victim = (Player) e.getEntity();
            damager = (Player) e.getDamager();
        } catch (ClassCastException ex) {
            getGameInstance().getUtils().log(Logger.Level.SEVERE, this.getClass(), "Class cast exception under assumption of players", ex);
            return;
        }
        if (!getGameInstance().getAlivePlayers().contains(victim.getUniqueId()) || !getGameInstance().getAlivePlayers().contains(damager.getUniqueId())) {
            return;
        }
        //Cancel
        e.setCancelled(true);
    }
}
