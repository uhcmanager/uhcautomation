package usa.cactuspuppy.uhc_automation.Tasks;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.Main;

public class DelayedPlayerRespawn implements Runnable {
    private Player p;
    private Location l;

    public DelayedPlayerRespawn(Player player, Location location) {
        p = player;
        l = location;
    }

    @Override
    public void run() {
        p.spigot().respawn();
        p.setGameMode(GameMode.SPECTATOR);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(),() -> p.teleport(l), 1L);
    }

    public void schedule() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), this, 1L);
    }
}
