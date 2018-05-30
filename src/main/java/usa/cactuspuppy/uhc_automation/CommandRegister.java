package usa.cactuspuppy.uhc_automation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRegister implements CommandExecutor {
    private Main m;

    public CommandRegister(Main main) {
        m = main;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length < 1) {
            return false;
        }
        String playerName = args[0];
        OfflinePlayer p = Bukkit.getPlayer(playerName);
        if (p == null) {
            commandSender.sendMessage(ChatColor.RED + "Could not find player '" + playerName + "'");
            return true;
        }
        m.gi.registerPlayer((Player) p);
        commandSender.sendMessage(ChatColor.GREEN + "Successfully registered " + p.getName() + " in " + m.getConfig().getString("event-name"));
        return true;
    }
}
