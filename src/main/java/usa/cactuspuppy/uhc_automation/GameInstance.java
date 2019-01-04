package usa.cactuspuppy.uhc_automation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import usa.cactuspuppy.uhc_automation.info.GameInfo;

@AllArgsConstructor
public abstract class GameInstance {
    @Getter private GameInfo gameInfo;
    @Getter private long gameID;
}
