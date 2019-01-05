package usa.cactuspuppy.uhc_automation.event.game.group;

import lombok.Getter;
import usa.cactuspuppy.uhc_automation.entity.unique.Group;
import usa.cactuspuppy.uhc_automation.event.game.GameEvent;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

public abstract class GroupEvent extends GameEvent {
    @Getter private Group group;

    public GroupEvent(GameInstance gameInstance, Group group) {
        super(gameInstance);
        this.group = group;
    }
}
