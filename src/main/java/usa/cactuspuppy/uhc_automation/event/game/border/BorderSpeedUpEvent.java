package usa.cactuspuppy.uhc_automation.event.game.border;

import org.bukkit.event.HandlerList;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

public class BorderSpeedUpEvent extends BorderEvent {
    public BorderSpeedUpEvent(GameInstance gameInstance, BorderStatus status) {
        super(gameInstance, status);
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
