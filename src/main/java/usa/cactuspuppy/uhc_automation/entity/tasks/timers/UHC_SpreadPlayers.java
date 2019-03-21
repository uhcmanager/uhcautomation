package usa.cactuspuppy.uhc_automation.entity.tasks.timers;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.types.UHC;

import java.util.Random;
import java.util.UUID;

public class UHC_SpreadPlayers extends TimerTask {
    /**
     * Store instance as UHC class to allow access to UHC-specific fields
     */
    private UHC uhcInstance;
    /**
     * Marks whether the state changed on last run
     */
    private boolean changedState = true;
    /**
     * Current phase of spreading
     */
    private Phase phase = Phase.NO_COORDS;

    private Random rng = new Random();
    private int runs = 0;

    public UHC_SpreadPlayers(UHC instance) {
        super(instance, true, 0L, 1L);
        uhcInstance = instance;
    }

    @Override
    public void cancel() {
        if (taskID == null) { return; }
        Bukkit.getScheduler().cancelTask(taskID);
    }

    @Override
    public void run() {
        int randX = rng.nextInt(2 * uhcInstance.getInitRadius() + 1) - uhcInstance.getInitRadius();
        int randY = rng.nextInt(256);
        int randZ = rng.nextInt(2 * uhcInstance.getInitRadius() + 1) - uhcInstance.getInitRadius();
        String randXS = String.format("% 8d", randX);
        String randYS = String.format(" %3d", randY);
        String randZS = String.format(" %8d", randZ);
        switch (phase) {
            case NO_COORDS:
                for (UUID u : gameInstance.getAlivePlayers()) {
                    Player p = Bukkit.getPlayer(u);
                    if (p == null) { continue; }
                    p.sendTitle();
                }
                break;
            case X_COORD:
                for
        }

        if ()

        if (changedState) { changedState = false; }
    }

    public enum Phase {
        NO_COORDS,
        X_COORD,
        XY_COORD,
        XYZ_COORD,
        TELEPORTED
    }
}
