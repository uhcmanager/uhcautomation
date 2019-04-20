package usa.cactuspuppy.uhc_automation.command.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Join implements UHCCommand {
    @Override
    public String getUsage() {
        return "/uhc join <player> [ID/name]";
    }

    @Override
    public String getPurpose() {
        return "Adds a player to the specified game";
    }

    @Override
    public String getMoreInfo() {
        return "Adds a player to the specified game, or if unspecified the game whose lobby world the executor is in.";
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String alias, String[] args) {
        if (args.length < 2) {
            return false;
        }

        return true;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender, String alias, String[] args) {
        return commandSender.hasPermission("uhc.manager");
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
