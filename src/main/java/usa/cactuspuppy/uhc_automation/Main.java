package usa.cactuspuppy.uhc_automation;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.permission.ChildPermission;
import org.bukkit.plugin.java.annotation.permission.Permission;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.LogPrefix;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import org.bukkit.scoreboard.Scoreboard;
import usa.cactuspuppy.uhc_automation.Commands.CommandHandler;
import usa.cactuspuppy.uhc_automation.Commands.TabCompleteHelper;
import usa.cactuspuppy.uhc_automation.Database.ConnectionHandler;
import usa.cactuspuppy.uhc_automation.Database.ConnectionInfo;
import usa.cactuspuppy.uhc_automation.Database.SQLAPI;
import usa.cactuspuppy.uhc_automation.Database.SQLRepeating;
import usa.cactuspuppy.uhc_automation.Tasks.DelayedPrep;
import usa.cactuspuppy.uhc_automation.Tasks.RestartTasks;

import java.io.*;
import java.net.ConnectException;
import java.util.Optional;
import java.util.logging.Level;

@Plugin(name = "UHC_Automation", version = "1.8")
@Description("Automates the process of running a UHC")
@Author("CactusPuppy")
@LogPrefix("UHC")
@Permission(name = "uhc.admin", desc = "Allows operator access to the UHC plugin", defaultValue = PermissionDefault.OP)
@Permission(name = "uhc.*", desc = "Wildcard permission", defaultValue = PermissionDefault.OP, children = {@ChildPermission(name = "uhc.admin")})
@ApiVersion(ApiVersion.Target.v1_13)
public class Main extends JavaPlugin {
    private static Main instance;
    @Getter private GameInstance gameInstance;
    @Getter private ConnectionInfo connectionInfo;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;
        createConfig();
        createRules();
        initDatabase();
        if (gameInstance == null) {
            gameInstance = new GameInstance(this);
        }
        registerCommands();
        (new RestartTasks()).schedule();
        (new DelayedPrep()).schedule();
        long timeElapsed = System.currentTimeMillis() - start;
        getLogger().info("UHC Automation loaded in " + timeElapsed + " ms");
    }

    @Override
    public void onDisable() {
        if (gameInstance.isActive()) {
            System.out.println("[UHC_Automation] Game is active, saving game data...");
            UHCUtils.saveWorldPlayers(this);
        }
        SQLRepeating.shutdown();
    }

    /**
     * @source Innectic's Permissify plugin. https://github.com/ifydev/Permissify
     */
    private void createConfig() {
        try {
            if (!getDataFolder().exists()) {
                getLogger().info("Data folder not found, creating...");
                boolean created = getDataFolder().mkdirs();
                if (!created) {
                    getLogger().log(Level.SEVERE, "Could not create data folder!");
                }
            }
            File config = new File(getDataFolder(), "config.yml");
            if (!config.exists()) {
                getLogger().info("config.yml not found, creating...");
                saveDefaultConfig();
            } else {
                getLogger().info("Loading config.yml...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void createRules() {
        try {
            if (!getDataFolder().exists()) {
                getLogger().info("Data folder not found, creating...");
                boolean created = getDataFolder().mkdirs();
                if (!created) {
                    getLogger().log(Level.SEVERE, "Could not create config folder!");
                }
            }
            File rules = new File(getDataFolder(), "rules.txt");
            if (!rules.exists()) {
                getLogger().info("rules.txt not found, creating...");

                Reader jarRulesReader = getTextResource("rules.txt");
                BufferedReader jarRulesBuffR = new BufferedReader(jarRulesReader);

                FileWriter rulesWriter = new FileWriter(rules.getPath());
                BufferedWriter rulesBuffW = new BufferedWriter(rulesWriter);

                String line;
                while ((line = jarRulesBuffR.readLine()) != null) {
                    rulesBuffW.write(line);
                    rulesBuffW.newLine();
                }

                rulesBuffW.close();
            } else {
                getLogger().info("Loading rules.txt...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createConnectionInfo() {
        connectionInfo = new ConnectionInfo(getConfig().getString("db.host"), getConfig().getString("db.database"), getConfig().getString("db.username"), getConfig().getString("db.password"), getConfig().getInt("db.port"), getConfig().getString("db.method", "sqlite"), getConfig().getString("db.file"));
    }

    private void initDatabase() {
        createConnectionInfo();
        new ConnectionHandler(connectionInfo);
        new SQLAPI();
        try {
            SQLAPI.getInstance().createUHCTimeTable();
            InfoModeCache.getInstance().addAllToCache(SQLAPI.getInstance().getPlayerPrefs());
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), SQLRepeating::start, 1L);
        } catch (ConnectException e) {
            getLogger().warning("Database integration failed, not attempting reconnect.");
            connectionInfo = null;
        }
    }

    private void registerCommands() {
        getCommand("uhc").setExecutor(new CommandHandler());
        getCommand("uhc").setTabCompleter(new TabCompleteHelper());
    }

    public static Main getInstance() {
        return instance;
    }
}
