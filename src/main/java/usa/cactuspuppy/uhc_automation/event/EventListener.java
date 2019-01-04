package usa.cactuspuppy.uhc_automation.event;

import usa.cactuspuppy.uhc_automation.event.events.GameStartEvent;
import usa.cactuspuppy.uhc_automation.event.events.PlayerJoinEvent;

public abstract class EventListener {
    public EventListener() {
        EventDistributor.addListener(this);
    }

    /**
     * Called when any game starts
     * @param e GameStartEvent
     */
    public void onGameStart(GameStartEvent e) {}

    public void onPlayerJoin(PlayerJoinEvent e) {}

    public void onPlayerLeave() {}

    public void onPlayerDeath() {}

    public void onGroup() {}
}
