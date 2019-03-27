package usa.cactuspuppy.uhc_automation.entity.tasks.timers;

import usa.cactuspuppy.uhc_automation.game.GameInstance;

import java.sql.Time;

public class UHC_InitCountdown extends TimerTask {
    public UHC_InitCountdown(GameInstance gameInstance, int secsDelay) {
        super(gameInstance, true, 0L, 2L);
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {

    }
}
