package usa.cactuspuppy.uhc_automation.Tasks;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

public class EpisodeAnnouncer implements Runnable {
    private int length;
    private long startEP;
    private long goalEP;
    private int epCount;

    public Integer assignedID;

    public EpisodeAnnouncer(int mins, long startT) {
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
            Main.getInstance().getGameInstance().getActivePlayers().forEach((p) -> markEpisode(Bukkit.getPlayer(p)));
            Main.getInstance().getLogger().info("Mark Episode " + epCount);
        }
    }

    private void markEpisode(Player p) {
        int mins = epCount * length;
        p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1F, 1F);
        p.sendMessage(ChatColor.GOLD + "\nMARK: " + ChatColor.WHITE + "End of Episode " + ChatColor.GREEN + epCount);
        p.sendMessage(ChatColor.AQUA + "Time Elapsed: " + ChatColor.WHITE + WordUtils.capitalize(UHCUtils.secsToFormatString(mins * 60)));
    }

    public void schedule() {
        this.assignedID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), this, 0L, 5L);
    }
}
