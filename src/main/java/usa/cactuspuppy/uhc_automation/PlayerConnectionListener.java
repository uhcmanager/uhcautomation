package usa.cactuspuppy.uhc_automation;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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
        Player p = e.getPlayer();
        if (UHCUtils.worldEqualsExt(p.getWorld(), m.gi.getWorld())) {
            if (m.gi.getBlacklistPlayers().contains(p.getUniqueId())) {
                p.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "You have been blacklisted from the current UHC. If you believe this is in error, pleace contact a server administrator. For now, you will be put into spectator mode, but will not be considered part of the game (you will not receive alerts concerning the game).");
                p.setGameMode(GameMode.SPECTATOR);
                return;
            }
            if (!m.gi.getRegPlayers().contains(p.getUniqueId())) {
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
