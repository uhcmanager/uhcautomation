package usa.cactuspuppy.uhc_automation.game.tasks.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import usa.cactuspuppy.uhc_automation.game.GameStateEvent;
import usa.cactuspuppy.uhc_automation.game.tasks.timers.UHC_BorderTask;
import usa.cactuspuppy.uhc_automation.game.types.UHC;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UHC_ActiveListener extends ListenerTask {
    private UHC uhc;
    private UHC_BorderTask borderTask;
    private Map<UUID, Location> respawnQueue = new HashMap<>();

    public UHC_ActiveListener(UHC uhcInstance, UHC_BorderTask borderTask) {
        super(uhcInstance);
        uhc = uhcInstance;
        this.borderTask = borderTask;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (!gameInstance.getAlivePlayers().contains(e.getEntity().getUniqueId())) {
            return;
        }
        String currMsg = e.getDeathMessage();
        //Information about dog attacks
        if (e.getEntity().getLastDamageCause() != null && e.getEntity().getLastDamageCause().getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
            try {
                EntityDamageByEntityEvent e1 = (EntityDamageByEntityEvent) e.getEntity().getLastDamageCause();
                if (e1.getDamager().getType().equals(EntityType.WOLF) && ((Wolf) e1.getDamager()).getOwner() != null) {
                    Player owner = (Player) ((Wolf)e1.getDamager()).getOwner();
                    if (owner != null) {
                        currMsg = e.getEntity().getDisplayName() + " was slain by " + owner.getDisplayName() + "'s Dog";
                    }
                }
            } catch (ClassCastException ex) {
                gameInstance.getUtils().log(Logger.Level.WARNING, this.getClass(), "Dog detection class cast exception");
            }
        }
        //Speed up border if should do so
        if (uhc.isDynamicSpeed()) {
            borderTask.speedUp();
        }
        //Set new death message
        e.setDeathMessage("[" + ChatColor.RED + "DEATH" + ChatColor.RESET + "] " + currMsg);
        //Force respawn
        e.getEntity().spigot().respawn();
        //Add to respawn queue
        Location died = e.getEntity().getLocation();
        respawnQueue.put(e.getEntity().getUniqueId(), died);
        //Log death
        gameInstance.getUtils().log(Logger.Level.INFO, this.getClass(), e.getEntity().getName() + " died at " + died.toString());
        //Move to spectators
        gameInstance.moveAliveToSpec(e.getEntity().getUniqueId());
        //Announce death
        gameInstance.getUtils().broadcastSoundTitle(Sound.ENTITY_WITHER_DEATH, 1F, e.getEntity().getDisplayName(), ChatColor.RED + "has been eliminated!", 0, 80, 40);
        //Check for win
        if (uhc.getAlivePlayers().size() == 1) {
            uhc.updateState(GameStateEvent.END);
            //TODO: Add victory screech etc.
        }
    }
}
