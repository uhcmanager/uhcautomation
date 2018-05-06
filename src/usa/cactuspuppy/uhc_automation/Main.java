package usa.cactuspuppy.uhc_automation;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;

public class Main extends JavaPlugin {
    protected GameInstance gi;
    private Connection connection;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        try {
            initSQL();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        gi = new GameInstance(this);
        this.getCommand("uhcstart").setExecutor(new CommandStart(this));
        Bukkit.getServer().getPluginManager().registerEvents(new WorldChangeListener(), this);
    }

    @Override
    public void onDisable() {
        saveDefaultConfig();
    }

    private void initSQL() throws SQLException, ClassNotFoundException {
        String host = getConfig().getString("db.host");
        int port = getConfig().getInt("db.port");
        String database = getConfig().getString("db.database");
        String username = getConfig().getString("db.username");
        String password = getConfig().getString("db.password");
        if (connection != null && !connection.isClosed()) {
            return;
        }
    }
}
