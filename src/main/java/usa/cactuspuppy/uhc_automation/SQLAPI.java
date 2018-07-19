package usa.cactuspuppy.uhc_automation;

import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

public class SQLAPI {
    private static SQLAPI sqlapi;
    private Statement s;
    private Main m;
    private LinkedList<UUID> uuidQueue;

    public SQLAPI(Main main, Statement statement) {
        s = statement;
        m = main;
        sqlapi = this;
        uuidQueue = new LinkedList<>();
    }

    public static SQLAPI getInstance() {
        return sqlapi;
    }

    public static boolean isInstanced() {
        return (sqlapi != null);
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
            Bukkit.getLogger().severe("SQLAPI Error: Could not create uhctime_mode table. Error Code: " + e.getErrorCode());
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

    public void storePlayerPref(UUID u, TimeDisplayMode tdm) {
        try {
            if (noUHCTimeTable()) {
                createUHCTimeTable();
            }
            if (tdm == null) {
                tdm = TimeDisplayMode.CHAT;
            }
            s.executeUpdate("INSERT INTO uhctime_mode VALUES (" + u.toString() + ", " + tdm.name() + ");");
        } catch (SQLException e) {
            Bukkit.getLogger().warning("SQLAPI Error: Could not update player timemode for UUID: " + u.toString() + ". Error Code: " + e.getErrorCode());
        }
    }

    public void storeQueue(List<UUID> prefs) {
        StringJoiner values = new StringJoiner(",\n");
        for (UUID u : prefs) {
            values.add("(" + u.toString() + ", " + TimeModeCache.getInstance().getPlayerPref(u) + ")");
        }
        try {
            s.executeUpdate("INSERT INTO uhctime_mode VALUES " + values.toString() + ";");
        } catch (SQLException e) {
            e.printStackTrace();
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
            Bukkit.getLogger().severe("SQLAPI Error: Could not get player timemodes from timetable. Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public boolean queueEmpty() {
        return uuidQueue.isEmpty();
    }

    public List<UUID> getQueue() {
        return uuidQueue;
    }

    public void enqueuePlayerUpdate(UUID uuid) {
        uuidQueue.addLast(uuid);
    }

    public void executePlayerUpdate() {
        UUID u = uuidQueue.pollFirst();
        if (u == null) {
            return;
        }
        storePlayerPref(u, TimeModeCache.getInstance().getPlayerPref(u));
    }
}
