package usa.cactuspuppy.uhc_automation.event.game.team;

import usa.cactuspuppy.uhc_automation.entity.Team;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

public class TeamDeleteEvent extends TeamEvent {
    public TeamDeleteEvent(GameInstance gameInstance, Team team) {
        super(gameInstance, team);
    }
}
