package usa.cactuspuppy.uhc_automation.game.types;

import org.bukkit.ChatColor;
import org.bukkit.World;
import usa.cactuspuppy.uhc_automation.game.entity.unique.UHCTeam;

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
    protected boolean reset() {
        return true;
    }

    @Override
    protected boolean init() {
        return true;
    }

    @Override
    protected boolean start() {
        return true;
    }

    @Override
    protected boolean pause() {
        return true;
    }

    @Override
    protected boolean resume() {
        return true;
    }

    @Override
    protected boolean end() {
        return true;
    }
}
