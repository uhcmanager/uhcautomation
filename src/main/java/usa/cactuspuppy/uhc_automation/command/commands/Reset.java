package usa.cactuspuppy.uhc_automation.command.commands;

import org.bukkit.command.CommandSender;

public class Reset extends UHCCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, String alias, String[] args) {
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender, String alias, String[] args) {
        return false;
    }
}
