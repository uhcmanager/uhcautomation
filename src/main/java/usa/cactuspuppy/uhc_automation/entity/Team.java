package usa.cactuspuppy.uhc_automation.entity;

import lombok.Getter;
import usa.cactuspuppy.uhc_automation.event.EventDistributor;
import usa.cactuspuppy.uhc_automation.event.game.team.TeamCreateEvent;
import usa.cactuspuppy.uhc_automation.event.game.team.TeamDeleteEvent;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

import java.util.*;

public class Team extends UniqueEntity {
    private static Map<Integer, Team> teamNumMap = new LinkedHashMap<>();
    private static int nextTeamNum = 0;

    @Getter private int teamNumber;
    @Getter private List<Group> groups;
    @Getter private String name;

    public Team(GameInstance gameInstance) {
        super(gameInstance);
        groups = new ArrayList<>();
        teamNumber = nextTeamNum;
        nextTeamNum = genNextTeamNum(nextTeamNum);
        EventDistributor.distributeEvent(new TeamCreateEvent(getGameInstance(), this));
    }

    public Team(GameInstance gameInstance, String name) {
        super(gameInstance);
        groups = new ArrayList<>();
        this.name = name;
        teamNumber = nextTeamNum;
        nextTeamNum = genNextTeamNum(nextTeamNum);
        EventDistributor.distributeEvent(new TeamCreateEvent(getGameInstance(), this));
    }

    public void addGroups(Group... groups) {
        int index = this.groups.size() + 1;
        for (Group g : groups) {
            this.groups.add(g);
            g.setTeam(this);
            g.setNum(index++);
        }
    }

    public void removeGroups(Group... groups) {
        this.groups.removeAll(Arrays.asList(groups));
        int index = 1;
        for (Group g : this.groups) {
            if (g.getNum() != index) g.setNum(index);
            index++;
        }
    }

    public void delete() {
        UniqueEntity.removeEntity(this);
        teamNumMap.remove(this.teamNumber);
        nextTeamNum = this.teamNumber;
        groups.forEach(Group::delete);
        EventDistributor.distributeEvent(new TeamDeleteEvent(getGameInstance(), this));
    }

    private int genNextTeamNum(int currTeamNum) {
        for (int i = currTeamNum; i < Collections.max(teamNumMap.keySet()); i++) {
            if (!teamNumMap.keySet().contains(i)) return i;
        }
        return Collections.max(teamNumMap.keySet()) + 1;
    }
}
