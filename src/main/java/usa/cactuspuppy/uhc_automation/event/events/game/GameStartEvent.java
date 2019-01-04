package usa.cactuspuppy.uhc_automation.event.events.game;

import usa.cactuspuppy.uhc_automation.GameInstance;
import usa.cactuspuppy.uhc_automation.event.events.Event;

public class GameStartEvent extends Event {
    public GameStartEvent(GameInstance gameInstance) {
        super(gameInstance);
    }
}
