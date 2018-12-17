package usa.cactuspuppy.uhc_automation;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.util.Optional;

public class Main extends JavaPlugin {
    @Getter private static Main instance;

    @Override
    public void onEnable() {
        long start = System.nanoTime();
        instance = this;
        boolean success = initBase();
        if (!success) {
            Logger.logError(this.getClass(), "Failure to initiate base plugin", Optional.empty());
            return;
        }
        long elapsed = System.nanoTime() - start;
        Logger.logInfo(this.getClass(), String.format("UHC Automation startup complete in %1$.2f ms (%2$d ns).", elapsed / 1000.0, elapsed));
    }

    @Override
    public void onDisable() {

    }

    private boolean initBase() {
        //TODO: Initiate base plugin
        return true;
    }
}
