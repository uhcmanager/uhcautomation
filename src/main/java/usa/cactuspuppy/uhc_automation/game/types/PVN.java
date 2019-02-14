package usa.cactuspuppy.uhc_automation.game.types;

import org.bukkit.World;
import usa.cactuspuppy.uhc_automation.entity.unique.Team;

public class PVN extends TeamGameInstance {

    public PVN(String name, World world) {
        super(name, world);
        teams.add(new Team(this, "Pirates"));
        teams.add(new Team(this, "Ninjas"));
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
