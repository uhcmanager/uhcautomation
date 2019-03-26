package usa.cactuspuppy.uhc_automation.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import usa.cactuspuppy.uhc_automation.utils.Logger;

public class Debug extends UHCCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, String alias, String[] args) {
        boolean debug = Logger.isDebug();
        if (debug) {
            Logger.setDebug(false);
            Logger.setLevel(Logger.Level.INFO);
            commandSender.sendMessage(ChatColor.GREEN + "Debug mode is now " + ChatColor.RESET + "OFF");
        } else {
            Logger.setDebug(true);
            Logger.setLevel(Logger.Level.FINEST);
            commandSender.sendMessage(ChatColor.GREEN + "Debug mode is now " + ChatColor.RESET + "ON");
        }
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender, String alias, String[] args) {
        return commandSender.hasPermission("uhc.manager");
    }
}
