package usa.cactuspuppy.uhc_automation.ScoreboardUtils;

import lombok.Getter;

public class ScoreboardSaver {
    private static final int SAVE_COOLDOWN = 1000;

    private static boolean shutdown = false;
    private static boolean doSave = false;
    private static long nextAttempt;

    @Getter private static Thread thread;

    private static Runnable task = () -> {
        try {
            for (; (nextAttempt >= System.currentTimeMillis()) && !shutdown && doSave; Thread.sleep(50L)) {
                ScoreboardIO.saveScoreboardToFile();
                nextAttempt += SAVE_COOLDOWN;
                doSave = false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    };

    public static void start() {
        thread = new Thread(task);
        nextAttempt = System.currentTimeMillis();
        thread.start();
    }

    public static void shutdown() {
        shutdown = true;
        ScoreboardIO.saveScoreboardToFile();
    }

    public static void queueSave() {
        doSave = true;
    }
}
