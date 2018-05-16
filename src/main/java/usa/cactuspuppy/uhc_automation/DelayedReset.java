package usa.cactuspuppy.uhc_automation;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public class DelayedReset implements Runnable {
    private Main main;

    public DelayedReset(Main m) {
        main = m;
    }

    @Override
    public void run() {
        Bukkit.getScheduler().cancelAllTasks();
        HandlerList.unregisterAll();
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinListener(main), main);
        Bukkit.getServer().getPluginManager().registerEvents(new WorldChangeListener(main), main);
    }

    public void schedule() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(main, this, 1L);
    }
}
