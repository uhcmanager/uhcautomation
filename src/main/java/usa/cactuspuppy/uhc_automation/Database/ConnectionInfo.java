package usa.cactuspuppy.uhc_automation.Database;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public @Data class ConnectionInfo {
    private String host, database, username, password;
    private int port;
    private String method, file;
}
