package usa.cactuspuppy.uhc_automation.game.tasks.countdowns;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.utils.FileIO;
import usa.cactuspuppy.uhc_automation.utils.Messaging;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ToInitCountdown implements Runnable {
    private static Map<Long, ToInitCountdown> countdownMap = new HashMap<>();

    private Integer assignedID;
    private GameInstance instance;
    private long initGameTime;
    private LinkedList<Long> titles;
    private LinkedList<Long> others;
    private long nextTitle;
    private long nextOther;

    public ToInitCountdown(int secsDelay, GameInstance gameInstance) {
        instance = gameInstance;
        countdownMap.put(instance.getGameID(), this);
        initGameTime = System.currentTimeMillis() + secsDelay * 1000;
        String rawLists = Main.getInstance().getConfig().getString("countdown.default", "");
        FileIO.CountdownList lists = FileIO.getList(rawLists);
        titles = new LinkedList<>(lists.getTitles());
        others = new LinkedList<>(lists.getOther());
        if (!titles.isEmpty()) nextTitle = titles.removeLast();
        if (!others.isEmpty()) nextOther = others.removeLast();
        Messaging.broadcastMessage(instance, ChatColor.RED.toString() + ChatColor.BOLD + "[ALERT] " + ChatColor.RESET + "Game begins in " + secsDelay + " seconds!");
        assignedID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), this, 0, 2L);
    }

    public static boolean isActive(long gameID) {
        return countdownMap.containsKey(gameID);
    }

    @Override
    public void run() {
        long timeTo = (System.currentTimeMillis() - initGameTime) / 1000;

        if (timeTo < 0) {
            instance.init();
            countdownMap.remove(instance.getGameID());
            Bukkit.getScheduler().cancelTask(assignedID);
            return;
        }

        if (timeTo < nextTitle) {
            String prefix = ChatColor.WHITE.toString();
            if (nextTitle <= 5) {
                prefix = ChatColor.RED.toString();
            }
            Messaging.broadcastTitle(instance, prefix + nextTitle, "Initiating match in...", 0, 30, 15);
            Messaging.broadcastSound(instance, Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 1.18F);
            if (!titles.isEmpty()) nextTitle = titles.removeLast();
        } else if (timeTo < nextOther) {
            Messaging.broadcastMessage(instance, ChatColor.RED.toString() + ChatColor.BOLD + "[ALERT] " + ChatColor.RESET + "Game begins in " + nextOther + " seconds!");
            Messaging.broadcastSound(instance, Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 1.18F);
            if (!others.isEmpty()) nextOther = others.removeLast();
        }
    }
}
