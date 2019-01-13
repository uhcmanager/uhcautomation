package usa.cactuspuppy.uhc_automation;

import lombok.Getter;

import java.util.regex.Pattern;

public class Constants {
    @Getter private static String lastDisableFile = "lastDisable.dat";
    @Getter private static String gamesDir = "/games";
    @Getter private static String gameInfoFile = "Game%dInfo.dat";
    @Getter private static Pattern gameInfoFilePattern = Pattern.compile("^Game(\\d+)Info\\.dat");
    @Getter private static String denyPermission = "You do not have permission to do that! Contact an administrator if you believe this is in error.";
}
