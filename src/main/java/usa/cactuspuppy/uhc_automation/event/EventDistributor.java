package usa.cactuspuppy.uhc_automation.event;

import usa.cactuspuppy.uhc_automation.event.events.GameStartEvent;
import usa.cactuspuppy.uhc_automation.event.events.PlayerJoinEvent;

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
}
