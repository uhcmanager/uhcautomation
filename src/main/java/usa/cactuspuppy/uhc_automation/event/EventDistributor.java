package usa.cactuspuppy.uhc_automation.event;

import usa.cactuspuppy.uhc_automation.event.events.game.GameStartEvent;
import usa.cactuspuppy.uhc_automation.event.events.player.PlayerDeathEvent;
import usa.cactuspuppy.uhc_automation.event.events.player.PlayerJoinEvent;
import usa.cactuspuppy.uhc_automation.event.events.player.PlayerLeaveEvent;

import java.util.ArrayList;
import java.util.List;

public class EventDistributor {

    private static List<EventListener> listeners = new ArrayList<>();

    public static void addListener(EventListener e) {
        listeners.add(e);
    }

    public static void start(GameStartEvent e) {
        for (EventListener l : listeners) {
            l.onGameStart(e);
        }
    }

    public static void playerJoin(PlayerJoinEvent e) {
        for (EventListener l : listeners) {
            l.onPlayerJoin(e);
        }
    }

    public static void playerLeave(PlayerLeaveEvent e) {
        for (EventListener l : listeners) {
            l.onPlayerLeave(e);
        }
    }

    public static void playerDeath(PlayerDeathEvent e) {
        for (EventListener l : listeners) {
            l.onPlayerDeath(e);
        }
    }
}
