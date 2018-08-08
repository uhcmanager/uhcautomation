package usa.cactuspuppy.uhc_automation.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TabCompleteHelper implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length < 2) {
            return null;
        }
        String subcommand = args[0];
        if (!CommandHandler.validSubcommand(subcommand)) { return new ArrayList<>(); }
        return null;
    }
}
