package usa.cactuspuppy.uhc_automation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PreGameCountdown implements Runnable {
    private Main main;
    private int length;
    private int secs;

    private Integer assignedID;

    public PreGameCountdown(Main m, int l) {
        main = m;
        length = l;
        secs = l;
    }

    @Override
    public void run() {
        //When time up, unschedule task
        if (secs <= 0) {
            main.gi.start();
            if (assignedID != null) { Bukkit.getScheduler().cancelTask(assignedID); }
            return;
        }

        //Chat message
        if (secs == length) {
            main.gi.getAllPlayers().forEach((p) -> Bukkit.getPlayer(p).sendMessage(ChatColor.RED + "ALERT: " + ChatColor.WHITE + "Game begins in 10 seconds!"));
        }

        //Title announcements
        ChatColor use;
        if (secs > 5) {
            use = ChatColor.WHITE;
        } else {
            use = ChatColor.RED;
        }
        main.gi.getAllPlayers().forEach((p) -> infoPlayer(Bukkit.getPlayer(p), use + "" + secs, ChatColor.WHITE + "until the game starts!", 0, 80, 40));

        secs--;
    }

    public void schedule() {
        this.assignedID = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, this, 0L, 20L);
    }

    private void infoPlayer(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        player.playSound(player.getLocation(), "minecraft:block.note.pling", (float) 1, (float) 1.18);
    }
}
