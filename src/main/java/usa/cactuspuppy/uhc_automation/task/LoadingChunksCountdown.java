package usa.cactuspuppy.uhc_automation.task;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.Main;

import java.util.UUID;

public class LoadingChunksCountdown implements Runnable {
    private int length;
    private int iter;
    private long lastTick;

    public Integer assignedID;

    public LoadingChunksCountdown(Main main, int l) {
        iter = l;
        length = l;
        lastTick = System.currentTimeMillis();
    }

    @Override
    public void run() {
        long currentTick = System.currentTimeMillis();
        if (Math.abs(currentTick - lastTick - 1000) > 200) {
            Bukkit.getLogger().info("Current tick: " + currentTick + " | Last tick: " + lastTick + " | Tick difference: " + (currentTick - lastTick) + "ms");
            lastTick = currentTick;
            return;
        }
        if (iter == 0) {
            Main.getInstance().getGameInstance().release();
            if (assignedID != null) { Bukkit.getScheduler().cancelTask(assignedID); }
            return;
        }

        for (UUID u : Main.getInstance().getGameInstance().getActivePlayers()) {
            Player p = Bukkit.getPlayer(u);
            p.sendTitle(ChatColor.WHITE + "" + iter, ChatColor.RED + "Releasing players in...", 0, 40, 20);
            if (iter > 3) {
                p.playSound(p.getLocation(), "minecraft:block.stone_button.click_on", (float) 1, (float) 1);
            } else {
                p.playSound(p.getLocation(), "minecraft:block.note_block.pling", (float) 1, (float) 0.59);
            }
        }
        iter--;
        lastTick = currentTick;
    }

    public int schedule() {
        this.assignedID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), this, 0L, 20L);
        return assignedID;
    }
}
