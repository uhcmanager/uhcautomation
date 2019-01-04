package usa.cactuspuppy.uhc_automation.event.events.player;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.GameInstance;
import usa.cactuspuppy.uhc_automation.event.events.Event;

import java.util.UUID;

public abstract class PlayerEvent extends Event {
    @Getter private UUID uniqueID;

    public PlayerEvent(GameInstance gameInstance, UUID u) {
        super(gameInstance);
        uniqueID = u;
    }

    public PlayerEvent(GameInstance gameInstance, Player p) {
        super(gameInstance);
        uniqueID = p.getUniqueId();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uniqueID);
    }
}
