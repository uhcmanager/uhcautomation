package usa.cactuspuppy.uhc_automation.game.tasks.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ListenerTask extends Task implements Listener {
    private static Map<Long, List<ListenerTask>> listeners = new HashMap<>();

    public ListenerTask(GameInstance gameInstance) {
        super(gameInstance);
    }

    @Override
    public boolean init() {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
        listeners.computeIfAbsent(gameInstance, k -> new ArrayList<>()).add(this);
        return true;
    }

    @Override
    public void cancel() {
        HandlerList.unregisterAll(this);
        listeners.computeIfAbsent(gameInstance, v -> new ArrayList<>()).remove(this);
    }

    /**
     * Get a copy of the list of listeners associated with an instance
     *
     * @param instance GameInstance whose listeners are fetched
     * @return A list of listeners associated with the GameInstance
     */
    public static List<ListenerTask> getInstanceListeners(GameInstance instance) {
        return new ArrayList<>(listeners.computeIfAbsent(instance.getGameID(), k -> new ArrayList<>()));
    }

    /**
     * Unregister and remove all listeners associated with an instance
     *
     * @param instance GameInstance whose listeners should be removed
     */
    public static void clearInstanceListeners(GameInstance instance) {
        for (ListenerTask l : listeners.computeIfAbsent(instance.getGameID(), k -> new ArrayList<>())) {
            HandlerList.unregisterAll(l);
        }
        listeners.put(instance.getGameID(), new ArrayList<>());
    }
}
