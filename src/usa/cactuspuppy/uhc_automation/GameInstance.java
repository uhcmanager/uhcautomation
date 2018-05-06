package usa.cactuspuppy.uhc_automation;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GameInstance {
    private Plugin plugin;
    private int state;
    private long startT;
    private int initSize;
    private int finalSize;

    public GameInstance(Plugin p) {
        plugin = p;
        state = 0;
        startT = 0;
    }

    public boolean start() {
        startT = System.currentTimeMillis();
        Bukkit.broadcastMessage("Game starting!");
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "spreadplayers ");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
        plugin.getLogger().info("Game Start Time - " + sdf.format(new Date(startT)));
        (new BorderCountdown((Main) plugin, calcBorderShrinkTime(), startT)).schedule();
        return true;
    }

    private int calcBorderShrinkTime() {
        return (initSize - finalSize) * 2;
    }
}
