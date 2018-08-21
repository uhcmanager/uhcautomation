package usa.cactuspuppy.uhc_automation.Tasks;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

import java.util.LinkedList;
import java.util.List;

public class BorderCountdown implements Runnable {
    @Getter private long end;
    private LinkedList<Long> warningTimes;
    private long nextWarn;
    private boolean silent;

    @Getter private static BorderCountdown instance;

    private Integer assignedID;

    /**
     * Creates new border countdown
     * @param l length of time to shrink (in seconds)
     * @param s start time of game
     */
    public BorderCountdown(int l, long s, boolean announce) {
        instance = this;
        end = s + l * 1000;
        silent = !announce;
        warningTimes = new LinkedList<>();
        List<Long> times = UHCUtils.getConfigCSLongs("warning-times.border").orElseGet(UHCUtils::getDefaultTimes);
        for (long add : times) {
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
            Main.getInstance().getGameInstance().startBorderShrink();
            if (assignedID != null) { Bukkit.getScheduler().cancelTask(assignedID); }
            return;
        }
        if (silent) return;
        if (System.currentTimeMillis() >= end - nextWarn * 1000) {
            int timeTo = (int) nextWarn;
            if (!warningTimes.isEmpty()) {
                nextWarn = warningTimes.removeLast();
            }
            if (timeTo <= 10) {
                UHCUtils.broadcastMessagewithSoundandTitle(Main.getInstance().getGameInstance(), ChatColor.RED.toString() + ChatColor.BOLD + "[Alert] " + ChatColor.RESET + "Border shrinks in " + UHCUtils.secsToFormatString(timeTo), String.valueOf(timeTo), ChatColor.RED + "secs to border shrink", 0, 80, 40, "minecraft:block.note_block.chime", (float) (0.5 + (10 - timeTo) / 40), 1.18F);
            } else {
                UHCUtils.broadcastMessagewithSound(Main.getInstance().getGameInstance(), ChatColor.YELLOW.toString() + ChatColor.BOLD + "[Warning] " + ChatColor.RESET + "Border shrinks in " + UHCUtils.secsToFormatString(timeTo), "minecraft:entity.player.levelup", 0.5F, 0.4F);
            }
        }
    }

    public int schedule() {
        this.assignedID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), this, 0L, 10L);
        return assignedID;
    }
}
