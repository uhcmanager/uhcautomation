package usa.cactuspuppy.uhc_automation;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {
    private Main m;

    public PlayerMoveListener(Main main) {
        m = main;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (m.gi.getLivePlayers().contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            e.getPlayer().teleport(e.getFrom());
            UHCUtils.sendActionBar(e.getPlayer(), ChatColor.RED + "Please remain still until the game starts!");
        }
    }
}
