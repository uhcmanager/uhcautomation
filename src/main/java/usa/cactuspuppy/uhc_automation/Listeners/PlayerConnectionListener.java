package usa.cactuspuppy.uhc_automation.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

public class PlayerConnectionListener implements Listener {
    private Main m;

    public PlayerConnectionListener(Main main) {
        m = main;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (UHCUtils.worldEqualsExt(p.getWorld(), m.gi.getWorld())) {
            m.gi.bindPlayertoScoreboard(p);
            if (m.gi.getBlacklistPlayers().contains(p.getUniqueId())) {
                p.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "You have joined the game as a spectator. Ask an administrator if you think you should be in the game.");
                p.setGameMode(GameMode.SPECTATOR);
                m.gi.registerPlayer(p);
                return;
            }
            if (m.gi.isActive()) {
                if (m.gi.getRegPlayers().contains(p.getUniqueId())) {
                    m.gi.registerPlayer(p);
                } else {
                    p.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "You have joined the game as a spectator. Ask an administrator if you think you should be in the game.");
                    p.setGameMode(GameMode.SPECTATOR);
                    m.gi.registerPlayer(p);
                }
            } else if (!m.gi.getActivePlayers().contains(p.getUniqueId())) {
                p.sendTitle(ChatColor.GOLD + "Welcome", "to the " + m.getConfig().getString("event-name"), 20, 60, 20);
                p.setHealth(19);
                p.setHealth(20);
                m.gi.registerPlayer(p);
            }
        } else {
            p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (m.gi.getActivePlayers().contains(e.getPlayer().getUniqueId())) {
            m.gi.lostConnectPlayer(e.getPlayer());
        }
    }
}
