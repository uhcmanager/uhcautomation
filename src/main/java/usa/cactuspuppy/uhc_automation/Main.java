package usa.cactuspuppy.uhc_automation;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.permission.ChildPermission;
import org.bukkit.plugin.java.annotation.permission.Permission;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.LogPrefix;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import usa.cactuspuppy.uhc_automation.command.CmdDelegator;
import usa.cactuspuppy.uhc_automation.game.tasks.MainListener;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.GameManager;
import usa.cactuspuppy.uhc_automation.game.GameState;
import usa.cactuspuppy.uhc_automation.game.GameStateEvent;
import usa.cactuspuppy.uhc_automation.utils.FileIO;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.io.*;
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
            return;
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
        //Save disable time
        Long disableTime = System.currentTimeMillis();
        FileIO.saveToFile(getDataFolder().getPath(), Constants.getLastDisableFile(), new ByteArrayInputStream(String.valueOf(disableTime).getBytes()), false);
        //Save games
        saveGames();
    }

    private void saveGames() {
        if (GameManager.getActiveGames().isEmpty()) return;
        Logger.logInfo(this.getClass(), "Detected active game(s), saving...");
        for (long l : GameManager.getActiveGames().keySet()) {
            try {
                GameInstance current = GameManager.getGame(l);
                boolean success = current.updateState(GameStateEvent.PAUSE); //Inform game of pause
                if (!success) {
                    current.getUtils().log(Logger.Level.INFO, this.getClass(), "Could not pause game, game will be reset on restart.");
                }
                FileOutputStream fileOS = new FileOutputStream(new File(getDataFolder() + Constants.getGamesDir(), String.format(Constants.getGameInfoFile(), l)));
                ObjectOutputStream out = new ObjectOutputStream(fileOS);
                out.writeObject(current);
                out.close();
                fileOS.close();
                Logger.logInfo(this.getClass(), "Game " + l + " saved");
            } catch (IOException e) {
                Logger.logWarning(this.getClass(), "Problem saving game (ID: " + l + ")");
            }
        }
    }

    @Override
    public void saveDefaultConfig() {
        //Do nothing, this breaks custom config setup
    }

    private boolean initBase() {
        //TODO: Initiate base plugin
        //Register command
        PluginCommand uhc = getCommand("uhc");
        if (uhc == null) {
            Logger.logError(this.getClass(), "Could not find /uhc command, operation impossible");
            return false;
        }
        uhc.setExecutor(new CmdDelegator());
        uhc.setTabCompleter(new CmdDelegator());
        //Set logger
        Logger.setOutput(getLogger());

        //Re-initiate all saved games
        reinitGames();

        //Register listeners
        getServer().getPluginManager().registerEvents(new MainListener(), this);
        return true;
    }

    private void reinitGames() {
        File gamesDir = new File(getDataFolder() + Constants.getGamesDir());

        if (!gamesDir.isDirectory() && !gamesDir.mkdirs()) {
            Logger.logWarning(this.getClass(), "Could not find or create game info saving directory, game loading/saving impossible!");
        } else  {
            File[] files = gamesDir.listFiles();
            if (files == null) return;
            for (File f : files) {
                try {
                    GameInstance instance = (GameInstance) new ObjectInputStream(new FileInputStream(f)).readObject();
                    GameManager.registerGame(instance);
                    //Resume game
                    GameState state = instance.getGameState();
                    if (state == GameState.LOBBY || state == GameState.ENDED) {
                        continue;
                    }
                    if (state != GameState.PAUSED) {
                        Logger.logWarning(this.getClass(), "GID: " + instance.getGameID() + " (" + f.getPath() + ") not paused before restart, resetting...");
                        instance.updateState(GameStateEvent.RESET);
                    } else {
                        instance.updateState(GameStateEvent.RESUME);
                    }
                    if (!f.delete()) Logger.logWarning(this.getClass(), "Unable to remove " + f.getName());
                } catch (IOException | ClassNotFoundException e) {
                    Logger.logWarning(this.getClass(), "Problem restoring game information from file " + f.getName() + ", deleting it...", e);
                    if (!f.delete()) Logger.logWarning(this.getClass(), "Unable to remove " + f.getName());
                }
            }
        }
    }
}
