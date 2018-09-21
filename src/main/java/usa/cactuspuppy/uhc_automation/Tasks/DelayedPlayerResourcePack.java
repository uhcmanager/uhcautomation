package usa.cactuspuppy.uhc_automation.Tasks;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

import java.util.UUID;

@AllArgsConstructor
public class DelayedPlayerResourcePack implements Runnable {
    private UUID player;

    @Override
    public void run() {
        //DEBUG
        Main.getInstance().getLogger().info("Player " + Bukkit.getPlayer(player) + " has been sent RP request.");
        String hash = Main.getInstance().getConfig().getString("rp-hash");
        Bukkit.getPlayer(player).setResourcePack(Main.getInstance().getConfig().getString("rp-url"), UHCUtils.hexStringToByteArray(hash));
    }

    public void schedule() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), this, 20L);
    }
}
