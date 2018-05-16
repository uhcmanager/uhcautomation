package usa.cactuspuppy.uhc_automation;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

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
    }
}
