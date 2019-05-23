package usa.cactuspuppy.uhc_automation;

import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.permission.ChildPermission;
import org.bukkit.plugin.java.annotation.permission.Permission;
import org.bukkit.plugin.java.annotation.plugin.*;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import usa.cactuspuppy.uhc_automation.command.CmdDelegator;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.GameManager;
import usa.cactuspuppy.uhc_automation.game.GameState;
import usa.cactuspuppy.uhc_automation.game.GameStateEvent;
import usa.cactuspuppy.uhc_automation.game.tasks.MainListener;
import usa.cactuspuppy.uhc_automation.utils.Config;
import usa.cactuspuppy.uhc_automation.utils.FileIO;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.io.*;
import java.util.Scanner;
import java.util.UUID;

@Plugin(name = "UHCAutomation", version = "2.0")
@Website("www.crashtc.com")
@Description("Provides the ability to create multiple concurrent game instances in different worlds with individual configurations")
@Author("CactusPuppy")
@LogPrefix("UHCAuto")
@ApiVersion(ApiVersion.Target.v1_13)
@Permission(name = "uhc.*", desc = "Wildcard permission", children = {@ChildPermission(name = "uhc.manager")})
@Permission(name = "uhc.manager", desc = "Allows game management", defaultValue = PermissionDefault.OP)
public class Main extends JavaPlugin {
    @Getter
    private static Main instance;
    /**
     * Time at which the plugin was last disabled, used to restart in-progress games
     */
    @Getter
    private static long lastDisable;
    /**
     * Time at which this plugin was enabled, used to restart in-progress games
     */
    @Getter
    private static long enabled;

    @Getter
    private static Config mainConfig;

    //TODO: Implement version check
    /**
     * Current version hash (to check for outdated configs)
     */
    public static final String VERSION_HASH = "D84BDB34D4EEEF4034D77E5403F850E35BC4A51B1143E3A83510E1AAAD839748";

    @Override
    public void onEnable() {
        long start = System.nanoTime();
        instance = this;
        boolean success = initBase();
        if (!success) {
            Logger.logSevere(this.getClass(), "Failure to initiate base plugin, disabling...");
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
        for (UUID uuid : GameManager.getActiveGames().keySet()) {
            try {
                GameInstance current = GameManager.getGame(uuid);
                boolean success = current.updateState(GameStateEvent.PAUSE); //Inform game of pause
                if (!success && (current.getGameState() != GameState.LOBBY || current.getGameState() != GameState.PAUSED)) {
                    current.getUtils().log(Logger.Level.INFO, this.getClass(), "Could not pause game, game may be reset on restart.");
                }
                FileOutputStream fileOS = new FileOutputStream(new File(getDataFolder() + Constants.getGamesDir(), String.format(Constants.getGameInfoFile(), uuid.toString())));
                ObjectOutputStream out = new ObjectOutputStream(fileOS);
                out.writeObject(current);
                out.close();
                fileOS.close();
                Logger.logInfo(this.getClass(), "Game " + uuid + " saved");
            } catch (IOException e) {
                Logger.logWarning(this.getClass(), "Problem saving game (ID: " + uuid + ")", e);
            }
        }
    }

    @Override
    public void saveDefaultConfig() {
        //Do nothing, this breaks custom config setup
    }

    private boolean initBase() {
        //Get/create main config
        File dataFolder = Main.getInstance().getDataFolder();
        if (!dataFolder.isDirectory() && !dataFolder.mkdirs()) {
            Logger.logSevere(this.getClass(), "Could not find or create data folder.");
            return false;
        }
        File config = new File(Main.getInstance().getDataFolder(), "config.yml");
        //Create config if not exist
        if (!config.isFile()) {
            InputStream inputStream = getResource("config.yml");
            if (inputStream == null) {
                Logger.logSevere(this.getClass(), "No packaged config.yml?!");
                return false;
            }
            try {
                FileUtils.copyToFile(inputStream, config);
            } catch (IOException e) {
                Logger.logSevere(this.getClass(), "Error while creating new config", e);
                return false;
            }
        }
        //Set mainConfig
        mainConfig = new Config();
        //Check version hash
        if (!Main.VERSION_HASH.equals(mainConfig.get("version-hash"))) {
            Logger.logWarning(this.getClass(), "Your config is outdated, consider deleting it and letting the plugin regenerate your config file.");
            //TODO: non-destructive config update
        }
        //Register command
        PluginCommand uhc = getCommand("uhc");
        if (uhc == null) {
            Logger.logSevere(this.getClass(), "Could not find /uhc command, operation impossible");
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
        } else {
            File[] files = gamesDir.listFiles();
            if (files == null) return;
            for (File f : files) {
                FileInputStream fileStream;
                ObjectInputStream objStream;
                try {
                    fileStream = new FileInputStream(f);
                    objStream = new ObjectInputStream(fileStream);
                } catch (IOException e) {
                    Logger.logWarning(this.getClass(), "Failed to initiate reading streams for file " + f.getName());
                    return;
                }
                try {
                    GameInstance instance = (GameInstance) objStream.readObject();
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
                    try {
                        objStream.close();
                    } catch (IOException ex) {
                        Logger.logWarning(this.getClass(), "Failed to close object stream");
                        return;
                    }
                    if (!f.delete()) Logger.logWarning(this.getClass(), "Unable to remove " + f.getName());
                }
            }
        }
    }
}
