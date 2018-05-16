package usa.cactuspuppy.uhc_automation;

import org.bukkit.ChatColor;
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
            e.getPlayer().sendMessage(ChatColor.GREEN + "You have been successfully registered in UHC Automation!");
        }
    }
}
