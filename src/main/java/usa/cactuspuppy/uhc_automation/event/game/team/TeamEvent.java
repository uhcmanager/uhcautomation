package usa.cactuspuppy.uhc_automation.event.game.team;

import lombok.Getter;
import usa.cactuspuppy.uhc_automation.entity.unique.UHCTeam;
import usa.cactuspuppy.uhc_automation.event.game.GameEvent;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

public abstract class TeamEvent extends GameEvent {
    @Getter private UHCTeam team;

    public TeamEvent(GameInstance gameInstance, UHCTeam team) {
        super(gameInstance);
        this.team = team;
    }
}
