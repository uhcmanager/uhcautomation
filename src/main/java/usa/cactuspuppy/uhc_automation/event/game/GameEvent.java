package usa.cactuspuppy.uhc_automation.event.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

@AllArgsConstructor
public abstract class GameEvent extends Event {
    @Getter private GameInstance gameInstance;
}
