package usa.cactuspuppy.uhc_automation.event.game.group;

import org.bukkit.event.HandlerList;
import usa.cactuspuppy.uhc_automation.game.entity.unique.Group;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

public class GroupDeleteEvent extends GroupEvent {
    public GroupDeleteEvent(GameInstance gameInstance, Group group) {
        super(gameInstance, group);
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
