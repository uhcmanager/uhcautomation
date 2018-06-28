package usa.cactuspuppy.uhc_automation;

import com.mysql.jdbc.CommunicationsException;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import usa.cactuspuppy.uhc_automation.Commands.CommandOptions;
import usa.cactuspuppy.uhc_automation.Commands.CommandPrep;
import usa.cactuspuppy.uhc_automation.Commands.CommandRegister;
import usa.cactuspuppy.uhc_automation.Commands.CommandReset;
import usa.cactuspuppy.uhc_automation.Commands.CommandSetWorld;
import usa.cactuspuppy.uhc_automation.Commands.CommandStart;
import usa.cactuspuppy.uhc_automation.Commands.CommandStatus;
import usa.cactuspuppy.uhc_automation.Commands.CommandTime;
import usa.cactuspuppy.uhc_automation.Commands.CommandUnregister;
import usa.cactuspuppy.uhc_automation.Listeners.GameModeChangeListener;
import usa.cactuspuppy.uhc_automation.Tasks.DelayedReset;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class Main extends JavaPlugin {
    public GameInstance gi;
    private Connection connection;
    private String host, database, username, password;
    private int port;
    protected Statement statement;
    public GameModeChangeListener gmcl;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        createConfig();
        (new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    initSQL();
                    statement = connection.createStatement();

                } catch (SQLException | ClassNotFoundException e) {
                    getLogger().warning("Could not establish connection to SQL database. Check that your config.yml is correct.");
                }
            }
        }).runTaskAsynchronously(this);
        if (gi == null) {
            gi = new GameInstance(this);
        }
        registerCommands();
        (new DelayedReset(this)).schedule();
        getLogger().info("UHC Automation loaded in " + ((System.currentTimeMillis() - start)) + " ms");
    }

    @Override
    public void onDisable() {
        if (gi.isActive()) {
            UHCUtils.saveWorldPlayers(this, gi.getLivePlayers(), gi.getActivePlayers());
        }
        saveConfig();
    }

    /**
     * @source Innectic's Permissify plugin.
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

    private void initSQL() throws SQLException, ClassNotFoundException {
        host = getConfig().getString("db.host");
        port = getConfig().getInt("db.port");
        database = getConfig().getString("db.database");
        username = getConfig().getString("db.username");
        password = getConfig().getString("db.password");
        if (connection != null && !connection.isClosed()) {
            return;
        }
        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            try {
                connection = DriverManager.getConnection("jdbc:mysql://" + this.host+ ":" + this.port + "/" + this.database, this.username, this.password);
            } catch (CommunicationsException e) {
                getLogger().warning("Could not establish connection to SQL database. Check that your config.yml is correct.");
            }
        }
    }

    private void registerCommands() {
        getCommand("uhcstart").setExecutor(new CommandStart(this));
        getCommand("uhcoptions").setExecutor(new CommandOptions(this));
        getCommand("uhcreset").setExecutor(new CommandReset(this));
        getCommand("uhcsetworld").setExecutor(new CommandSetWorld(this));
        getCommand("uhcstatus").setExecutor(new CommandStatus(this));
        getCommand("uhcprep").setExecutor(new CommandPrep(this));
        getCommand("uhctime").setExecutor(new CommandTime(this));
        getCommand("uhcreg").setExecutor(new CommandRegister(this));
        getCommand("uhcunreg").setExecutor(new CommandUnregister(this));
    }
}
