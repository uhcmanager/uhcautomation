package usa.cactuspuppy.uhc_automation.event.game.update;

import lombok.Getter;
import org.bukkit.event.HandlerList;
import usa.cactuspuppy.uhc_automation.event.game.GameEvent;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

public class GameEndEvent extends GameEvent {
    /**
     * Whether the game was prematurely terminated
     */
    @Getter
    private boolean forced;

    public GameEndEvent(GameInstance gameInstance, boolean forced) {
        super(gameInstance);
        this.forced = forced;
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
