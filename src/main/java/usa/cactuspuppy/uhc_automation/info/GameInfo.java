package usa.cactuspuppy.uhc_automation.info;

import lombok.Data;

import java.io.Serializable;

@Data
public class GameInfo implements Serializable {
    //BASIC INFO
    /** Name of the game */
    private String name;
    /** Name to display */
    private String displayName;
    /** Whether the game should only include server uptime in elapsed time */
    private boolean realTime;
    /** Long representing start time via System.currentTimeMillis */
    private long startTime;

    //WORLD INFO
    //EPISODE INFO
    //BORDER INFO
    //PLAYER INFO

}
