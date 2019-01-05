package usa.cactuspuppy.uhc_automation.event.game.border;

import usa.cactuspuppy.uhc_automation.game.GameInstance;

public class BorderBeginShrinkEvent extends BorderEvent {

    public BorderBeginShrinkEvent(GameInstance gameInstance) {
        super(gameInstance, BorderStatus.SHRINKING);
    }
}
