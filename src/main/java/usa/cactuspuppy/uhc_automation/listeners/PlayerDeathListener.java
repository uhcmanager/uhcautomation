package usa.cactuspuppy.uhc_automation.listeners;

import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
public class PlayerDeathListener implements Listener {
    private static Map<UUID, Location> respawnQueue = new HashMap<>();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        String oldMsg = e.getDeathMessage();
        Player p = e.getEntity();
        if (!(Main.getInstance().getGameInstance().getLivePlayers().contains(p.getUniqueId())) && Main.getInstance().getGameInstance().isActive()) {
            return;
        }
        //Wolf handling
        if (e.getEntity().getLastDamageCause().getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
            try {
                EntityDamageByEntityEvent e1 = (EntityDamageByEntityEvent) e.getEntity().getLastDamageCause();
                if (e1.getDamager().getType().equals(EntityType.WOLF) && ((Wolf) e1.getDamager()).getOwner() != null) {
                    Player owner = (Player) ((Wolf)e1.getDamager()).getOwner();
                    oldMsg = p.getDisplayName() + " was slain by " + owner.getDisplayName() + "'s Dog";
                }
            } catch (ClassCastException ex) {
                UHCUtils.logBug(Main.getInstance().getGameInstance(), "Death handler entity get exception");
            }
        }
        if (!Main.getInstance().getGameInstance().isActive() && Main.getInstance().getGameInstance().getActivePlayers().contains(p.getUniqueId())) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                p.spigot().respawn();
                p.teleport(new Location(Main.getInstance().getGameInstance().getWorld(), 0, 254, 0));
            }, 1L);
            return;
        }
        e.setDeathMessage("[" + ChatColor.RED.toString() + ChatColor.BOLD + "DEATH" + ChatColor.RESET + "] " + oldMsg);
        Main.getInstance().getLogger().info(p.getName() + " died at [" + p.getLocation().getWorld().getName() + "] "
                + p.getLocation().getX() + ", " + p.getLocation().getY() + ", " + p.getLocation().getZ());
        Location drops = p.getLocation();
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> p.spigot().respawn(), 1L);
        respawnQueue.put(p.getUniqueId(), drops);
        for (UUID u : Main.getInstance().getGameInstance().getActivePlayers()) {
            Player p1 = Bukkit.getPlayer(u);
            announceDeath(p, p1);
        }
        UHCUtils.saveWorldPlayers(Main.getInstance());
        Main.getInstance().getGameInstance().removePlayerFromLive(p);
        Main.getInstance().getGameInstance().checkForWin();
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Location loc = respawnQueue.get(e.getPlayer().getUniqueId());
        if (loc == null) { return; }
        respawnQueue.remove(e.getPlayer().getUniqueId());
        e.setRespawnLocation(loc);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> e.getPlayer().setGameMode(GameMode.SPECTATOR), 1L);
    }

    private void announceDeath(Player died, Player tell) {
        assert died != null && tell != null;
        tell.sendTitle(died.getDisplayName(), ChatColor.RED + "has been eliminated!", 0, 80, 40);
        tell.playSound(tell.getLocation(), "minecraft:entity.wither.death", 0.5F, 1F);
    }
}
