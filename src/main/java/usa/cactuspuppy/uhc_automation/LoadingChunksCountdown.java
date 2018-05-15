package usa.cactuspuppy.uhc_automation;

import org.bukkit.Bukkit;

import java.util.UUID;

public class LoadingChunksCountdown implements Runnable {
    private Main m;
    private int length;
    private int iter;

    public Integer assignedID;

    public LoadingChunksCountdown(Main main, int l) {
        iter = l;
        length = l;
        m = main;
    }

    @Override
    public void run() {
        if (iter == 0) {
            m.gi.release();
            return;
        }

        for (UUID u : m.gi.getAllPlayers()) {

        }
        iter--;
    }

    public int schedule() {
        this.assignedID = Bukkit.getScheduler().scheduleSyncRepeatingTask(m, this, 0L, 20L);
        return assignedID;
    }
}
