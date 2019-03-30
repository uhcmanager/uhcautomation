package usa.cactuspuppy.uhc_automation.game.tasks.timers;

import usa.cactuspuppy.uhc_automation.game.types.UHC;

public class UHC_BorderTimer extends TimerTask {
    private UHC uhc;
    /**
     * Timestamp at which to begin shrinking
     */
    long beginShrink;
    /**
     * Whether the border is currently shrinking
     */
    boolean shrinking;
    /**
     * Speed of one side of the border in blocks per second
     */
    double currentShrinkSpeed;
    /**
     * Number of players remaining at shrink
     */
    int playersAtShrink;

    public static double baseSpeed = 0.3;
    public static double maxSpeed = 4.0;

    public UHC_BorderTimer(UHC uhc, long initDelay) {
        super(uhc, true, 0L, 5L);
        this.uhc = uhc;
        currentShrinkSpeed = baseSpeed;
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
        //TODO: Set timer for shrinking
    }

    public void speedUp() {
        if (!shrinking) {
            return;
        }
        int initPlayer = gameInstance.getInitNumPlayers();
        if (initPlayer < 3) {
            initPlayer = 3;
        }
        if (currentShrinkSpeed == maxSpeed) {
            return;
        }
        double addSpeed = 1D / (initPlayer - 2) * (maxSpeed - baseSpeed);
        double newSpeed = currentShrinkSpeed + addSpeed;
        if (newSpeed > maxSpeed) {
            newSpeed = maxSpeed;
        }
        game
    }
}
