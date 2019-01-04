package usa.cactuspuppy.uhc_automation.event.game.border;

import lombok.Getter;
import usa.cactuspuppy.uhc_automation.GameInstance;
import usa.cactuspuppy.uhc_automation.event.game.GameEvent;

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
