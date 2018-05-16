package usa.cactuspuppy.uhc_automation;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DelayedPlayerRespawn implements Runnable {
    private Main m;
    private Player p;
    private Location l;

    public Integer assignedID;

    public DelayedPlayerRespawn(Main main, Player player, Location location) {
        m = main;
        p = player;
        l = location;
    }

    @Override
    public void run() {
        p.spigot().respawn();
        p.setGameMode(GameMode.SPECTATOR);
        p.teleport(l);
    }

    public void schedule() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(m, this, 1L);
    }
}
