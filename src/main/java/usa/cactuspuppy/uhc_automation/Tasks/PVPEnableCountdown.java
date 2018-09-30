package usa.cactuspuppy.uhc_automation.Tasks;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import usa.cactuspuppy.uhc_automation.GameInstance;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

import java.util.LinkedList;
import java.util.List;

public class PVPEnableCountdown implements Runnable {
    @Getter private long enableTime;
    private LinkedList<Long> announceTimes;
    private long nextAnnounce;
    private boolean silent;

    @Getter private static PVPEnableCountdown instance;

    private Integer assignedID;

    public PVPEnableCountdown(long secs, long startT, boolean announce) {
        instance = this;
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
            UHCUtils.broadcastMessagewithSoundandTitle(Main.getInstance().getGameInstance(), "\n" + ChatColor.DARK_RED + ChatColor.BOLD + "PVP HAS BEEN ENABLED.", ChatColor.DARK_RED.toString() + ChatColor.BOLD + "PVP", "enabled!", 0, 40, 20, "minecraft:entity.ender_dragon.growl", 1.0F, 1.0F);
            if (assignedID != null) { Bukkit.getScheduler().cancelTask(assignedID); }
            return;
        }
        if (silent) return;
        if (System.currentTimeMillis() >= enableTime - nextAnnounce * 1000) {
            long timeTo = nextAnnounce;
            if (!announceTimes.isEmpty()) {
                nextAnnounce = announceTimes.removeLast();
            } else {
                silent = true;
            }
            if (timeTo <= 10) {
                if (timeTo == 10) {
                    UHCUtils.broadcastChatMessage(ChatColor.RED.toString() + ChatColor.BOLD + "[Alert] " + ChatColor.RESET + "PVP enabled in " + UHCUtils.secsToFormatString(timeTo));
                }
                UHCUtils.broadcastSoundandTitle(Main.getInstance().getGameInstance(), "minecraft:block.note_block.chime", (float) (0.5 + (10 - timeTo) / 40), 1.18F, String.valueOf(timeTo), ChatColor.RED + "secs to border shrink", 0, 80, 40);
            } else {
                UHCUtils.broadcastMessagewithSound(Main.getInstance().getGameInstance(), ChatColor.YELLOW.toString() + ChatColor.BOLD + "[Warning] " + ChatColor.RESET + "PVP enabled in " + UHCUtils.secsToFormatString(timeTo), "minecraft:entity.player.levelup", 0.5F, 0.4F);
            }
        }
    }

    public void schedule() {
        this.assignedID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), this, 0L, 10L);
    }
}
