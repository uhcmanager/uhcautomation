package usa.cactuspuppy.uhc_automation.event.game.group;

import usa.cactuspuppy.uhc_automation.entity.unique.Group;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

public class GroupCreateEvent extends GroupEvent {
    public GroupCreateEvent(GameInstance gameInstance, Group group) {
        super(gameInstance, group);
    }
}
