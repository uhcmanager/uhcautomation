package usa.cactuspuppy.uhc_automation.game.tasks.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.game.types.UHC;

public class UHC_LobbyListener extends ListenerTask {
    UHC uhc;

    public UHC_LobbyListener(UHC gameInstance) {
        super(gameInstance);
        uhc = gameInstance;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (!uhc.getAllPlayers().contains(e.getEntity().getUniqueId())) {
            return;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            e.getEntity().spigot().respawn();
            int x = uhc.getCenterX();
            int z = uhc.getCenterZ();
            int y = uhc.getMainWorld().getHighestBlockYAt(x, z);
            e.getEntity().teleport(new Location(uhc.getMainWorld(), x, y, z));
        }, 1L);
        e.setDeathMessage("[" + ChatColor.RED + "DEATH" + ChatColor.RESET + "]" + e.getDeathMessage());
    }
}
