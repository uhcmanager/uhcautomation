package usa.cactuspuppy.uhc_automation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class CommandUnregister implements CommandExecutor {
    private Main m;

    public CommandUnregister(Main main) {
        m = main;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length < 1) {
            return false;
        }
        UUID u;
        OfflinePlayer p = Bukkit.getPlayer(args[0]);
        if (p == null) {
            //TODO: Get UUID from database.
        } else {
            u = p.getUniqueId();
        }
        m.gi.blacklistPlayer(u);
        commandSender.sendMessage(ChatColor.GREEN + "Successfully unregistered " + p.getName() + " in " + m.getConfig().getString("event-name"));
        return true;
    }
}
