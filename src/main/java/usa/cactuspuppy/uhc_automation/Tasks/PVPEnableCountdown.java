package usa.cactuspuppy.uhc_automation.Tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.Main;

public class PVPEnableCountdown implements Runnable {
    private long enableTime;

    public PVPEnableCountdown(int secs) {
        enableTime = System.currentTimeMillis() + secs * 1000;
    }

    public void run() {
        if (System.currentTimeMillis() < enableTime) { return; }
        Main.getInstance().getGameInstance().getWorld().setPVP(true);
        Main.getInstance().getGameInstance().getActivePlayers().stream().map(Bukkit::getPlayer).forEach(this::announcePVPEnabled);
    }

    private void announcePVPEnabled(Player p) {
        assert p != null;
        p.sendMessage("\n" + ChatColor.DARK_RED);
    }
}
