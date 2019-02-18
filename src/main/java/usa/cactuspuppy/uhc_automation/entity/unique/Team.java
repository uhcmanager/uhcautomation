package usa.cactuspuppy.uhc_automation.entity.unique;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import usa.cactuspuppy.uhc_automation.event.game.team.TeamAddGroupsEvent;
import usa.cactuspuppy.uhc_automation.event.game.team.TeamCreateEvent;
import usa.cactuspuppy.uhc_automation.event.game.team.TeamDeleteEvent;
import usa.cactuspuppy.uhc_automation.event.game.team.TeamRemoveGroupsEvent;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.types.TeamGameInstance;

import java.util.*;

public class Team extends UniqueEntity {
    private static Map<Integer, Team> teamNumMap = new LinkedHashMap<>();
    private static int nextTeamNum = 0;

    @Getter private int teamNumber;
    @Getter private List<Group> groups;
    @Getter private String name;
    @Getter @Setter private Color color;

    public Team(TeamGameInstance gameInstance) {
        super(gameInstance);
        //TODO: Add team to team game instance
        groups = new ArrayList<>();
        teamNumber = nextTeamNum;
        nextTeamNum = genNextTeamNum(nextTeamNum);
        Bukkit.getServer().getPluginManager().callEvent(new TeamCreateEvent(getGameInstance(), this));
    }

    public Team(TeamGameInstance gameInstance, String name) {
        super(gameInstance);
        //TODO: Add team to team game instance
        groups = new ArrayList<>();
        this.name = name;
        teamNumber = nextTeamNum;
        nextTeamNum = genNextTeamNum(nextTeamNum);
        Bukkit.getServer().getPluginManager().callEvent(new TeamCreateEvent(getGameInstance(), this));
    }

    public Set<UUID> getPlayers() {
        return groups.stream().map(Group::getPlayers).reduce((uuids, uuids2) -> {
            uuids.addAll(uuids2);
            return uuids;
        }).orElse(new HashSet<>());
    }

    public int size() {
        return getPlayers().size();
    }

    public void addGroups(Group... groups) {
        int index = this.groups.size() + 1;
        for (Group g : groups) {
            this.groups.add(g);
            g.setTeam(this);
            g.setNum(index++);
        }
        Bukkit.getServer().getPluginManager().callEvent(new TeamAddGroupsEvent(getGameInstance(), this, groups));
    }

    public void removeGroups(Group... groups) {
        this.groups.removeAll(Arrays.asList(groups));
        int index = 1;
        for (Group g : this.groups) {
            if (g.getNum() != index) g.setNum(index);
            index++;
        }
        Bukkit.getServer().getPluginManager().callEvent(new TeamRemoveGroupsEvent(getGameInstance(), this, groups));
    }

    public void delete() {
        UniqueEntity.removeEntity(this);
        teamNumMap.remove(this.teamNumber, this);
        nextTeamNum = this.teamNumber;
        groups.forEach(Group::delete);
        Bukkit.getServer().getPluginManager().callEvent(new TeamDeleteEvent(getGameInstance(), this));
    }

    private int genNextTeamNum(int currTeamNum) {
        for (int i = currTeamNum; i < Collections.max(teamNumMap.keySet()); i++) {
            if (!teamNumMap.keySet().contains(i)) return i;
        }
        return Collections.max(teamNumMap.keySet()) + 1;
    }
}
