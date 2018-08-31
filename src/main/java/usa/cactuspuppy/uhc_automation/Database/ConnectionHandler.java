package usa.cactuspuppy.uhc_automation.Database;

import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.util.Optional;

public class ConnectionHandler {
    @Getter private static ConnectionHandler instance;

    @Getter @Setter private ConnectionInfo connInfo;

    public ConnectionHandler(ConnectionInfo connInfo) {
        instance = this;
        this.connInfo = connInfo;
    }

    public Optional<Connection> getConnection() {
        return Optional.empty();
    }
}
