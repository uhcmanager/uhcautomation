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
        boolean hasPerm = sender.hasPermission("uhc.admin");
        String subcommand = args[0];
        if (!CommandHandler.validSubcommand(subcommand)) { return new ArrayList<>(); }
        if (subcommand.equals("info")) {
            return CommandInfo.onTabComplete(Arrays.copyOfRange(args, 1, args.length));
        } else if (subcommand.equals("options") || Arrays.asList(CommandHandler.getOPTIONS_ALIASES()).contains(subcommand)) {
            if (!hasPerm) return new ArrayList<>();
            return CommandOptions.onTabComplete(Arrays.copyOfRange(args, 1, args.length));
        } else if (subcommand.equals("prep")) {
            if (!hasPerm) return new ArrayList<>();
            return CommandPrep.onTabComplete(Arrays.copyOfRange(args, 1, args.length));
        } else if (subcommand.equals("register") || Arrays.asList(CommandHandler.getREGISTER_ALIASES()).contains(subcommand) || subcommand.equalsIgnoreCase("unregister") || Arrays.asList(CommandHandler.getUNREGISTER_ALIASES()).contains(subcommand)) {
            if (!hasPerm) return new ArrayList<>();
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        } else if (subcommand.equals("setworld")) {
            if (!hasPerm) return new ArrayList<>();
            return CommandSetWorld.onTabComplete(Arrays.copyOfRange(args, 1, args.length));
        } else if (subcommand.equals("team")) {
            if (!hasPerm) return new ArrayList<>();
            return CommandTeam.onTabComplete(Arrays.copyOfRange(args, 1, args.length));
        }
        return new ArrayList<>();
    }
}
