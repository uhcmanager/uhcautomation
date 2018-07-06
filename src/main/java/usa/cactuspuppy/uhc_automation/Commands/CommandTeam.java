package usa.cactuspuppy.uhc_automation.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import usa.cactuspuppy.uhc_automation.Main;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandTeam implements CommandExecutor, TabCompleter {
    private Main m;

    public static final String[] SUBCOMMANDS = {"add", "remove", "join", "leave", "option"};

    public CommandTeam(Main main) {
        m = main;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length < 1) {
            return false;
        }
        String subcommand = args[0];
        if (!Arrays.asList(SUBCOMMANDS).contains(subcommand)) {
            commandSender.sendMessage(ChatColor.RED + "Subcommand " + ChatColor.WHITE + subcommand + ChatColor.RED + " not recognized. Acceptable options: " + ChatColor.WHITE + "add, remove, join, leave, option");
        } else {
            commandSender.sendMessage(ChatColor.YELLOW + "Work in progress!");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.stream(CommandTeam.SUBCOMMANDS).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        } else if (args.length == 0) {
            return Arrays.asList(CommandTeam.SUBCOMMANDS);
        } else {
            return null;
        }
    }
}
