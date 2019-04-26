package usa.cactuspuppy.uhc_automation.game.entity.unique;

import lombok.Getter;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.entity.Entity;

import java.io.Serializable;

public abstract class UniqueEntity extends Entity implements Serializable {
    @Getter
    protected long id;

    public UniqueEntity(GameInstance gameInstance) {
        super(gameInstance);
        id = EntityIDManager.getNewID();
        EntityIDManager.trackEntity(id, this);
    }

    public static void removeEntity(UniqueEntity e) {
        EntityIDManager.untrackEntity(e.getId());
    }
}
