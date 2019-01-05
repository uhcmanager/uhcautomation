package usa.cactuspuppy.uhc_automation.entity;

import lombok.Getter;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

public abstract class Entity {
    @Getter private GameInstance gameInstance;

    public Entity(GameInstance gameInstance) {
        this.gameInstance = gameInstance;
    }
}
