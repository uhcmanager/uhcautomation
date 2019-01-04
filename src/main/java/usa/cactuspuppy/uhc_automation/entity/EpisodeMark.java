package usa.cactuspuppy.uhc_automation.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.util.Optional;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

/**
 * Abstraction for end-of-episode episode markers
 */
@Getter
@AllArgsConstructor
public class EpisodeMark {
    /** Index of completed episode */
    private int epNum;
    /** Seconds elapsed since start of game */
    private long secsElapsed;

    public EpisodeMark(int epNum, long duration, TimeUnit unit) {
        this.epNum = epNum;
        long secs = unit.toSeconds(duration);
        if (secs == Long.MIN_VALUE || secs == Long.MAX_VALUE) {
            Logger.logWarning(this.getClass(), "Overflow while creating episode marker", Optional.empty());
        }
        secsElapsed = secs;
    }
}
