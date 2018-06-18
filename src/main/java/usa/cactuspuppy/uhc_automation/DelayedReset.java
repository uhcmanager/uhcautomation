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
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerConnectionListener(main), main);
        Bukkit.getServer().getPluginManager().registerEvents(new WorldChangeListener(main), main);
        main.gmcl = new GameModeChangeListener(main);
        Bukkit.getServer().getPluginManager().registerEvents(main.gmcl, main);
    }

    public void schedule() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(main, this, 1L);
    }
}
