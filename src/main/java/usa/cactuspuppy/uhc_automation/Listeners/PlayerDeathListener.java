package usa.cactuspuppy.uhc_automation.Listeners;

import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
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
        if (e.getKeepInventory()) {
            p.getInventory().clear();
        } else {
            for (ItemStack i : p.getInventory()) {
                if (i == null) continue;
                drops.getWorld().dropItemNaturally(drops, i);
            }
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
           p.spigot().respawn();
        }, 1L);
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
        e.setRespawnLocation(loc);
        e.getPlayer().setGameMode(GameMode.SPECTATOR);
    }

    private void announceDeath(Player died, Player tell) {
        assert died != null && tell != null;
        tell.sendTitle(died.getDisplayName(), ChatColor.RED + "has been eliminated!", 0, 80, 40);
        tell.playSound(tell.getLocation(), "minecraft:entity.wither.death", 0.5F, 1F);
    }
}
