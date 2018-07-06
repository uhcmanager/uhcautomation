package usa.cactuspuppy.uhc_automation.Tasks;

import org.bukkit.Bukkit;
import usa.cactuspuppy.uhc_automation.Main;

public class LiftOverrideCountdown implements Runnable {
    private Main main;
    private long liftTime;

    /**
     * Creates restoration countdown to be used
     * @param length in ticks until override is lifted
     * @param main class to use
     */
    public LiftOverrideCountdown(int length, Main main) {
        liftTime = System.currentTimeMillis() + length * 50;
        this.main = main;
    }

    public void run() {
        if (System.currentTimeMillis() >= liftTime) {
            main.gi.getTimeAnnouncer().setOverride(false);
        }
    }

    public void schedule() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(main, this, 0L, 10L);
    }
}
