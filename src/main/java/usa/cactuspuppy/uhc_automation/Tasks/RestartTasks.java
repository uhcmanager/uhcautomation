package usa.cactuspuppy.uhc_automation.Tasks;

import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import usa.cactuspuppy.uhc_automation.Listeners.*;
import usa.cactuspuppy.uhc_automation.Main;

@NoArgsConstructor
public class RestartTasks implements Runnable {
    @Override
    public void run() {
        Bukkit.getScheduler().cancelTasks(Main.getInstance());
        HandlerList.unregisterAll(Main.getInstance());
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerConnectionListener(), Main.getInstance());
        Bukkit.getServer().getPluginManager().registerEvents(new WorldChangeListener(), Main.getInstance());
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerDeathListener(), Main.getInstance());
        Bukkit.getServer().getPluginManager().registerEvents(new GameModeChangeListener(), Main.getInstance());
        Bukkit.getServer().getPluginManager().registerEvents(new FixColoredNamesChatListener(), Main.getInstance());
        (new FixTabNameTask(Main.getInstance())).schedule();
    }

    public void schedule() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), this, 1L);
    }
}
