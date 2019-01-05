package usa.cactuspuppy.uhc_automation.event.game.group;

import lombok.Getter;
import usa.cactuspuppy.uhc_automation.entity.Group;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

public class GroupMergeEvent extends GroupEvent {
    @Getter private Group other;

    public GroupMergeEvent(GameInstance gameInstance, Group group, Group other) {
        super(gameInstance, group);
        this.other = other;
    }
}
