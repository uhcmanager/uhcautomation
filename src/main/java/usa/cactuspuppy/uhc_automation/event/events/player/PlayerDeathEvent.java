package usa.cactuspuppy.uhc_automation.event.events.player;

import lombok.Getter;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.GameInstance;
import usa.cactuspuppy.uhc_automation.event.events.player.PlayerEvent;

import java.util.UUID;

public class PlayerDeathEvent extends PlayerEvent {
    @Getter private boolean isFinal;

    public PlayerDeathEvent(GameInstance gameInstance, UUID u) {
        super(gameInstance, u);
    }

    public PlayerDeathEvent(GameInstance gameInstance, Player p) {
        super(gameInstance, p);
    }
}
