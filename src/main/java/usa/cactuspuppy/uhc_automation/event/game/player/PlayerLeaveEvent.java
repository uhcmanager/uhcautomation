package usa.cactuspuppy.uhc_automation.event.game.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

import java.util.UUID;

public class PlayerLeaveEvent extends PlayerEvent {

    public PlayerLeaveEvent(GameInstance gameInstance, UUID u) {
        super(gameInstance, u);
    }

    public PlayerLeaveEvent(GameInstance gameInstance, Player p) {
        super(gameInstance, p);
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
