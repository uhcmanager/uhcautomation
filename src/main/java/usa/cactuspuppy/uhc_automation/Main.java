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
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.GameManager;
import usa.cactuspuppy.uhc_automation.game.info.GameInfo;
import usa.cactuspuppy.uhc_automation.game.types.GameType;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
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
        //TODO: Save all active game info
    }

    boolean initBase() {
        //TODO: Initiate base plugin
        //Set logger
        Logger.setOutput(getLogger());
        //Re-initiate all saved games
        File gamesDir = new File(getDataFolder() + Constants.getGamesDir());

        if (!gamesDir.isDirectory() && !gamesDir.mkdirs()) {
            Logger.logWarning(this.getClass(), "Could not find or create game info saving directory, game loading/saving impossible!");
        } else  {
            File[] files = gamesDir.listFiles();
            if (files == null) return true;
            for (File f : files) {
                try {
                    GameInfo info = (GameInfo) new ObjectInputStream(new FileInputStream(f)).readObject();
                    Class<?> gameType = Class.forName("usa.cactuspuppy.uhc_automation.game.types" + info.getGameType());
                    if (gameType.isEnum()) continue;
                    GameInstance gameInstance = gameType.asSubclass(GameInstance.class).getConstructor(GameInfo.class).newInstance(info);
                    GameManager.registerGame(gameInstance);

                } catch (IOException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    Logger.logWarning(this.getClass(), "Problem restoring game information from file " + f.getName() + ", deleting it...", e);
                    if (!f.delete()) Logger.logWarning(this.getClass(), "Unable to remove " + f.getName());
                }
            }
        }
        return true;
    }
}
