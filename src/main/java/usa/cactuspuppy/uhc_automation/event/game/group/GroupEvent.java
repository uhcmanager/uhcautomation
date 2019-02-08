package usa.cactuspuppy.uhc_automation.event.game.group;

import lombok.Getter;
import org.bukkit.event.HandlerList;
import usa.cactuspuppy.uhc_automation.entity.unique.Group;
import usa.cactuspuppy.uhc_automation.event.game.GameEvent;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

public abstract class GroupEvent extends GameEvent {
    @Getter private Group group;

    public GroupEvent(GameInstance gameInstance, Group group) {
        super(gameInstance);
        this.group = group;
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
