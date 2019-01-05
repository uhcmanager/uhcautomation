package usa.cactuspuppy.uhc_automation.event;

import usa.cactuspuppy.uhc_automation.event.game.GameEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventDistributor {

    private static List<EventListener> listeners = new ArrayList<>();

    public static void addListener(EventListener e) {
        listeners.add(e);
    }

    public static void distributeEvent(GameEvent e) {
        Class c = e.getClass();
        Matcher m = Pattern.compile("^(\\w+)Event").matcher(c.getName());
        if (m.find()) {

        }
    }

    public static void onEnable() {
        for (EventListener l : listeners) {
            l.onEnable();
        }
    }
}
