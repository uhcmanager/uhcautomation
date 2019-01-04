package usa.cactuspuppy.uhc_automation.event;

import usa.cactuspuppy.uhc_automation.event.events.game.GameStartEvent;
import usa.cactuspuppy.uhc_automation.event.events.player.PlayerDeathEvent;
import usa.cactuspuppy.uhc_automation.event.events.player.PlayerJoinEvent;
import usa.cactuspuppy.uhc_automation.event.events.player.PlayerLeaveEvent;

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

    public void onPlayerLeave(PlayerLeaveEvent e) {}

    public void onPlayerDeath(PlayerDeathEvent e) {}

    public void onPlayerGroup() {}
}
