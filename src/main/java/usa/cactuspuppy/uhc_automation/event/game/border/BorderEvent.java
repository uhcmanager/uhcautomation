package usa.cactuspuppy.uhc_automation.event.game.border;

import lombok.Getter;
import usa.cactuspuppy.uhc_automation.event.game.GameEvent;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

public abstract class BorderEvent extends GameEvent {
    @Getter private BorderStatus borderStatus;

    public BorderEvent(GameInstance gameInstance, BorderStatus status) {
        super(gameInstance);
        borderStatus = status;
    }

    public enum BorderStatus {
        STATIC,
        SHRINKING,
        GROWING
    }
}
