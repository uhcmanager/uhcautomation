package usa.cactuspuppy.uhc_automation.entity.tasks.listeners;

import usa.cactuspuppy.uhc_automation.entity.tasks.Task;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

import java.util.HashMap;
import java.util.Map;

public abstract class ListenerTask extends Task {
    private static Map<Long, ListenerTask> listeners = new HashMap<>();

    public ListenerTask(GameInstance gameInstance) {
        super(gameInstance);
    }
}
