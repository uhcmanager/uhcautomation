package usa.cactuspuppy.uhc_automation;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Main extends JavaPlugin {
    protected GameInstance gi;
    private Connection connection;
    private String host, database, username, password;
    private int port;
    protected Statement statement;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    initSQL();
                    statement = connection.createStatement();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };
        r.runTaskAsynchronously(this);
        gi = new GameInstance(this);
        this.getCommand("uhcstart").setExecutor(new CommandStart(this));
        this.getCommand("uhcoptions").setExecutor(new CommandOptions(this));
        Bukkit.getServer().getPluginManager().registerEvents(new WorldChangeListener(), this);
    }

    @Override
    public void onDisable() {
        saveDefaultConfig();
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
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host+ ":" + this.port + "/" + this.database, this.username, this.password);
        }
    }
}
