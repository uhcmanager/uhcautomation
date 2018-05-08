package usa.cactuspuppy.uhc_automation;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public class GameInstance {
    private Plugin plugin;
    private int state;
    private long startT;
    private int minsToShrink;
    private int initSize;
    private int finalSize;
    private boolean teamMode;
    private int borderCountdown;
    private boolean borderShrinking;

    public GameInstance(Plugin p) {
        plugin = p;
        state = 0;
        startT = 0;
        minsToShrink = 120;
        borderShrinking = false;
    }

    public void setInitSize(int s) {
        initSize = s;
    }

    public void setFinalSize(int s) {
        finalSize = s;
    }

    public void setTimeToShrink(int minutes) {
        minsToShrink = minutes;
    }

    public void setTeamMode(boolean b) {
        teamMode = b;
    }

    public boolean start() {
        startT = System.currentTimeMillis();
        Bukkit.broadcastMessage("Game starting!");
        //TODO: Finish spreadplayers command
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "spreadplayers ");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
        plugin.getLogger().info("Game Start Time - " + sdf.format(new Date(startT)));
        borderCountdown = (new BorderCountdown((Main) plugin, minsToShrink * 60, startT)).schedule();
        return true;
    }

    public void stop() {
        if (!borderShrinking) {
            Bukkit.getScheduler().cancelTask(borderCountdown);
        }
        long stopT = System.currentTimeMillis();
        long timeElapsed = stopT - startT;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
        plugin.getLogger().info("Game Stop Time - " + sdf.format(new Date(stopT)));
        plugin.getLogger().info("Time Elapsed: " + timeElapsed / 3600000 + " hours "
                + (timeElapsed / 60000) % 60 + " minutes "
                + (timeElapsed / 1000) + "seconds");
    }

    protected void startBorderShrink() {
        //stop stop() from cancelling task because it no longer exists
        borderShrinking = true;
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "worldborder set " + finalSize + " " + calcBorderShrinkTime());
    }

    private int calcBorderShrinkTime() {
        return (initSize - finalSize) * 2;
    }
}
