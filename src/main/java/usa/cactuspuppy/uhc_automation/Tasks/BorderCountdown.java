package usa.cactuspuppy.uhc_automation.Tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

import java.util.LinkedList;

public class BorderCountdown implements Runnable {
    private Main main;
    private long start;
    private long end;
    private LinkedList<Long> warningTimes;
    private long nextWarn;

    private Integer assignedID;

    /**
     * Creates new border countdown
     * @param m main class
     * @param l length of time to shrink (in seconds)
     * @param s start time of game
     */
    public BorderCountdown(Main m, int l, long s) {
        main = m;
        start = s;
        long timeDiff = l * 1000;
        end = s + timeDiff;
        warningTimes = new LinkedList<>();
        int[] times = {1,2,3,4,5,6,7,8,9,10,15,20,30,45,60,120,180,300,600,900,1200,1800,3600};
        for (int i = 0; i < times.length; i++) {
            long add = times[i];
            if (add > l) {
                break;
            }
            warningTimes.addLast(add);
        }
        nextWarn = warningTimes.removeLast();
    }

    @Override
    public void run() {
        //When time up, unschedule task
        if (System.currentTimeMillis() >= end) {
            main.gi.startBorderShrink();
            if (assignedID != null) { Bukkit.getScheduler().cancelTask(assignedID); }
            return;
        }
        if (System.currentTimeMillis() >= end - nextWarn * 1000) {
            int timeTo = (int) nextWarn;
            if (!warningTimes.isEmpty()) {
                nextWarn = warningTimes.removeLast();
            }
            if (timeTo <= 10) {
                UHCUtils.broadcastMessagewithSoundandTitle(main.gi, ChatColor.RED.toString() + ChatColor.BOLD + "[Alert] " + ChatColor.RESET + "Border shrinks in " + UHCUtils.secsToFormatString(timeTo), String.valueOf(timeTo), ChatColor.RED + "secs to border shrink", 0, 80, 40, "minecraft:block.note.chime", (float) (0.5 + (10 - timeTo) / 40), 1.18F);
            } else {
                UHCUtils.broadcastMessagewithSound(main.gi, ChatColor.YELLOW.toString() + ChatColor.BOLD + "[Warning] " + ChatColor.RESET + "Border shrinks in " + UHCUtils.secsToFormatString(timeTo), "minecraft:entity.player.levelup", 0.5F, 0.4F);
            }
        }
    }

    public int schedule() {
        this.assignedID = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, this, 0L, 20L);
        return assignedID;
    }
}
