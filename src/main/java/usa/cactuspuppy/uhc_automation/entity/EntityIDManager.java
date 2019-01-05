package usa.cactuspuppy.uhc_automation.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.util.*;

@NoArgsConstructor
public class EntityIDManager {
    private static final int MAX_GEN_ATTEMPTS = 10000;
    private Random rng = new Random();
    @Getter private Map<Long, UniqueEntity> uniqueEntityMap = new HashMap<>();

    public EntityIDManager(Random random) {
        rng = random;
    }

    public long getNewId() {
        for (int i = 0; i < MAX_GEN_ATTEMPTS; i++) {

        }
        Logger.logWarning(this.getClass(), "Could not generate new unique ID");
        return -1;
    }
}
