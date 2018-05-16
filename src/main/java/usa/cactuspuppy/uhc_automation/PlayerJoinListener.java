package usa.cactuspuppy.uhc_automation;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private Main m;

    public PlayerJoinListener(Main main) {
        m = main;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!m.gi.getAllPlayers().contains(e.getPlayer().getUniqueId())
                && UHCUtils.worldEqualsExt(e.getPlayer().getWorld(), m.gi.getWorld())) {
            m.gi.registerPlayer(e.getPlayer());
            Player p = e.getPlayer();
            p.sendTitle(ChatColor.GOLD + "Welcome", "to Wyntr's May 2018 Subscriber UHC", 20, 60, 20);
            p.setHealth(19);
            p.setHealth(20);
        }
    }
}
