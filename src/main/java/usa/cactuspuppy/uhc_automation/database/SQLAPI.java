package usa.cactuspuppy.uhc_automation.database;

import org.bukkit.Bukkit;
import usa.cactuspuppy.uhc_automation.InfoDisplayMode;
import usa.cactuspuppy.uhc_automation.InfoModeCache;
import usa.cactuspuppy.uhc_automation.Main;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SQLAPI {
    private static SQLAPI sqlapi;

    private LinkedList<UUID> uuidQueue;

    public SQLAPI() {
        sqlapi = this;
        uuidQueue = new LinkedList<>();
    }

    public static SQLAPI getInstance() {
        return sqlapi;
    }

    private void storePlayerPref(UUID u, InfoDisplayMode tdm) {
        try {
            if (tdm == null) {
                tdm = InfoDisplayMode.SCOREBOARD;
            }
            Optional<Connection> connection = SQLHandler.getInstance().getConnection();
            if (!connection.isPresent()) {
                Main.getInstance().getLogger().warning("Could not get connection to database from connection info! Please check your config.yml.");
                throw new ConnectException();
            }
            PreparedStatement statement = connection.get().prepareStatement("REPLACE INTO uhcinfo_mode VALUES ('" + u.toString() + "', '" + tdm.name() +"')");
            statement.execute();
            statement.close();
            connection.get().close();
        } catch (SQLException | ConnectException e) {
            Bukkit.getLogger().warning("SQLAPI Error: Could not update player timemode for UUID: " + u.toString());
            e.printStackTrace();
        }
    }

    public void storeQueue(List<UUID> prefs) {
        for (UUID u : prefs) {
            storePlayerPref(u, InfoModeCache.getInstance().getPlayerPref(u));
        }
    }

    public Map<UUID, InfoDisplayMode> getPlayerPrefs() {
        try {
            Map<UUID, InfoDisplayMode> rv = new HashMap<>();
            Optional<Connection> connection = SQLHandler.getInstance().getConnection();
            if (!connection.isPresent()) {
                throw new ConnectException();
            }
            ResultSet result = connection.get().prepareStatement("SELECT * FROM uhcinfo_mode;").executeQuery();
            while (result.next()) {
                rv.put(UUID.fromString(result.getString("UniqueID")), InfoDisplayMode.fromString(result.getString("Mode")));
            }
            connection.get().close();
            return rv;
        } catch (SQLException | ConnectException e) {
            Main.getInstance().getLogger().severe("SQLAPI Error: Could not get player timemodes from timetable.");
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
        storePlayerPref(u, InfoModeCache.getInstance().getPlayerPref(u));
    }
}
