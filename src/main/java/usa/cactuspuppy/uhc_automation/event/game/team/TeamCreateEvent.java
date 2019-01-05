package usa.cactuspuppy.uhc_automation.event.game.team;

import usa.cactuspuppy.uhc_automation.entity.unique.Team;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

public class TeamCreateEvent extends TeamEvent {
    public TeamCreateEvent(GameInstance gameInstance, Team t) {
        super(gameInstance, t);
    }
}
