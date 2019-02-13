package usa.cactuspuppy.uhc_automation.game.types;

import org.bukkit.World;
import usa.cactuspuppy.uhc_automation.entity.unique.Team;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

import java.util.HashSet;
import java.util.Set;

public abstract class TeamGameInstance extends GameInstance {

    private Set<Team> teams = new HashSet<>();

    public TeamGameInstance(String name, World world) {
        super(name, world);
    }

    @Override
    protected void reset() {
        //TODO: Reset team game
    }
}
