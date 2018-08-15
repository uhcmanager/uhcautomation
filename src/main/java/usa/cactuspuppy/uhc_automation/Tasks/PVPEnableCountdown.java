package usa.cactuspuppy.uhc_automation.Tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

import java.util.LinkedList;
import java.util.List;

public class PVPEnableCountdown implements Runnable {
    private long enableTime;
    private LinkedList<Long> announceTimes;
    private long nextAnnounce;
    private boolean silent;

    private Integer assignedID;

    public PVPEnableCountdown(long secs, long startT, boolean announce) {
        enableTime = startT + secs * 1000;
        silent = !announce;
        announceTimes = new LinkedList<>();
        List<Long> times = UHCUtils.getConfigCSLongs("warning-times.pvp").orElseGet(UHCUtils::getDefaultTimes);
        for (long add : times) {
            if (add > secs) {
                break;
            }
            announceTimes.addLast(add);
        }
        nextAnnounce = announceTimes.removeLast();
    }

    public void run() {
        if (System.currentTimeMillis() >= enableTime) {
            Main.getInstance().getGameInstance().getWorld().setPVP(true);
            UHCUtils.broadcastMessagewithSound(Main.getInstance().getGameInstance(), "\n" + ChatColor.DARK_RED + ChatColor.BOLD + "PVP HAS BEEN ENABLED.", "minecraft:entity.ender_dragon.growl", 1.0F, 1.0F);
            if (assignedID != null) { Bukkit.getScheduler().cancelTask(assignedID); }
            return;
        }
        if (silent) return;
        if (System.currentTimeMillis() >= enableTime - nextAnnounce * 1000) {
            long timeTo = nextAnnounce;
            if (!announceTimes.isEmpty()) {
                nextAnnounce = announceTimes.removeLast();
            }
            if (timeTo <= 10) {
                UHCUtils.broadcastMessagewithSoundandTitle(Main.getInstance().getGameInstance(), ChatColor.RED.toString() + ChatColor.BOLD + "[Alert] " + ChatColor.RESET + "PVP enabled in " + UHCUtils.secsToFormatString(timeTo), String.valueOf(timeTo), ChatColor.RED + "secs until PVP enabled", 0, 80, 40, "minecraft:block.note.chime", (float) (0.5 + (10 - timeTo) / 40), 1.18F);
            } else {
                UHCUtils.broadcastMessagewithSound(Main.getInstance().getGameInstance(), ChatColor.YELLOW.toString() + ChatColor.BOLD + "[Warning] " + ChatColor.RESET + "PVP enabled in " + UHCUtils.secsToFormatString(timeTo), "minecraft:entity.player.levelup", 0.5F, 0.4F);
            }
        }
    }

    public void schedule() {
        this.assignedID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), this, 0L, 20L);
    }
}
