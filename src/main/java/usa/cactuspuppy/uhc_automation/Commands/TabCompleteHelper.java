package usa.cactuspuppy.uhc_automation.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TabCompleteHelper implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return null;
        }
        String subcommand = args[0];
        if (!CommandHandler.validSubcommand(subcommand)) { return new ArrayList<>(); }
        if (subcommand.equalsIgnoreCase("info")) {
            return CommandInfo.onTabComplete(Arrays.copyOfRange(args, 1, args.length));
        } else if (subcommand.equalsIgnoreCase("options") || Arrays.asList(CommandHandler.getOPTIONS_ALIASES()).contains(subcommand)) {
            return CommandOptions.onTabComplete(Arrays.copyOfRange(args, 1, args.length));
        } else if (subcommand.equalsIgnoreCase("setworld")) {
            return CommandSetWorld.onTabComplete(Arrays.copyOfRange(args, 1, args.length));
        } else if (subcommand.equalsIgnoreCase("register") || Arrays.asList(CommandHandler.getREGISTER_ALIASES()).contains(subcommand) || subcommand.equalsIgnoreCase("unregister") || Arrays.asList(CommandHandler.getUNREGISTER_ALIASES()).contains(subcommand)) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
