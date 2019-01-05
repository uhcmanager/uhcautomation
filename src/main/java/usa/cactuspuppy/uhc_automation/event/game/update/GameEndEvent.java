package usa.cactuspuppy.uhc_automation.event.game.update;

import lombok.Getter;
import usa.cactuspuppy.uhc_automation.event.game.GameEvent;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

public class GameEndEvent extends GameEvent {
    /** Whether the game was prematurely terminated */
    @Getter private boolean forced;

    public GameEndEvent(GameInstance gameInstance, boolean forced) {
        super(gameInstance);
        this.forced = forced;
    }
}
