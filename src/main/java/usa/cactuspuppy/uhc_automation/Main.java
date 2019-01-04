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
        Logger.logInfo(this.getClass(), String.format("UHC Automation startup complete in %1$.2fms (%2$dns).", elapsed / 1000.0, elapsed));
    }

    public void onRestart() {
        //TODO: restart
    }

    @Override
    public void onDisable() {

    }

    boolean initBase() {
        //TODO: Initiate base plugin
        return true;
    }
}
