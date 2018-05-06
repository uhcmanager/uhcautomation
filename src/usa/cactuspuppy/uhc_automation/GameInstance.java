package usa.cactuspuppy.uhc_automation;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GameInstance {
    private Plugin plugin;
    private int state;
    private long startT;
    private int minsToShrink;
    private int initSize;
    private int finalSize;

    public GameInstance(Plugin p) {
        plugin = p;
        state = 0;
        startT = 0;
        minsToShrink = 120;
    }

    public void setGameDuration(int minutes) {
        minsToShrink = minutes;
    }

    public boolean start() {
        startT = System.currentTimeMillis();
        Bukkit.broadcastMessage("Game starting!");
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "spreadplayers ");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
        plugin.getLogger().info("Game Start Time - " + sdf.format(new Date(startT)));
        (new BorderCountdown((Main) plugin, minsToShrink, startT)).schedule();
        return true;
    }

    protected void startBorderShrink() {
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "worldborder set " + finalSize + " " + calcBorderShrinkTime());
    }

    private int calcBorderShrinkTime() {
        return (initSize - finalSize) * 2;
    }
}
