package usa.cactuspuppy.uhc_automation.event.events;

import lombok.Getter;
import usa.cactuspuppy.uhc_automation.GameInstance;

import java.util.UUID;

public class PlayerJoinEvent extends GameEvent {
    @Getter private UUID uuid;

    public PlayerJoinEvent(GameInstance gameInstance, UUID u) {
        super(gameInstance);
        uuid = u;
    }
}
