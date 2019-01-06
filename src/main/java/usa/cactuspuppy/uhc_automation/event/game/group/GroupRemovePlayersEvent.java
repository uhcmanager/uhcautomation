package usa.cactuspuppy.uhc_automation.event.game.group;

import usa.cactuspuppy.uhc_automation.entity.unique.Group;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class GroupRemovePlayersEvent extends GroupEvent {
    private Set<UUID> uuids;


    public GroupRemovePlayersEvent(GameInstance gameInstance, Group group, UUID... u) {
        super(gameInstance, group);
        uuids = Arrays.stream(u).collect(Collectors.toSet());
    }

    public Set<UUID> getUuids() {
        return new HashSet<>(uuids);
    }
}
