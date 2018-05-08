package usa.cactuspuppy.uhc_automation;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class CommandOptions implements CommandExecutor {
    private Main main;
    private static final String[] OPTIONS = {"initSize", "finalSize", "timeToBorderShrink", "teamMode"};

    public CommandOptions(Main m) {
        main = m;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length != 2) {
            return false;
        }
        if (Arrays.asList(OPTIONS).contains(args[0])) {
            try {
                if (args[0].equals(OPTIONS[0])) {
                    main.gi.setInitSize(Integer.valueOf(args[1]));
                } else if (args[0].equals(OPTIONS[1])) {
                    if (Integer.valueOf(args[1]) > main.gi.getFinalSize()) {
                        commandSender.sendMessage(
                                ChatColor.RED + "ERROR: Requested final border size " + ChatColor.RESET + Integer.valueOf(args[1])
                                        + ChatColor.RED + " is not smaller than current initial border size "
                                        + ChatColor.RESET + main.gi.getInitSize());
                        return true;
                    }
                    main.gi.setFinalSize(Integer.valueOf(args[1]));
                } else if (args[0].equals(OPTIONS[2])) {
                    main.gi.setTimeToShrink(Integer.valueOf(args[1]));
                } else if (args[0].equals(OPTIONS[3])) {
                    if (args[1].equalsIgnoreCase("true")) {
                        main.gi.setTeamMode(true);
                    } else if (args[1].equalsIgnoreCase("false")) {
                        main.gi.setTeamMode(false);
                    } else {
                        commandSender.sendMessage(ChatColor.RED + "ERROR: Option " + args[1] + " is not true or false.");
                        return true;
                    }
                }
            } catch (NumberFormatException e) {
                commandSender.sendMessage(ChatColor.RED + "ERROR: Option " + args[1] + " is not an integer.");
            }
            return true;
        }
        return false;
    }
}
