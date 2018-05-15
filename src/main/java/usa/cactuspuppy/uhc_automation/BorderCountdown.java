package usa.cactuspuppy.uhc_automation;

import org.bukkit.Bukkit;

public class BorderCountdown implements Runnable {
    private Main main;
    private long start;
    private long end;

    private Integer assignedID;

    public BorderCountdown(Main m, int l, long s) {
        main = m;
        start = s;
        end = s + l * 1000;
    }

    @Override
    public void run() {
        //When time up, unschedule task
        if (System.currentTimeMillis() == end) {
            main.gi.startBorderShrink();
            if (assignedID != null) { Bukkit.getScheduler().cancelTask(assignedID); }
            return;
        }
    }

    public int schedule() {
        this.assignedID = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, this, 0L, 20L);
        return assignedID;
    }
}
