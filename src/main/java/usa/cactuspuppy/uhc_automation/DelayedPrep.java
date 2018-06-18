package usa.cactuspuppy.uhc_automation;

import org.bukkit.Bukkit;

public class DelayedPrep implements Runnable {
    private Main main;

    public DelayedPrep(Main m) {
        main = m;
    }

    @Override
    public void run() {
        main.gi.prep();
    }

    public void schedule() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(main, this, 1L);
    }
}
