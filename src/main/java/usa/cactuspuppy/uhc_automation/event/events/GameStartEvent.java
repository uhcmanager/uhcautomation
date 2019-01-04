package usa.cactuspuppy.uhc_automation.event.events;

import usa.cactuspuppy.uhc_automation.GameInstance;

public class GameStartEvent extends GameEvent {
    public GameStartEvent(GameInstance gameInstance) {
        super(gameInstance);
    }
}
