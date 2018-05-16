package usa.cactuspuppy.uhc_automation;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BorderAnnouncer implements Runnable {
    private Main m;

    public BorderAnnouncer(Main main) {
        m = main;
    }

    @Override
    public void run() {
        for (UUID u : m.gi.getAllPlayers()) {
            Player p = Bukkit.getPlayer(u);
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.BOLD + "" + ChatColor.DARK_RED
                    + "[Border]" + ChatColor.RESET + " ±" + (int) m.gi.getWorld().getWorldBorder().getSize() / 2));
        }
    }

    public void schedule() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(m, this, 0L, 5L);
    }
}
