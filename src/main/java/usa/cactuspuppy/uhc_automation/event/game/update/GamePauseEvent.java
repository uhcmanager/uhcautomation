package usa.cactuspuppy.uhc_automation.event.game.update;

import usa.cactuspuppy.uhc_automation.event.game.GameEvent;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

public class GamePauseEvent extends GameEvent {
    public GamePauseEvent(GameInstance gameInstance) {
        super(gameInstance);
    }
}
