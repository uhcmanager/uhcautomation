package usa.cactuspuppy.uhc_automation.command.commands;

import org.bukkit.command.CommandSender;

public abstract class UHCCommand {
    public String getUsage() {
        return "/uhc <subcommand> [args]";
    }

    public abstract boolean onCommand(CommandSender commandSender, String alias, String[] args);

    public abstract boolean hasPermission(CommandSender commandSender, String alias, String[] args);
}
