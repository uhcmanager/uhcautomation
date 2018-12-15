package usa.cactuspuppy.uhc_automation;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.util.Optional;

public class Main extends JavaPlugin {
    @Getter private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        boolean success = initBase();
        if (!success) {
            Logger.logError(this.getClass(), "Failure to initiate base plugin", Optional.empty());
            return;
        }
    }

    @Override
    public void onDisable() {

    }

    private boolean initBase() {

    }
}
