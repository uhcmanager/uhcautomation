package usa.cactuspuppy.uhc_automation.utils;

import lombok.Getter;
import lombok.Setter;

/**
 * Provides feedback to server terminal. Logs at levels SEVERE, WARNING, INFO, FINE, and FINER<br>
 * No other class should report directly to terminal; instead, report messages to this class first to ensure template consistency.
 */
public final class Logger {
    @Setter private static boolean printStackTraces = false;
    @Setter private static Level level = Level.INFO;
    @Getter @Setter private static boolean debug = false;
    @Setter private static java.util.logging.Logger output = java.util.logging.Logger.getLogger("UHC");


    public enum Level {
        SEVERE (5),
        WARNING (4),
        INFO (3),
        FINE (2),
        FINER (1),
        FINEST (0);

        private int tier;
        Level (int t) {
            tier = t;
        }

        static boolean insufficientLevel(Level check, Level required) {
            return check.tier < required.tier;
        }
    }

    /**
     * Relay message to appropriate output logger
     * @param msg message to relay
     * @param lvl to relay at
     */
    private static void relayMessage(String msg, Level lvl) {
        output.log(java.util.logging.Level.parse(lvl.name()), msg);
    }

    public static void log(Logger.Level level, Class c, String msg, Exception e) {
        if (level.tier < 3) logFineMsg(c, msg, level.tier);
        else if (level == Level.INFO) logInfo(c, msg);
        else if (level == Level.WARNING) logWarning(c, msg, e);
        else if (level == Level.SEVERE) logError(c, msg, e);
    }

    /**
     * Logs a message at level SEVERE.
     * @param c Class reporting error
     * @param reason Reason for error. If exception thrown, do not repeat info in exception
     * @param e Exception if one was thrown, else {@code null}
     */
    public static void logError(Class c, String reason, Exception e) {
        if (Level.insufficientLevel(Level.SEVERE, level)) return;
        String message = (debug ? c.getName() : c.getSimpleName()) + " error: " + reason;
        if (e != null) {
            message = message + ". Exception: " + e.getMessage();
        }
        relayMessage(message, Level.SEVERE);
        if (printStackTraces && e != null) e.printStackTrace();
    }

    /**
     * Logs a message at level SEVERE.
     * @param c Class reporting error
     * @param reason Reason for error.
     */
    public static void logError(Class c, String reason) {
        logError(c, reason, null);
    }

    /**
     * Logs a message at level WARNING.
     * @param c Class reporting warning
     * @param reason Reason for warning. If exception thrown, do not repeat info in exception
     * @param e Exception, if thrown, else {@code null}
     */
    public static void logWarning(Class c, String reason, Exception e) {
        if (Level.insufficientLevel(Level.WARNING, level)) return;
        String message = (debug ? c.getName() : c.getSimpleName()) + " warning: " + reason;
        if (e != null) {
            message = message + ". Exception: " + e.getMessage();
        }
        relayMessage(message, Level.WARNING);
        if (printStackTraces && e != null) e.printStackTrace();
    }
    /**
     * Logs a message at level WARNING.
     * @param c Class reporting warning
     * @param reason Reason for warning.
     */
    public static void logWarning(Class c, String reason) {
        logWarning(c, reason, null);
    }

    /**
     * Logs a message at level INFO.
     * @param c Class reporting info
     * @param reason Reason/message
     */
    public static void logInfo(Class c, String reason) {
        if (Level.insufficientLevel(Level.INFO, level)) return;
        String message;
        if (debug) {
            message = c.getName() + ": " + reason;
        } else {
            message = reason;
        }
        relayMessage(message, Level.INFO);
    }

    /**
     * Logs a message from FINE to FINEST.
     * @param c class reporting fine info
     * @param info Info being reported
     * @param fineLevel Level of fineness
     *                  0 - FINE
     *                  1 - FINER
     *                  2 - FINEST
     *                  DEFAULT - FINEST
     */
    public static void logFineMsg(Class c, String info, int fineLevel) {
        Level level;
        if (fineLevel == 0) {
            level = Level.FINE;
        } else if (fineLevel == 1) {
            level = Level.FINER;
        } else {
            level = Level.FINEST;
        }
        if (Level.insufficientLevel(level, Logger.level)) return;
        String message;
        if (debug) {
            message = "<" + level.name() + " | " + c.getCanonicalName() + "> " + info;
        } else {
            message = info;
        }
        relayMessage(message, level);
    }
}
