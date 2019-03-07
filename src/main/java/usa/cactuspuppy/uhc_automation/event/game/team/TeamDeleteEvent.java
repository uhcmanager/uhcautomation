package usa.cactuspuppy.uhc_automation.event.game.team;

import org.bukkit.event.HandlerList;
import usa.cactuspuppy.uhc_automation.entity.unique.UHCTeam;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

public class TeamDeleteEvent extends TeamEvent {
    public TeamDeleteEvent(GameInstance gameInstance, UHCTeam team) {
        super(gameInstance, team);
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
