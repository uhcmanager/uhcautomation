package usa.cactuspuppy.uhc_automation.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public abstract class UHCCommand implements TabCompleter {
    public abstract String getUsage();

    public abstract String getPurpose();

    public abstract boolean onCommand(CommandSender commandSender, String alias, String[] args);

    public abstract boolean hasPermission(CommandSender commandSender, String alias, String[] args);
}
