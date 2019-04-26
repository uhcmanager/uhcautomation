package usa.cactuspuppy.uhc_automation.game.entity;

import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.GameManager;

import java.io.Serializable;

/**
 * Represents objects that a gameInstance can produce
 */
public abstract class Entity implements Serializable {
    protected long gameInstance;

    public Entity(GameInstance gameInstance) {
        this.gameInstance = gameInstance.getGameID();
    }

    public GameInstance getGameInstance() {
        return GameManager.getActiveGames().get(gameInstance);
    }
}
