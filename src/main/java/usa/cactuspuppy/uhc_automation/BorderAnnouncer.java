package usa.cactuspuppy.uhc_automation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BorderAnnouncer implements Runnable {
    private Main m;

    public BorderAnnouncer(Main main) {
        m = main;
    }

    @Override
    public void run() {
        for (UUID u : m.gi.getActivePlayers()) {
            Player p = Bukkit.getPlayer(u);
            UHCUtils.sendActionBar(p, ChatColor.DARK_RED.toString() + ChatColor.BOLD
                    + "[Border]" + ChatColor.RESET + " ±" + (int) m.gi.getWorld().getWorldBorder().getSize() / 2);
        }
    }

    public void schedule() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(m, this, 0L, 5L);
    }
}
