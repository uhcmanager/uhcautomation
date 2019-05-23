package usa.cactuspuppy.uhc_automation.event.game.team;

import org.bukkit.event.HandlerList;
import usa.cactuspuppy.uhc_automation.game.entity.unique.Group;
import usa.cactuspuppy.uhc_automation.game.entity.unique.UHCTeam;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TeamAddGroupsEvent extends TeamEvent {
    private List<Group> groups;

    public TeamAddGroupsEvent(GameInstance gameInstance, UHCTeam team, Group... groups) {
        super(gameInstance, team);
        this.groups = Arrays.stream(groups).collect(Collectors.toList());
    }

    public List<Group> getGroups() {
        return new ArrayList<>(groups);
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
