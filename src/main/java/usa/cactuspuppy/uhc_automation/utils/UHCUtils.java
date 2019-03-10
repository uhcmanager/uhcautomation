package usa.cactuspuppy.uhc_automation.utils;

import usa.cactuspuppy.uhc_automation.game.GameInstance;

import java.util.Set;
import java.util.UUID;

public final class UHCUtils {
    /**
     * Increases the border shrink speed of the given game instance
     * @param instance {@code GameInstance} to act on
     */
    public static void increaseBorderSpeed(GameInstance instance) {

    }

    /**
     *
     */
    public static boolean spreadplayers(GameInstance instance) {
        Set<Set<UUID>> spreadGroups = instance.getSpreadGroups();
        int locations = spreadGroups.size();
        int maxRadius = instance.getInitRadius();

        return false;
    }

    //TODO: Broadcast methods
}
