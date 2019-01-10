package usa.cactuspuppy.uhc_automation.game.tasks.countdowns;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.utils.Messaging;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class LoadChunksCountdown implements Runnable {
    private static Map<Long, LoadChunksCountdown> countdownMap = new HashMap<>();

    private Integer assignedID;
    private GameInstance instance;
    private long releaseTime;

    public LoadChunksCountdown(GameInstance gameInstance) {
        instance = gameInstance;
        countdownMap.put(instance.getGameID(), this);
        releaseTime = System.currentTimeMillis() + 30 * 1000;
        assignedID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), this, 0, 2L);
        Messaging.broadcastTitle(instance, "Loading...",  ChatColor.YELLOW + "Initiating match...", 0, 400, 15);
    }

    public static boolean isActive(long gameID) {
        return countdownMap.containsKey(gameID);
    }

    @Override
    public void run() {
        long timeTo = (System.currentTimeMillis() - releaseTime) / 1000;

        if (timeTo < 0) {
            instance.init();
            countdownMap.remove(instance.getGameID());
            Bukkit.getScheduler().cancelTask(assignedID);
            return;
        }

        if (timeTo + 1 <= 10) {
            boolean three = timeTo + 1 <= 3;
            Messaging.broadcastTitle(instance, (three ? ChatColor.RED : ChatColor.WHITE) + "" + (timeTo + 1), "Starting in...", 0, 30,15);
        } else {
            Messaging.broadcastTitle(instance, "Loading...",  ChatColor.YELLOW + "Initiating match...", 0, 400, 15);
        }
    }
}
