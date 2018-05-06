package usa.cactuspuppy.uhc_automation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class BorderCountdown implements Runnable {
    private Main main;
    private int length;
    private int secs;

    private Integer assignedID;

    public BorderCountdown(Main m, int l, long start) {
        main = m;
        length = l;
        secs = l;
    }

    @Override
    public void run() {
        //When time up, unschedule task
        if (secs <= 0) {
            main.gi.start();
            if (assignedID != null) { Bukkit.getScheduler().cancelTask(assignedID); }
            return;
        }

        //Chat message
        if (secs == length) {
            Bukkit.broadcastMessage(ChatColor.RED + "ALERT: " + ChatColor.WHITE + "Game begins in 10 seconds!");
        }

        //Title announcements
        ChatColor use;
        if (secs > 5) {
            use = ChatColor.WHITE;
        } else {
            use = ChatColor.RED;
        }
        Bukkit.getOnlinePlayers().forEach((p) -> p.sendTitle(use + "" + secs, ChatColor.WHITE + "until the game starts!", 0, 80, 40));

        secs--;
    }

    public void schedule() {
        this.assignedID = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, this, 0L, 20L);
    }
}
