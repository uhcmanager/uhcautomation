package usa.cactuspuppy.uhc_automation.command.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ListGames extends UHCCommand {
    @Override
    public String getUsage() {
        return "/uhc list";
    }

    @Override
    public String getPurpose() {
        return "Lists all registered game instances";
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
    public java.util.@Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
