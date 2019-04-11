package usa.cactuspuppy.uhc_automation.game.entity;

import lombok.Getter;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Abstraction for end-of-episode episode markers
 */
@Getter
public class EpisodeMark extends Entity {
    /** Index of completed episode */
    private int epNum;
    /** Seconds elapsed since start of game */
    private long secsElapsed;

    public EpisodeMark(GameInstance gameInstance, int epNum, long secsElapsed) {
        super(gameInstance);
        this.epNum = epNum;
        this.secsElapsed = secsElapsed;
    }

    /**
     * Alternative constructor for EpisodeMark
     * @param gameInstance GameInstance which triggered this episode mark
     * @param epNum Index of complete episode
     * @param quantities Ordered list of quantities. {@code quantities.get(i)} will be matched with {@code units.get(i)} if both exist to add to {@code secsElapsed}.
     * @param units Ordered list of {@code TimeUnit}s. See {@code quantities}.
     */
    public EpisodeMark(GameInstance gameInstance, int epNum, List<Long> quantities, List<TimeUnit> units) {
        super(gameInstance);
        this.epNum = epNum;
        secsElapsed = 0;
        for (int i = 0; i < quantities.size() && i < units.size(); i++) {
            long result = safeAdd(secsElapsed, units.get(i).toSeconds(quantities.get(i)));
            if (result == Long.MAX_VALUE) {
                Logger.logWarning(this.getClass(), "Overflow while calculating secsElapsed");
                return;
            }
            secsElapsed = result;
        }
    }

    /**
     * Add a to b, checking for overflow
     * @return the sum, or {@code Long.MAX_VALUE} if addition would overflow
     */
    long safeAdd(long a, long b) {
        if (b == Long.MAX_VALUE) return Long.MAX_VALUE;
        if (a > Long.MAX_VALUE - b) return Long.MAX_VALUE;
        return a + b;
    }
}
