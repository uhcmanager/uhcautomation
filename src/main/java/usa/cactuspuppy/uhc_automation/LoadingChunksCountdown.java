package usa.cactuspuppy.uhc_automation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LoadingChunksCountdown implements Runnable {
    private Main m;
    private int length;
    private int iter;

    public Integer assignedID;

    public LoadingChunksCountdown(Main main, int l) {
        iter = l;
        length = l;
        m = main;
    }

    @Override
    public void run() {
        if (iter == 0) {
            m.gi.release();
            if (assignedID != null) { Bukkit.getScheduler().cancelTask(assignedID); }
            return;
        }

        for (UUID u : m.gi.getAllPlayers()) {
            Player p = Bukkit.getPlayer(u);
            p.sendTitle(ChatColor.WHITE + "" + iter, ChatColor.RED + "Releasing players in...", 0, 40, 20);
        }
        iter--;
    }

    public int schedule() {
        this.assignedID = Bukkit.getScheduler().scheduleSyncRepeatingTask(m, this, 0L, 20L);
        return assignedID;
    }
}
