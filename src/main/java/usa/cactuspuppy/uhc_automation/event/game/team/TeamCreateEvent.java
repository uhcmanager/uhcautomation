package usa.cactuspuppy.uhc_automation.event.game.team;

import usa.cactuspuppy.uhc_automation.entity.Team;
import usa.cactuspuppy.uhc_automation.event.game.GameEvent;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

public class TeamCreateEvent extends TeamEvent {
    public TeamCreateEvent(GameInstance gameInstance, Team t) {
        super(gameInstance, t);
    }
}
