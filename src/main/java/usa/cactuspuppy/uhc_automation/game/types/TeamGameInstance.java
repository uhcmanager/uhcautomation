package usa.cactuspuppy.uhc_automation.game.types;

import org.bukkit.World;
import org.bukkit.scoreboard.Scoreboard;
import usa.cactuspuppy.uhc_automation.entity.unique.Team;
import usa.cactuspuppy.uhc_automation.entity.util.ScoreboardSet;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class TeamGameInstance extends GameInstance {

    protected Map<String, Team> teams = new HashMap<>();

    public TeamGameInstance(String name, World world) {
        super(name, world);
    }

    public Map<String, Team> getTeams() {
        return new HashMap<>(teams);
    }

    /**
     * Adds a team to this GameInstance
     * @param team Team to add
     * @param overwrite Whether the GameInstance should overwrite the team previously under this name, if any.
     * @return Whether the team was successfully added. Always true if overwrite is true.
     */
    public boolean addTeam(Team team, boolean overwrite) {
        String name = team.getName();
        if (teams.containsKey(name) && !overwrite) { return false; }
        teams.put(name, team);

        return true;
    }

    public void removeTeam(Team team) {
        teams.remove(team);
    }
}
