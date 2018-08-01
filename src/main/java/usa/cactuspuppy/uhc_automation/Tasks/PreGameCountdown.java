package usa.cactuspuppy.uhc_automation.Tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.Main;

public class PreGameCountdown implements Runnable {
    private int length;
    private int secs;
    private CommandSender sender;

    private static Integer assignedID;
    public static boolean instanced;
    public static PreGameCountdown pgc;

    public PreGameCountdown(int l, CommandSender s) {
        length = l;
        secs = l;
        instanced = true;
        sender = s;
        pgc = this;
    }

    @Override
    public void run() {
        //When time up, unschedule task
        if (secs <= 0) {
            Main.getInstance().getGameInstance().start(sender);
            if (assignedID != null) { Bukkit.getScheduler().cancelTask(assignedID); }
            instanced = false;
            pgc = null;
            assignedID = null;
            return;
        }

        //Chat message
        if (secs == length) {
            Main.getInstance().getGameInstance().getActivePlayers().forEach((p) -> Bukkit.getPlayer(p).sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "ALERT: " + ChatColor.WHITE + "Game begins in " + length + " seconds!"));
        }

        //Title announcements
        ChatColor use;
        if (secs > 5) {
            use = ChatColor.WHITE;
        } else {
            use = ChatColor.RED;
        }
        Main.getInstance().getGameInstance().getActivePlayers().forEach((p) -> infoPlayer(Bukkit.getPlayer(p), use + "" + secs, ChatColor.WHITE + "until the game starts!", 0, 80, 40));

        secs--;
    }

    public void schedule() {
        assignedID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), this, 0L, 20L);
    }

    public void instaStart() {
        secs = 0;
        run();
    }

    public int getID() {
        return assignedID;
    }

    public static PreGameCountdown getInstance() {
        return pgc;
    }

    private void infoPlayer(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        player.playSound(player.getLocation(), "minecraft:block.note.pling", (float) 1, (float) 1.18);
    }
}
