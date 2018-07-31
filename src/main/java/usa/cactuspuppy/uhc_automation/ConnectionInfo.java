package usa.cactuspuppy.uhc_automation;

public class ConnectionInfo {
    private String host, database, username, password;
    private int port;

    public ConnectionInfo(String h, String d, String u, String p, int pnum) {
        host = h;
        database = d;
        username = u;
        password = p;
        port = pnum;
    }

    public String getHost() {
        return host;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }
}
