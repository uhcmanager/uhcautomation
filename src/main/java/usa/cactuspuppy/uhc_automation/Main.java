package usa.cactuspuppy.uhc_automation;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.permission.ChildPermission;
import org.bukkit.plugin.java.annotation.permission.Permission;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.LogPrefix;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

@Plugin(name = "UHCAutomation", version = "2.0")
@Description("Provides the ability to create multiple concurrent game instances in different worlds with different configurations")
@Author("CactusPuppy")
@LogPrefix("UHCAuto")
@Permission(name = "uhc.*", desc = "Wildcard permission", children = {@ChildPermission(name = "uhc.manager")})
@Permission(name = "uhc.manager", desc = "Allows game management", defaultValue = PermissionDefault.OP)
public class Main extends JavaPlugin {
    @Getter private static Main instance;
    /** Time at which the plugin was last disabled, used to restart in-progress games */
    @Getter private static long lastDisable;
    /** Time at which this plugin was enabled, used to restart in-progress games */
    @Getter private static long enabled;

    @Override
    public void onEnable() {
        long start = System.nanoTime();
        instance = this;
        boolean success = initBase();
        if (!success) {
            Logger.logError(this.getClass(), "Failure to initiate base plugin, disabling...");
            Bukkit.getPluginManager().disablePlugin(this);
            return; // this probably never gets hit but whatever
        }
        File lastDisableFile = new File(getDataFolder(), Constants.getLastDisableFile());
        if (lastDisableFile.isFile()) {
            try {
                Scanner scan = new Scanner(lastDisableFile);
                lastDisable = scan.nextLong();
                scan.close();
            } catch (FileNotFoundException e) {
                Logger.logWarning(this.getClass(), "Scanner could not find last disabled time, cannot assess downtime.");
                lastDisable = -1;
            }
        } else lastDisable = -1;
        enabled = System.currentTimeMillis();
        long elapsed = System.nanoTime() - start;
        Logger.logInfo(this.getClass(), String.format("UHC Automation startup complete in %1$.2fms (%2$dns).", elapsed / 1000.0, elapsed));
    }

    public void onRestart() {
        //TODO: restart
    }

    @Override
    public void onDisable() {
        //TODO: Save disable time
    }

    boolean initBase() {
        //TODO: Initiate base plugin
        Logger.setOutput(getLogger());
        //TODO: Set logger in Logger
        return true;
    }
}
