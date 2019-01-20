package usa.cactuspuppy.uhc_automation.event;

import usa.cactuspuppy.uhc_automation.event.game.GameEvent;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventDistributor {

    private static List<EventListener> listeners = new ArrayList<>();

    public static void addListener(EventListener e) {
        if (!listeners.contains(e)) {
            listeners.add(e);
        }
    }

    public static void removeListener(EventListener e) {
        listeners.remove(e);
    }

    public static void removeAllListeners() {
        listeners.clear();
    }

    public static void distributeEvent(GameEvent e) {
        Class c = e.getClass();
        Matcher m = Pattern.compile("^(\\w+)Event").matcher(c.getSimpleName());
        if (m.find()) {
            String methodName = "on" + m.group(1);
            try {
                Method method = EventListener.class.getMethod(methodName, c);
                for (EventListener l : listeners) {
                    try {
                        method.invoke(l, e);
                    } catch (IllegalAccessException | InvocationTargetException e1) {
                        Logger.logWarning(EventDistributor.class, "Could not pass " + e.getClass().getSimpleName() + " to " + l.getClass().getName(), e1);
                    }
                }
            } catch (NoSuchMethodException e1) {
                Logger.logWarning(EventDistributor.class, "Could not find corresponding EventListener method: " + methodName);
            }
        } else {
            Logger.logWarning(EventDistributor.class, "Could not distribute GameEvent " + e.toString());
        }
    }
}
