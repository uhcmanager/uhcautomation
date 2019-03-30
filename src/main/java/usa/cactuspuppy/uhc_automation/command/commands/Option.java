package usa.cactuspuppy.uhc_automation.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import usa.cactuspuppy.uhc_automation.Main;

public class Option extends UHCCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, String alias, String[] args) {
        //TODO: Implement option changing
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender, String alias, String[] args) {
        return commandSender.hasPermission("uhc.manager");
    }
}
