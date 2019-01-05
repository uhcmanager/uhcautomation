package usa.cactuspuppy.uhc_automation.event.game.border;

import usa.cactuspuppy.uhc_automation.game.GameInstance;

public class BorderSpeedUpEvent extends BorderEvent {
    public BorderSpeedUpEvent(GameInstance gameInstance, BorderStatus status) {
        super(gameInstance, status);
    }
}
