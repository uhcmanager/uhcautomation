package usa.cactuspuppy.uhc_automation.event.events.player;

import lombok.Getter;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.GameInstance;

import java.util.UUID;

public class PlayerDeathEvent extends PlayerEvent {
    /**
     * Whether or not this death removes the player from the game
     */
    @Getter private boolean isFinal;

    public PlayerDeathEvent(GameInstance gameInstance, UUID u) {
        super(gameInstance, u);
    }

    public PlayerDeathEvent(GameInstance gameInstance, Player p) {
        super(gameInstance, p);
    }
}
