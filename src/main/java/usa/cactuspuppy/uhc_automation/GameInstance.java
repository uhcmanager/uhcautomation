package usa.cactuspuppy.uhc_automation;

import org.bukkit.Bukkit;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GameInstance {
    private Main main;
    private long startT;

    private int minsToShrink;
    private int initSize;
    private int finalSize;
    private int spreadDistance;
    private boolean teamMode;
    private boolean respectTeams;
    private boolean uhcMode;
    
    private int borderCountdown;
    private boolean borderShrinking;

    protected GameInstance(Main p) {
        main = p;
        startT = 0;
        minsToShrink = p.getConfig().getInt("game.minds-to-shrink");
        initSize = p.getConfig().getInt("game.init-size");
        finalSize = p.getConfig().getInt("game.final-size");
        teamMode = p.getConfig().getBoolean("game.team-mode");
        respectTeams = p.getConfig().getBoolean("game.respect-teams");
        spreadDistance = p.getConfig().getInt("game.spread-distance");
        borderShrinking = false;
    }

    protected void setInitSize(int s) {
        initSize = s;
    }

    protected void setFinalSize(int s) {
        finalSize = s;
    }

    protected void setTimeToShrink(int minutes) {
        minsToShrink = minutes;
    }

    protected void setTeamMode(boolean b) {
        teamMode = b;
    }

    protected void setSpreadDistance(int sd) {
        spreadDistance = sd;
    }

    protected void setRespectTeams(boolean rt) {
        respectTeams = rt;
    }

    public boolean start() {
        startT = System.currentTimeMillis();
        Bukkit.broadcastMessage("Game starting!");
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "spreadplayers 0 0 " + spreadDistance + " " + initSize / 2 + " " + respectTeams + " @a[m=0]");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
        main.getLogger().info("Game Start Time - " + sdf.format(new Date(startT)));
        borderCountdown = (new BorderCountdown(main, minsToShrink * 60, startT)).schedule();
        return true;
    }

    public void stop() {
        if (!borderShrinking) {
            Bukkit.getScheduler().cancelTask(borderCountdown);
        }
        long stopT = System.currentTimeMillis();
        long timeElapsed = stopT - startT;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
        main.getLogger().info("Game Stop Time - " + sdf.format(new Date(stopT)));
        main.getLogger().info("Time Elapsed: " + timeElapsed / 3600000 + " hours "
                + (timeElapsed / 60000) % 60 + " minutes "
                + (timeElapsed / 1000) + "seconds");
    }

    protected void startBorderShrink() {
        borderShrinking = true;
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
                "worldborder set " + finalSize + " " + calcBorderShrinkTime());
        main.getLogger().info("Game border shrinking from " + initSize + " " + " to " + finalSize
                + "over " + calcBorderShrinkTime() + " secs");
    }

    private int calcBorderShrinkTime() {
        return (initSize - finalSize) * 2;
    }

    public int getInitSize() {
        return initSize;
    }

    public int getFinalSize() {
        return finalSize;
    }
}
