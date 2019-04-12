package usa.cactuspuppy.uhc_automation.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public interface UHCCommand extends TabCompleter {
    String getUsage();

    String getPurpose();

    String getMoreInfo();

    boolean onCommand(CommandSender commandSender, String alias, String[] args);

    boolean hasPermission(CommandSender commandSender, String alias, String[] args);
}
