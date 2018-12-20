package usa.cactuspuppy.uhc_automation.Tasks;

import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import usa.cactuspuppy.uhc_automation.Main;

@NoArgsConstructor
public class DelayedPrep implements Runnable {
    @Override
    public void run() {
        Main.getInstance().getGameInstance().prep();
    }

    public void schedule() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), this, 1L);
    }
}
