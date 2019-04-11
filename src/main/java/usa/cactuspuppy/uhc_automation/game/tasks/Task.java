package usa.cactuspuppy.uhc_automation.game.tasks;

import usa.cactuspuppy.uhc_automation.game.entity.Entity;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

/**
 * Wrapper for spinoff tasks that must be executed over multiple ticks.
 * These tasks must be created to avoid locking up the server thread on a single tick.
 */
public abstract class Task extends Entity {
    public Task(GameInstance gameInstance) {
        super(gameInstance);
    }

    /**
     * Execute everything that must be done when initiating this task
     * @return Whether the task was successfully initiated.
     */
    public abstract boolean init();

    /**
     * Execute everything that must be done when halting this task
     */
    public abstract void cancel();
}
