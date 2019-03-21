package usa.cactuspuppy.uhc_automation.entity.tasks.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.entity.tasks.Task;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

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
        listeners.computeIfAbsent(gameInstance.getGameID(), k -> new ArrayList<>()).add(this);
        return true;
    }

    @Override
    public void cancel() {
        HandlerList.unregisterAll(this);
        listeners.computeIfAbsent(gameInstance.getGameID(), v -> new ArrayList<>()).remove(this);
    }
}
