package usa.cactuspuppy.uhc_automation.utils;

import usa.cactuspuppy.uhc_automation.Main;

import java.util.Optional;

/**
 * Provides feedback to server terminal. Logs at levels SEVERE, WARNING, INFO, FINE, and FINER
 * No other class should report directly to terminal; instead, report messages to this class first to ensure template consistency.
 */
public class Logger {
    private static boolean printStackTraces;
    /**
     * Logs a message at level SEVERE.
     * @param c Class reporting error
     * @param reason Reason for error. If exception thrown, do not repeat info in exception
     * @param optionalE Optional containing exception, if needed.
     */
    public static void logError(Class c, String reason, Optional<Exception> optionalE) {
        String message = "[UHC] " + c.getSimpleName() + " error: " + reason;
        if (optionalE.isPresent()) {
            Exception e = optionalE.get();
            message = message + ". Exception: " + e.getMessage();
        }
        Main.getInstance().getLogger().severe(message);

    }
}
