package usa.cactuspuppy.uhc_automation;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {
    private Main m;

    public PlayerConnectionListener(Main main) {
        m = main;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!m.gi.getActivePlayers().contains(e.getPlayer().getUniqueId())
                && UHCUtils.worldEqualsExt(e.getPlayer().getWorld(), m.gi.getWorld())) {
            if (!m.gi.getRegPlayers().contains(e.getPlayer().getUniqueId())) {
                Player p = e.getPlayer();
                p.sendTitle(ChatColor.GOLD + "Welcome", "to the " + m.getConfig().getString("event-name"), 20, 60, 20);
                p.setHealth(19);
                p.setHealth(20);
            }
            m.gi.registerPlayer(e.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (m.gi.getActivePlayers().contains(e.getPlayer().getUniqueId())) {
            m.gi.lostConnectPlayer(e.getPlayer());
        }
    }
}
