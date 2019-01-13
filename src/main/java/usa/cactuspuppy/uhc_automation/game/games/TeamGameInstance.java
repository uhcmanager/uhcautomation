package usa.cactuspuppy.uhc_automation.game.games;

import lombok.Getter;
import usa.cactuspuppy.uhc_automation.entity.unique.Team;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

import java.util.HashSet;
import java.util.Set;

public class TeamGameInstance extends GameInstance {
    @Getter
    protected Set<Team> teamSet = new HashSet<>();

    public TeamGameInstance(GameInfo gameInfo) {
        super(gameInfo);
        gameInfo.setTeamMode(true);
    }

    public void addTeam(Team team) {  teamSet.add(team);  }

    public void removeTeam(Team team) { teamSet.remove(team); }
}
