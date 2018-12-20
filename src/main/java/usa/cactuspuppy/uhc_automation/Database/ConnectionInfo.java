package usa.cactuspuppy.uhc_automation.Database;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ConnectionInfo {
    private String host, database, username, password;
    private int port;
    private String method, file;
}
