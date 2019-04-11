package usa.cactuspuppy.uhc_automation.game.types;

import org.bukkit.World;
import usa.cactuspuppy.uhc_automation.game.entity.unique.UHCTeam;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

import java.util.*;

public abstract class TeamGameInstance extends GameInstance {

    protected Map<String, UHCTeam> teams = new HashMap<>();

    public TeamGameInstance(String name, World world) {
        super(world);
        setName(name);
    }

    public Map<String, UHCTeam> getTeams() {
        return new HashMap<>(teams);
    }

    /**
     * Adds a team to this GameInstance
     * @param team Team to add
     * @param overwrite Whether the GameInstance should overwrite the team previously under this name, if any.
     * @return Whether the team was successfully added. Always true if overwrite is true.
     */
    public boolean addTeam(UHCTeam team, boolean overwrite) {
        String name = team.getName();
        if (teams.containsKey(name) && !overwrite) { return false; }
        teams.put(name, team);

        return true;
    }

    public void removeTeam(UHCTeam team) {
        teams.remove(team.getName());
    }
}
