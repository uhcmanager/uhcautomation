package usa.cactuspuppy.uhc_automation.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import usa.cactuspuppy.uhc_automation.game.info.GameInfo;

@AllArgsConstructor
public abstract class GameInstance {
    @Getter private GameInfo gameInfo;
}
