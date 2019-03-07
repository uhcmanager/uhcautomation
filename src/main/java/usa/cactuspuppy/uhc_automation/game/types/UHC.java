package usa.cactuspuppy.uhc_automation.game.types;

import org.bukkit.World;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.utils.UHCUtils;

public class UHC extends GameInstance {
    public UHC(World world) {
        super(world);
    }

    //TODO: Implement methods
    @Override
    protected void reset() {

    }

    @Override
    protected void init() {
        boolean spreadSuccess = UHCUtils.spreadplayers(this);
        if (!spreadSuccess) {
            //TODO: Broadcast failure
            reset();
        }
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
