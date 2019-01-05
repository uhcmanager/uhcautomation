package usa.cactuspuppy.uhc_automation.entity;

import lombok.Getter;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

public abstract class UniqueEntity extends Entity {
    @Getter protected long id;

    public UniqueEntity(GameInstance gameInstance) {
        super(gameInstance);
        id = EntityIDManager.getNewID();
        EntityIDManager.trackEntity(id, this);
    }

    public static void removeEntity(UniqueEntity e) {
        EntityIDManager.untrackEntity(e.getId());
    }
}
