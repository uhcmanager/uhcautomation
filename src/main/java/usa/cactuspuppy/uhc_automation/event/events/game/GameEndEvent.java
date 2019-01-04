package usa.cactuspuppy.uhc_automation.event.events.game;

import usa.cactuspuppy.uhc_automation.GameInstance;
import usa.cactuspuppy.uhc_automation.event.events.Event;

public class GameEndEvent extends Event {

    public GameEndEvent(GameInstance gameInstance, boolean victory) {
        super(gameInstance);
    }
}
