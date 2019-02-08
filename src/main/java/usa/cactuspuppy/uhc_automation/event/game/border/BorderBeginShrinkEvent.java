package usa.cactuspuppy.uhc_automation.event.game.border;

import org.bukkit.event.HandlerList;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

public class BorderBeginShrinkEvent extends BorderEvent {
    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public BorderBeginShrinkEvent(GameInstance gameInstance) {
        super(gameInstance, BorderStatus.SHRINKING);
    }
}
