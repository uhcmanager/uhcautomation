package usa.cactuspuppy.uhc_automation.event.game.player;

import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.GameInstance;

import java.util.UUID;

public class PlayerLeaveEvent extends PlayerEvent {

    public PlayerLeaveEvent(GameInstance gameInstance, UUID u) {
        super(gameInstance, u);
    }

    public PlayerLeaveEvent(GameInstance gameInstance, Player p) {
        super(gameInstance, p);
    }
}
