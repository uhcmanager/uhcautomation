package usa.cactuspuppy.uhc_automation.database;


import java.util.List;
import java.util.UUID;

public class SQLRepeating {
    public SQLRepeating() {}

    private static boolean paused;
    private static boolean shutdown = false;

    private static Thread thread;
    private static Runnable task = new Runnable() {
        @Override
        public void run() {
            try {
                for (paused = false; !shutdown; Thread.sleep(10L)) {
                    if (paused || SQLAPI.getInstance().queueEmpty()) {
                        continue;
                    }
                    SQLAPI.getInstance().executePlayerUpdate();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public static void start() {
        thread = new Thread(task);
        thread.start();
    }

    public static void pause() {
        paused = true;
    }

    public static void unpause() {
        paused = false;
    }

    public static void shutdown() {
        shutdown = true;
        if (!SQLAPI.getInstance().queueEmpty()) {
            System.out.println("[UHC_Automation] Finishing up SQL storage, please wait...");
            List<UUID> queue = SQLAPI.getInstance().getQueue();
            SQLAPI.getInstance().storeQueue(queue);
            System.out.println("[UHC_Automation] Success! Resuming server shutdown...");
        }
    }

    public static Thread getThread() {
        return thread;
    }
}
