package usa.cactuspuppy.uhc_automation;

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
        startEP = startT;
        goalEP = startEP + 60000 * mins;
        epCount = 0;
    }

    @Override
    public void run() {
        if (System.currentTimeMillis() >= goalEP) {
            startEP = goalEP;
            goalEP = startEP + 60000 * length;
            epCount++;
            m.gi.getAllPlayers().forEach((p) -> markEpisode(Bukkit.getPlayer(p)));
            m.getLogger().info(ChatColor.GOLD + "Mark Episode " + epCount);
        }
    }

    private void markEpisode(Player p) {
        int mins = epCount * length;
        int hours = Math.floorDiv(mins, 60);
        mins = mins % 60;
        p.playSound(p.getLocation(), "minecraft:entity.firework.launch", (float) 1, (float) 1);
        p.sendMessage(ChatColor.GOLD + "\nMARK: " + ChatColor.WHITE + "End of Episode" + ChatColor.GREEN + epCount);
        p.sendMessage(ChatColor.AQUA + "Time Elapsed: " + ChatColor.GREEN + hours + ChatColor.WHITE + " Hours " + ChatColor.GREEN + mins + ChatColor.WHITE + " Minutes");
    }

    public void schedule() {
        this.assignedID = Bukkit.getScheduler().scheduleSyncRepeatingTask(m, this, 0L, 5L);
    }
}
