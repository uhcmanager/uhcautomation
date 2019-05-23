package usa.cactuspuppy.uhc_automation.event.game.team;

import org.bukkit.event.HandlerList;
import usa.cactuspuppy.uhc_automation.game.entity.unique.UHCTeam;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

public class TeamCreateEvent extends TeamEvent {
    public TeamCreateEvent(GameInstance gameInstance, UHCTeam t) {
        super(gameInstance, t);
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
