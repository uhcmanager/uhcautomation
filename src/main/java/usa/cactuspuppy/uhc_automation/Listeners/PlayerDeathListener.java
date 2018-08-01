package usa.cactuspuppy.uhc_automation.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.Tasks.DelayedPlayerRespawn;
import usa.cactuspuppy.uhc_automation.UHCUtils;

import java.util.UUID;

public class PlayerDeathListener implements Listener {
    private Main m;

    public PlayerDeathListener(Main main) {
        m = main;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        String oldMsg = e.getDeathMessage();
        e.setDeathMessage(ChatColor.RED.toString() + ChatColor.BOLD + "[DEATH] " + ChatColor.RESET + oldMsg);
        Player p = e.getEntity();
        if (!(m.gi.getLivePlayers().contains(p.getUniqueId())) && m.gi.isActive()) {
            return;
        }
        if (!m.gi.isActive() && m.gi.getActivePlayers().contains(p.getUniqueId())) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(m, () -> {
                p.spigot().respawn();
                p.teleport(new Location(m.gi.getWorld(), 0, 254, 0));
            }, 1L);
            return;
        }
        m.getLogger().info(p.getName() + " died at [" + p.getLocation().getWorld().getName() + "] "
                + p.getLocation().getX() + ", " + p.getLocation().getY() + ", " + p.getLocation().getZ());
        Location drops = p.getLocation();
        if (e.getKeepInventory()) {
            p.getInventory().clear();
        } else {
            for (ItemStack i : p.getInventory()) {
                drops.getWorld().dropItemNaturally(drops, i);
            }
        }
        (new DelayedPlayerRespawn(m, p, drops)).schedule();
        p.setGameMode(GameMode.SPECTATOR);
        m.gi.removePlayerFromLive(p);
        for (UUID u : m.gi.getActivePlayers()) {
            try {
                Player p1 = Bukkit.getPlayer(u);
                announceDeath(p, p1);
            } catch (NullPointerException f) { }
        }
        UHCUtils.saveWorldPlayers(m);
        m.gi.checkForWin();
    }

    private void announceDeath(Player died, Player tell) {
        tell.sendTitle(died.getDisplayName(), ChatColor.RED + "has been eliminated!", 0, 80, 40);
        tell.playSound(tell.getLocation(), "minecraft:entity.wither.death", 0.5F, 1F);
    }
}
