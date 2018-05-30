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

        for (UUID u : m.gi.getActivePlayers()) {
            Player p = Bukkit.getPlayer(u);
            p.sendTitle(ChatColor.WHITE + "" + iter, ChatColor.RED + "Releasing players in...", 0, 40, 20);
            if (iter > 3) {
                p.playSound(p.getLocation(), "minecraft:block.stone_button.click_on", (float) 1, (float) 1);
            } else {
                p.playSound(p.getLocation(), "minecraft:block.note.pling", (float) 1, (float) 0.59);
            }
        }
        iter--;
    }

    public int schedule() {
        this.assignedID = Bukkit.getScheduler().scheduleSyncRepeatingTask(m, this, 0L, 20L);
        return assignedID;
    }
}
