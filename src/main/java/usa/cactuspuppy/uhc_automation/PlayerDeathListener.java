package usa.cactuspuppy.uhc_automation;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerDeathListener implements Listener {
    private Main m;

    public PlayerDeathListener(Main main) {
        m = main;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
//        m.getLogger().info(p.getDisplayName() + " died at [" + p.getLocation().getWorld().getName() + "] "
//                + p.getLocation().getX() + ", " + p.getLocation().getY() + ", " + p.getLocation().getZ());
        Location drops = p.getLocation();
        if (m.gi.getWorld().isGameRule("keepInventory")) {
            p.getInventory().clear();
        } else {
            for (ItemStack i : p.getInventory()) {
                drops.getWorld().dropItemNaturally(drops, i);
            }
        }
        (new DelayedPlayerRespawn(m, p, drops)).schedule();
        p.setGameMode(GameMode.SPECTATOR);
        m.gi.removePlayerFromLive(p);
        for (UUID u : m.gi.getAllPlayers()) {
            Player p1 = Bukkit.getPlayer(u);
            announceDeath(p, p1);
        }
        m.gi.checkForWin();
    }

    private void announceDeath(Player died, Player tell) {
        tell.sendTitle(died.getName(), ChatColor.RED + "has been eliminated!", 0, 80, 40);
        tell.playSound(tell.getLocation(), "minecraft:entity.wither.death", 1F, 1F);
        tell.playSound(tell.getLocation(), "minecraft:entity.wither.spawn", 1F, 1F);
    }
}
