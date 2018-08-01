package usa.cactuspuppy.uhc_automation;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import usa.cactuspuppy.uhc_automation.Commands.CommandHelp;
import usa.cactuspuppy.uhc_automation.Commands.CommandInfo;
import usa.cactuspuppy.uhc_automation.Commands.CommandOptions;
import usa.cactuspuppy.uhc_automation.Commands.CommandPrep;
import usa.cactuspuppy.uhc_automation.Commands.CommandRegister;
import usa.cactuspuppy.uhc_automation.Commands.CommandReset;
import usa.cactuspuppy.uhc_automation.Commands.CommandRules;
import usa.cactuspuppy.uhc_automation.Commands.CommandSetWorld;
import usa.cactuspuppy.uhc_automation.Commands.CommandStart;
import usa.cactuspuppy.uhc_automation.Commands.CommandStatus;
import usa.cactuspuppy.uhc_automation.Commands.CommandUnregister;
import usa.cactuspuppy.uhc_automation.Listeners.GameModeChangeListener;
import usa.cactuspuppy.uhc_automation.Tasks.RestartTasks;
import usa.cactuspuppy.uhc_automation.Tasks.SQLRepeating;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;

public class Main extends JavaPlugin {
    private static Main instance;
    public GameInstance gi;
    public SQLAPI sqlHandler;
    private ConnectionInfo connectionInfo;
    public GameModeChangeListener gmcl;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;
        createConfig();
        createRules();
        createConnectionInfo();
        new SQLAPI();
        sqlHandler = SQLAPI.getInstance();
        sqlHandler.createUHCTimeTable();
        InfoModeCache.getInstance().addAllToCache(sqlHandler.getPlayerPrefs());
        new BukkitRunnable() {
            @Override
            public void run() {
                SQLRepeating.start();
            }
        }.runTaskLater(getInstance(), 1L);
        if (gi == null) {
            gi = new GameInstance(this);
        }
        registerCommands();
        (new RestartTasks(this)).schedule();
        getLogger().info("UHC Automation loaded in " + ((System.currentTimeMillis() - start)) + " ms");
    }

    @Override
    public void onDisable() {
        if (gi.isActive()) {
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
                boolean created = getDataFolder().mkdirs();
                if (!created) {
                    getLogger().log(Level.SEVERE, "Could not create config folder!");
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
        connectionInfo = new ConnectionInfo(getConfig().getString("db.host"), getConfig().getString("db.database"), getConfig().getString("db.username"), getConfig().getString("db.password"), getConfig().getInt("db.port"));
    }

    public Optional<Connection> getConnection() {
        synchronized (this) {
            try {
                if (connectionInfo == null) {
                    return Optional.empty();
                }
                String connectionURL = "jdbc:mysql://" + connectionInfo.getHost() + ":" + connectionInfo.getPort() + "/" + connectionInfo.getDatabase();
                return Optional.ofNullable(DriverManager.getConnection(connectionURL, connectionInfo.getUsername(), connectionInfo.getPassword()));
            } catch (SQLException e) {
                getLogger().warning("Unable to obtain database connection! Double check your config.yml is correct.");
                e.printStackTrace();
            }
            return Optional.empty();
        }
    }

    private void registerCommands() {
        getCommand("uhcstart").setExecutor(new CommandStart(this));
        getCommand("uhcoptions").setExecutor(new CommandOptions(this));
        getCommand("uhcreset").setExecutor(new CommandReset(this));
        getCommand("uhcsetworld").setExecutor(new CommandSetWorld(this));
        getCommand("uhcstatus").setExecutor(new CommandStatus(this));
        getCommand("uhcprep").setExecutor(new CommandPrep(this));
        getCommand("uhcinfo").setExecutor(new CommandInfo(this));
        getCommand("uhcreg").setExecutor(new CommandRegister(this));
        getCommand("uhcunreg").setExecutor(new CommandUnregister(this));
        getCommand("uhcrules").setExecutor(new CommandRules(this));
        getCommand("uhchelp").setExecutor(new CommandHelp());
    }

    public static Main getInstance() {
        return instance;
    }
}
