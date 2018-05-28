package usa.cactuspuppy.uhc_automation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
        OfflinePlayer p = Bukkit.getPlayer(args[0]);
        if (p == null) {
            commandSender.sendMessage(ChatColor.RED + "Could not find player '" + args[0] + "'");
            return true;
        }
        m.gi.unRegisterPlayer(p);
        commandSender.sendMessage(ChatColor.GREEN + "Successfully registered " + p.getName() + " in " + m.gi.getWorld().getName() + "'s UHC");
        return true;
    }
}
