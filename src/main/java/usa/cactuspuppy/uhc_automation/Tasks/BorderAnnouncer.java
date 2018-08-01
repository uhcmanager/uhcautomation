package usa.cactuspuppy.uhc_automation.Tasks;

import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

import java.util.UUID;

@NoArgsConstructor
public class BorderAnnouncer implements Runnable {
    @Override
    public void run() {
        for (UUID u : Main.getInstance().getGameInstance().getActivePlayers()) {
            Player p = Bukkit.getPlayer(u);
            int playerX = p.getLocation().getBlockX();
            int playerZ = p.getLocation().getBlockZ();
            int mostSigCoord = Math.max(Math.abs(playerX), Math.abs(playerZ));
            int distToBorder = ((int) Main.getInstance().getGameInstance().getWorld().getWorldBorder().getSize() / 2) - mostSigCoord;

            String color = ChatColor.RED.toString();
            if (distToBorder <= 10) {
                color = ChatColor.DARK_RED.toString() + ChatColor.BOLD;
            }

            UHCUtils.sendActionBar(p, ChatColor.GOLD.toString() + ChatColor.BOLD + "[Border] " + ChatColor.RESET + "±" + (int) Main.getInstance().getGameInstance().getWorld().getWorldBorder().getSize() / 2 + color + " (" + distToBorder + distToString(Math.abs(distToBorder)) + " away)");
        }
    }

    private String distToString(int dist) {
        if (dist == 1) {
            return " block";
        } else {
            return " blocks";
        }
    }

    public void schedule() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), this, 0L, 5L);
    }
}
