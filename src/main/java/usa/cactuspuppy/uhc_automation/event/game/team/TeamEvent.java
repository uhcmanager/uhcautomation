package usa.cactuspuppy.uhc_automation.event.game.team;

import lombok.Getter;
import usa.cactuspuppy.uhc_automation.entity.unique.Team;
import usa.cactuspuppy.uhc_automation.event.game.GameEvent;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

public abstract class TeamEvent extends GameEvent {
    @Getter private Team team;

    public TeamEvent(GameInstance gameInstance, Team team) {
        super(gameInstance);
        this.team = team;
    }
}
