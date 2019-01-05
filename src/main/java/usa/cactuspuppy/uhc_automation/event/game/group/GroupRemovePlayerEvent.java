package usa.cactuspuppy.uhc_automation.event.game.group;

import lombok.Getter;
import usa.cactuspuppy.uhc_automation.entity.unique.Group;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

import java.util.UUID;

public class GroupRemovePlayerEvent extends GroupEvent {
    @Getter private UUID uuid;

    public GroupRemovePlayerEvent(GameInstance gameInstance, Group group, UUID u) {
        super(gameInstance, group);
        uuid = u;
    }
}
