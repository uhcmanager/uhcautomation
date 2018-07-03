package usa.cactuspuppy.uhc_automation;

public enum TimeDisplayMode {
    CHAT,
    SUBTITLE,
    SCOREBOARD;

    public static TimeDisplayMode fromString(String s) {
        if (s.equalsIgnoreCase("CHAT")) {
            return CHAT;
        } else if (s.equalsIgnoreCase("SUBTITLE")) {
            return SUBTITLE;
        } else if (s.equalsIgnoreCase("SCOREBOARD")) {
            return SCOREBOARD;
        } else {
            return null;
        }
    }
}
