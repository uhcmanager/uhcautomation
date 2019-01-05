package usa.cactuspuppy.uhc_automation.event.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

@AllArgsConstructor
public abstract class GameEvent {
    @Getter private GameInstance gameInstance;
}
