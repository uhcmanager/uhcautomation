package usa.cactuspuppy.uhc_automation.game.types;

import org.bukkit.ChatColor;
import org.bukkit.World;
import usa.cactuspuppy.uhc_automation.entity.unique.UHCTeam;

public class PVN extends TeamGameInstance {

    public PVN(World world) {
        super("Pirates vs. Ninjas UHC", world);
        UHCTeam pirates = new UHCTeam(this, "Pirates");
        pirates.setColor(ChatColor.YELLOW);
        addTeam(pirates, true);
        UHCTeam ninjas = new UHCTeam(this, "Ninjas");
        ninjas.setColor(ChatColor.LIGHT_PURPLE);
        addTeam(ninjas, true);
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
