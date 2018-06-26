package usa.cactuspuppy.uhc_automation;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class EpisodeAnnouncer implements Runnable {
    private Main m;
    private int length;
    private long startEP;
    private long goalEP;
    private int epCount;

    public Integer assignedID;

    public EpisodeAnnouncer(Main main, int mins, long startT) {
        m = main;
        length = mins;
        long currTime = System.currentTimeMillis();
        if (currTime - startT > 10000) {
            int minsElapsed = (int) (Math.floorDiv(currTime - startT, 60000));
            epCount = Math.floorDiv(minsElapsed, length);
            startEP = startT + epCount * length * 60000;
            goalEP = startEP + 60000 * mins;
        } else {
            startEP = startT;
            goalEP = startEP + 60000 * mins;
            epCount = 0;
        }
    }

    @Override
    public void run() {
        if (System.currentTimeMillis() >= goalEP) {
            startEP = goalEP;
            goalEP = startEP + 60000 * length;
            epCount++;
            m.gi.getActivePlayers().forEach((p) -> markEpisode(Bukkit.getPlayer(p)));
            m.getLogger().info("Mark Episode " + epCount);
        }
    }

    private void markEpisode(Player p) {
        int mins = epCount * length;
        p.playSound(p.getLocation(), "minecraft:entity.firework.launch", (float) 1, (float) 1);
        p.sendMessage(ChatColor.GOLD + "\nMARK: " + ChatColor.WHITE + "End of Episode " + ChatColor.GREEN + epCount);
        p.sendMessage(ChatColor.AQUA + "Time Elapsed: " + ChatColor.WHITE + WordUtils.capitalize(UHCUtils.secsToFormatString(mins * 60)));
    }

    public void schedule() {
        this.assignedID = Bukkit.getScheduler().scheduleSyncRepeatingTask(m, this, 0L, 5L);
    }
}
