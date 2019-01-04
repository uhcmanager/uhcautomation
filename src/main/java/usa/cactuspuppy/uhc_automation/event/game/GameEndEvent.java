package usa.cactuspuppy.uhc_automation.event.game;

import lombok.Getter;
import usa.cactuspuppy.uhc_automation.GameInstance;

public class GameEndEvent extends GameEvent {
    /** Whether the game was prematurely terminated */
    @Getter private boolean forced;

    public GameEndEvent(GameInstance gameInstance, boolean forced) {
        super(gameInstance);
        this.forced = forced;
    }
}
