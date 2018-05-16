package usa.cactuspuppy.uhc_automation;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
    private Main m;

    public PlayerDeathListener(Main main) {
        m = main;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (UHCUtils.worldEqualsExt(e.getEntity().getWorld(), m.gi.getWorld())) {

        }
    }
}
