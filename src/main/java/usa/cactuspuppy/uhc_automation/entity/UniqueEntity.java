package usa.cactuspuppy.uhc_automation.entity;

import lombok.Getter;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

public class UniqueEntity extends Entity {
    @Getter private long id;

    public UniqueEntity(GameInstance gameInstance) {
        super(gameInstance);
    }
}
