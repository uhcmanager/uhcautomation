package usa.cactuspuppy.uhc_automation;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDeathListener implements Listener {
    private Main m;

    public PlayerDeathListener(Main main) {
        m = main;
    }

    @EventHandler
    public void onPlayerDeath(EntityDamageByEntityEvent e) {
        if (e.getEntityType().equals(EntityType.PLAYER) && ((Player) e.getEntity()).getHealth() - e.getDamage() < 1) {
            Player p = (Player) e.getEntity();
            m.gi.removePlayerFromLive(p);
            p.setHealth(20.0);
            for (ItemStack itemStack : p.getInventory()) {
                p.getWorld().dropItemNaturally(p.getLocation(), itemStack);
            }
            p.setGameMode(GameMode.SPECTATOR);
            p.sendTitle(ChatColor.BOLD + "" + ChatColor.RED + "You Died!", "", 0, 80, 40);
        }
    }
}
