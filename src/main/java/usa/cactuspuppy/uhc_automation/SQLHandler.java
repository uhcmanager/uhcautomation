package usa.cactuspuppy.uhc_automation;

import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

public class SQLHandler {
    private static SQLHandler sqlHandler;
    private Statement s;
    private Main m;

    public SQLHandler(Main main, Statement statement) {
        s = statement;
        m = main;
        sqlHandler = this;
    }

    public static SQLHandler getInstance() {
        return sqlHandler;
    }

    public static boolean isInstanced() {
        return (sqlHandler != null);
    }

    public void rebind(Statement statement) {
        s = statement;
    }

    public void createUHCTimeTable() {
        try {
            if (noUHCTimeTable()) {
                Bukkit.getLogger().info("uhctime_mode table does not exist, creating...");
                s.executeUpdate("CREATE TABLE uhctime_mode (UniqueID VARCHAR(36) NOT NULL, Mode TINYTEXT NOT NULL, Primary Key (UniqueID));");
            } else {
                Bukkit.getLogger().info("uhctime_mode table exists, using that...");
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("SQLHandler Error: Could not create uhctime_mode table. Error Code: " + e.getErrorCode());
            e.printStackTrace();
        }
    }

    public boolean noUHCTimeTable() {
        try {
            s.executeQuery("SELECT 1 FROM uhctime_mode LIMIT 1");
            return false;
        } catch (SQLException e) {
            return true;
        }
    }

    public void updateAllPlayerPrefs(Map<UUID, TimeDisplayMode> prefs) {
        StringJoiner values = new StringJoiner(",\n");
        for (UUID u : prefs.keySet()) {
            values.add("(" + u.toString() + ", " + prefs.get(u).name() + ")");
        }
        try {
            s.executeUpdate("INSERT INTO uhctime_mode(UniqueID, Mode) VALUES " + values.toString() + ";");
        } catch (SQLException e) {
            Bukkit.getLogger().severe("SQLHandler Error: Could not sync cache to timetable. Error Code: " + e.getErrorCode());
        }
    }

    public Map<UUID, TimeDisplayMode> getPlayerPrefs() {
        try {
            Map<UUID, TimeDisplayMode> rv = new HashMap<>();
            if (noUHCTimeTable()) {
                return rv;
            }
            ResultSet result = s.executeQuery("SELECT * FROM uhctime_mode;");
            while (result.next()) {
                rv.put(UUID.fromString(result.getString("UniqueID")), TimeDisplayMode.fromString(result.getString("Mode")));
            }
            return rv;
        } catch (SQLException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
