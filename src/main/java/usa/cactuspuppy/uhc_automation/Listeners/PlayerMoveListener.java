package usa.cactuspuppy.uhc_automation.Listeners;

import lombok.NoArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

@NoArgsConstructor
public class PlayerMoveListener implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (UHCUtils.worldEqualsExt(e.getPlayer().getWorld(), Main.getInstance().getGameInstance().getWorld()) && Main.getInstance().getGameInstance().getLivePlayers().contains(e.getPlayer().getUniqueId())) {
            if (samePlace(e.getFrom(), e.getTo())) {
                return;
            }
            e.setCancelled(true);
            e.getPlayer().teleport(e.getFrom());
            UHCUtils.sendActionBar(e.getPlayer(), ChatColor.RED + "Please remain still until the game starts!");
        }
    }

    private boolean samePlace(Location a, Location b) {
        return a.getWorld().equals(b.getWorld()) && a.getX() == b.getX()
                && a.getZ() == b.getZ();
    }
}
