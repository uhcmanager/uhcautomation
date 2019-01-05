package usa.cactuspuppy.uhc_automation.event.game.update;

import usa.cactuspuppy.uhc_automation.game.GameInstance;

public class GameStartEvent extends GameEvent {
    public GameStartEvent(GameInstance gameInstance) {
        super(gameInstance);
    }
}
