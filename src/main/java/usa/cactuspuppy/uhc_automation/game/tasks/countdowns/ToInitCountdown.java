package usa.cactuspuppy.uhc_automation.game.tasks.countdowns;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

import java.util.HashMap;
import java.util.Map;

public class ToInitCountdown implements Runnable {
    private static Map<Long, ToInitCountdown> countdownMap = new HashMap<>();

    private GameInstance instance;
    private long initTime;

    public ToInitCountdown(int secsDelay, GameInstance gameInstance) {
        instance = gameInstance;
        countdownMap.put(instance.getGameID(), this);
        initTime = System.currentTimeMillis() + secsDelay * 1000;
    }

    @Override
    public void run() {

    }
}
