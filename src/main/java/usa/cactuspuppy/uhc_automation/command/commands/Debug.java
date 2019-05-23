package usa.cactuspuppy.uhc_automation.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class Debug implements UHCCommand {
    @Override
    public String getUsage() {
        return "/uhc debug [true/false]";
    }

    @Override
    public String getPurpose() {
        return "Controls debug mode";
    }

    @Override
    public String getMoreInfo() {
        return "Controls debug mode, which when active will output highly verbose information to the server logs.";
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String alias, String[] args) {
        if (args.length < 1) {
            boolean debug = !Logger.isDebug();
            Logger.setDebug(debug);
            Logger.setLevel((debug ? Logger.Level.FINEST : Logger.Level.INFO));
            commandSender.sendMessage(ChatColor.GREEN + "Debug mode is now " + ChatColor.RESET + (debug ? "ON" : "OFF"));
        } else {
            String arg = args[0];
            if (!arg.equalsIgnoreCase("true") && !arg.equalsIgnoreCase("false")) {
                return false;
            }
            boolean val = Boolean.valueOf(args[0]);
            boolean debug = Logger.isDebug();
            if (val == debug) {
                commandSender.sendMessage(ChatColor.YELLOW + "Debug mode is already " + ChatColor.RESET + (debug ? "ON" : "OFF"));
                return true;
            }
            Logger.setDebug(val);
            Logger.setLevel((val ? Logger.Level.FINEST : Logger.Level.INFO));
            commandSender.sendMessage(ChatColor.GREEN + "Debug mode is now " + ChatColor.RESET + (val ? "ON" : "OFF"));
        }
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender, String alias, String[] args) {
        return commandSender.hasPermission("uhc.manager");
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
