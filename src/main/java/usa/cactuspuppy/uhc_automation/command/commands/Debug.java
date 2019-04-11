package usa.cactuspuppy.uhc_automation.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.util.List;

public class Debug implements UHCCommand {
    @Override
    public String getUsage() {
        return "/uhc debug [on/off]";
    }

    @Override
    public String getPurpose() {
        return "Controls debug mode";
    }

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

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
