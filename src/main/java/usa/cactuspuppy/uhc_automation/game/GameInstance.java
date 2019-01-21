package usa.cactuspuppy.uhc_automation.game;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public abstract class GameInstance implements Serializable {
    /**
     * Unique name by which a game is identified.<br>Must match regex (-_A-Za-z0-9){1-255}
     */
    @Getter @Setter(AccessLevel.PACKAGE) private String name;
    /**
     * Display-friendly name for this game. May contain spaces and any other characters.
     */
    @Getter @Setter private String displayName;
}
