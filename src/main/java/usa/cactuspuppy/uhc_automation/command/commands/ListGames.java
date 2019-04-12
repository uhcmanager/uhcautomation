package usa.cactuspuppy.uhc_automation.command.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

public class ListGames implements UHCCommand {
    @Override
    public String getUsage() {
        return "/uhc listgames";
    }

    @Override
    public String getPurpose() {
        return "Lists all registered game instances";
    }

    @Override
    public String getMoreInfo() {
        return "Displays information on all the currently active games.";
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String alias, String[] args) {
        //TODO
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender, String alias, String[] args) {
        return commandSender.hasPermission("uhc.manager");
    }

    @Override
    public java.util.@Nullable List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
