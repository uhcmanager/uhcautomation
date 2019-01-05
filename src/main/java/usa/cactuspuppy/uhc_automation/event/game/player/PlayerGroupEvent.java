package usa.cactuspuppy.uhc_automation.event.game.player;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

import java.util.UUID;

public class PlayerGroupEvent extends PlayerEvent {
    @Getter private UUID otherUUID;

    public PlayerGroupEvent(GameInstance gameInstance, UUID u, UUID other) {
        super(gameInstance, u);
        otherUUID = other;
    }

    public PlayerGroupEvent(GameInstance gameInstance, Player p, Player other) {
        super(gameInstance, p);
        otherUUID = other.getUniqueId();
    }

    public Player getOtherPlayer() {
        return Bukkit.getPlayer(otherUUID);
    }
}