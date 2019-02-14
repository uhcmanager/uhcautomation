package usa.cactuspuppy.uhc_automation.game.types;

import lombok.Getter;
import org.bukkit.World;
import org.bukkit.scoreboard.Scoreboard;
import usa.cactuspuppy.uhc_automation.entity.unique.Team;

public class PVN extends TeamGameInstance {

    public PVN(String name, World world) {
        super(name, world);
        addTeam(new Team(this, "Pirates"));
        addTeam(new Team(this, "Ninjas"));
    }

    @Override
    protected void reset() {

    }

    @Override
    protected void init() {

    }

    @Override
    protected void start() {

    }

    @Override
    protected void pause() {

    }

    @Override
    protected void resume() {

    }

    @Override
    protected void end() {

    }
}
