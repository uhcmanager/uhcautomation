package usa.cactuspuppy.uhc_automation.event.game.group;

import usa.cactuspuppy.uhc_automation.entity.unique.Group;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class GroupAddPlayersEvent extends GroupEvent {
    private Set<UUID> uuids;

    public GroupAddPlayersEvent(GameInstance gameInstance, Group group, UUID... u) {
        super(gameInstance, group);
        uuids = Arrays.stream(u).collect(Collectors.toSet());
    }

    public Set<UUID> getUuids() {
        return new HashSet<>(uuids);
    }
}
