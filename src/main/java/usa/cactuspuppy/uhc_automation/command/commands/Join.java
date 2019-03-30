package usa.cactuspuppy.uhc_automation.command.commands;

import org.bukkit.command.CommandSender;

public class Join extends UHCCommand {
    @Override
    public String getUsage() {
        return "/uhc join <player> [ID/name]";
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String alias, String[] args) {
        if (args.length < 2) {
            return false;
        }
        //TODO: Implement joining
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender, String alias, String[] args) {
        return commandSender.hasPermission("uhc.manager");
    }
}
