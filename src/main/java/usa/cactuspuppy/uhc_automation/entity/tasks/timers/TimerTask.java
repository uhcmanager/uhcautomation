package usa.cactuspuppy.uhc_automation.entity.tasks.timers;

import org.bukkit.Bukkit;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.entity.tasks.Task;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TimerTask extends Task implements Runnable {
    private static Map<Long, List<Integer>> timers = new HashMap<>();

    /**
     * The task ID assigned to this task by the Bukkit scheduler
     */
    protected Integer taskID;
    /**
     * Whether this task should repeat or run once
     */
    protected boolean repeating;
    /**
     * How many server ticks to wait before initiating
     */
    protected long initDelay;
    /**
     * If repeating, how many server ticks per run.
     */
    protected long repeatDelay;

    public TimerTask(GameInstance gameInstance, boolean repeating, long initDelay, long repeatDelay) {
        super(gameInstance);
        this.repeating = repeating;
        this.initDelay = initDelay;
        this.repeatDelay = repeatDelay;
    }

    /**
     * Get a copy of the list of timers associated with an instance
     * @param instance GameInstance whose timers are fetched
     * @return A list of taskIDs assosciated with the GameInstance
     */
    public static List<Integer> getInstanceTimers(GameInstance instance) {
        return new ArrayList<>(timers.get(instance.getGameID()));
    }

    /**
     * Cancel and clear all timers associated with an instance
     * @param instance GameInstance whose timers should be cleared
     */
    public static void clearInstanceTimers(GameInstance instance) {
        List<Integer> instTimers = timers.get(instance.getGameID());
        for (int i : instTimers) {
            Bukkit.getScheduler().cancelTask(i);
        }
        timers.put(instance.getGameID(), new ArrayList<>());
    }

    @Override
    public boolean init() {
        if (repeating) {
            taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), this, initDelay, repeatDelay);
        } else {
            taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), this, initDelay);
        }
        if (taskID != -1) {
            timers.computeIfAbsent(gameInstance.getGameID(), k -> new ArrayList<>());
            timers.get(gameInstance.getGameID()).add(taskID);
            return true;
        }
        return false;
    }

    @Override
    public void cancel() {
        Bukkit.getScheduler().cancelTask(taskID);
        timers.get(gameInstance.getGameID()).remove(taskID);
    }
}
