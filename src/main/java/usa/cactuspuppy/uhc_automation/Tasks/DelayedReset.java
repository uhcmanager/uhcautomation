package usa.cactuspuppy.uhc_automation.Tasks;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import usa.cactuspuppy.uhc_automation.Listeners.FixColoredNamesChatListener;
import usa.cactuspuppy.uhc_automation.Listeners.GameModeChangeListener;
import usa.cactuspuppy.uhc_automation.Listeners.PlayerConnectionListener;
import usa.cactuspuppy.uhc_automation.Listeners.PlayerDeathListener;
import usa.cactuspuppy.uhc_automation.Listeners.WorldChangeListener;
import usa.cactuspuppy.uhc_automation.Main;

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
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerDeathListener(main), main);
        main.gmcl = new GameModeChangeListener(main);
        Bukkit.getServer().getPluginManager().registerEvents(main.gmcl, main);
        Bukkit.getServer().getPluginManager().registerEvents(new FixColoredNamesChatListener(main), main);
        (new FixTabNameTask(main)).schedule();
    }

    public void schedule() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(main, this, 1L);
    }
}
