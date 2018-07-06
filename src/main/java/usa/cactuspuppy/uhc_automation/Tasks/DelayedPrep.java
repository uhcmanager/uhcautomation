package usa.cactuspuppy.uhc_automation.Tasks;

import org.bukkit.Bukkit;
import usa.cactuspuppy.uhc_automation.Main;

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
