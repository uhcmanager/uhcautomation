package usa.cactuspuppy.uhc_automation;

public enum InfoDisplayMode {
    CHAT,
    SCOREBOARD;

    public static InfoDisplayMode fromString(String s) {
        if (s.equalsIgnoreCase("CHAT")) {
            return CHAT;
        } else if (s.equalsIgnoreCase("SCOREBOARD")) {
            return SCOREBOARD;
        } else {
            return null;
        }
    }
}
