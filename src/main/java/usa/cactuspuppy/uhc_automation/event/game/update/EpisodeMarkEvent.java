package usa.cactuspuppy.uhc_automation.event.game.update;

import lombok.Getter;
import org.bukkit.event.HandlerList;
import usa.cactuspuppy.uhc_automation.event.game.GameEvent;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.entity.EpisodeMark;

/**
 * Marks the completion of an episode
 */
public class EpisodeMarkEvent extends GameEvent {
    /**
     * Episode number of the just-completed episode
     */
    @Getter
    private int episode;
    /**
     * Seconds elapsed since start of game
     */
    @Getter
    private long secs;

    public EpisodeMarkEvent(GameInstance gameInstance, EpisodeMark mark) {
        super(gameInstance);
        episode = mark.getEpNum();
        secs = mark.getSecsElapsed();
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
