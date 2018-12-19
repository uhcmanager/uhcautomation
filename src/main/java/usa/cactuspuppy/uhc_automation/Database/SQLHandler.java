package usa.cactuspuppy.uhc_automation.Database;

import lombok.Getter;
import lombok.Setter;
import usa.cactuspuppy.uhc_automation.Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public class SQLHandler {
    @Getter private static SQLHandler instance;

    @Getter @Setter private ConnectionInfo connInfo;
    private String baseConnectionURL;
    private boolean sqlLite = false;

    public SQLHandler(ConnectionInfo connInfo) {
        instance = this;
        this.connInfo = connInfo;
        String method = connInfo.getMethod();
        baseConnectionURL = "jdbc:" + method + ":";
        if (method.equals("sqlite")) {
            baseConnectionURL += connInfo.getFile();
            sqlLite = true;
        } else if (method.equals("mysql")) {
            baseConnectionURL += "//" + connInfo.getHost() + ":" + connInfo.getPort();
        } else {
            Main.getInstance().getLogger().warning("Unknown connection method: " + method);
            return;
        }
        Connection connection = getConnection().orElse(null);
        if (connection == null) {
            Main.getInstance().getLogger().warning("Could not get initial connection to database");
            return;
        }
        try {
            String database = connInfo.getDatabase();
            if (!sqlLite) {
                PreparedStatement statement = connection.prepareStatement("CREATE DATABASE IF NOT EXISTS " + database);
                statement.execute();
                statement.close();
                database += ".";
            } else {
                database = "";
            }
            PreparedStatement initiateTable = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + database + "uhcinfo_mode (UniqueID VARCHAR(255) NOT NULL, Mode TINYTEXT NOT NULL, Primary Key (UniqueID))");
            initiateTable.execute();
            initiateTable.close();
        } catch (SQLException e) {
            Main.getInstance().getLogger().warning("Could not initialize database");
            e.printStackTrace();
        }
    }

    public Optional<Connection> getConnection() {
        try {
            if (sqlLite) return Optional.ofNullable(DriverManager.getConnection(baseConnectionURL));
            String connectionURL = baseConnectionURL + "/" + connInfo.getDatabase();
            return Optional.ofNullable(DriverManager.getConnection(connectionURL, connInfo.getUsername(), connInfo.getPassword()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
