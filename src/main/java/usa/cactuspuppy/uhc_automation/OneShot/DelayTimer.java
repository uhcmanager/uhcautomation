package usa.cactuspuppy.uhc_automation.OneShot;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class DelayTimer implements Runnable {
    @Getter
    private long end;
    private LinkedList<Long> warningTimes;
    private long nextWarn;
    private boolean silent;

    @Getter private static DelayTimer instance;

    private Integer assignedID;

    public DelayTimer(int l, long s, boolean announce) {
        instance = this;
        end = s + l * 1000;
        silent = !announce;
        warningTimes = new LinkedList<>();
        List<Long> times = UHCUtils.getConfigCSLongs("warning-times.one-shot").orElseGet(UHCUtils::getDefaultTimes);
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
            Location location = new SwordHandler().spawnPlayerKiller(Main.getInstance().getGameInstance());
            String playerName = Main.getInstance().getConfig().getString("one-shot.target");
            if (playerName.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}")) {
                playerName = Bukkit.getPlayer(UUID.fromString(playerName)).getName();
            }
            UHCUtils.broadcastMessagewithSoundandTitle(Main.getInstance().getGameInstance(), ChatColor.GOLD.toString() + ChatColor.BOLD + "The Player Slayer is now in play!\nTarget: " + ChatColor.RESET + ChatColor.ITALIC + Bukkit.getPlayer(playerName).getDisplayName() + ChatColor.AQUA + "\nLocation: (" + ChatColor.RESET + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ChatColor.AQUA + ")","Player Slayer", ChatColor.GREEN + "now available", 0, 80, 40, "minecraft:entity.lightning_bolt.thunder",2,1);
            if (assignedID != null) { Bukkit.getScheduler().cancelTask(assignedID); }
            return;
        }
        if (silent) return;
        if (System.currentTimeMillis() >= end - nextWarn * 1000) {
            int timeTo = (int) nextWarn;
            if (!warningTimes.isEmpty()) {
                nextWarn = warningTimes.removeLast();
            } else {
                silent = true;
            }
            if (timeTo <= 10) {
                if (timeTo == 10) {
                    UHCUtils.broadcastChatMessage(ChatColor.RED.toString() + ChatColor.BOLD + "[Alert] " + ChatColor.RESET + "Player Slayer created in " + UHCUtils.secsToFormatString(timeTo));
                }
                UHCUtils.broadcastSoundandTitle(Main.getInstance().getGameInstance(), "minecraft:block.note_block.chime", (float) (0.5 + (10 - timeTo) / 40), 1.18F, String.valueOf(timeTo), ChatColor.RED + "Player Slayer summoned in", 0, 80, 40);
            } else {
                UHCUtils.broadcastMessageWithSound(ChatColor.YELLOW.toString() + ChatColor.BOLD + "[Warning] " + ChatColor.RESET + "Player Slayer blade created in " + UHCUtils.secsToFormatString(timeTo), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 0.4F);
            }
        }
    }

    public int schedule() {
        this.assignedID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), this, 0L, 10L);
        return assignedID;
    }
}
