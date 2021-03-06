package usa.cactuspuppy.uhc_automation.command.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Option implements UHCCommand {
    @Override
    public String getUsage() {
        return "/uhc option <option> [value] [name/ID]";
    }

    @Override
    public String getPurpose() {
        return "View and edit game options";
    }

    @Override
    public String getMoreInfo() {
        return "Displays or modifies the specified game option for the specified game, or the game whose lobby world the executor is in.";
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String alias, String[] args) {
        //TODO: Implement option changing
        return false;
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
