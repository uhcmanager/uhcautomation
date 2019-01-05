package usa.cactuspuppy.uhc_automation.event.game.team;

import usa.cactuspuppy.uhc_automation.event.game.GameEvent;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

public class TeamCreateEvent extends GameEvent {
    public TeamCreateEvent(GameInstance gameInstance) {
        super(gameInstance);
    }
}
