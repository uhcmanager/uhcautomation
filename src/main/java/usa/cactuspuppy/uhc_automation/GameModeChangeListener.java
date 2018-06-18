package usa.cactuspuppy.uhc_automation;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class GameModeChangeListener implements Listener {
    private Main m;

    public GameModeChangeListener(Main main) {
        m = main;
    }

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent e) {
        Player p = e.getPlayer();
        if (m.gi.activePlayers.contains(p.getUniqueId())) {
            if (m.gi.livePlayers.contains(p.getUniqueId()) && e.getNewGameMode() != GameMode.SURVIVAL) {
                m.gi.removePlayerFromLive(p);
            } else if (!m.gi.livePlayers.contains(p.getUniqueId()) && e.getNewGameMode() == GameMode.SURVIVAL) {
                m.gi.addPlayerToLive(p);
            }
        }
    }
}