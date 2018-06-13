package usa.cactuspuppy.uhc_automation;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class CommandOptions implements CommandExecutor {
    private Main main;
    private static final String[] OPTIONS =
            {"init-size", "final-size", "mins-to-shrink", "team-mode", "spread-distance", "uhc-mode", "respect-teams", "episode-length", "event-name"};

    public CommandOptions(Main m) {
        main = m;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        //event-name handling
        } else if (args[0].equalsIgnoreCase(OPTIONS[8])) {
            if (args.length < 2) {
                return false;
            }
            String eventName = StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " ");
            main.getConfig().set("event-name", eventName);
            main.saveConfig();
            commandSender.sendMessage("Successfully set " + args[0] + " to be " + eventName);
            return true;
        } else if (args.length != 2) {
            return false;
        }
        if (Arrays.asList(OPTIONS).contains(args[0])) {
            try {
                //init-size
                if (args[0].equalsIgnoreCase(OPTIONS[0])) {
                    if (Integer.valueOf(args[1]) <= main.gi.getFinalSize()) {
                        commandSender.sendMessage(ChatColor.RED + "ERROR: Requested initial border size " + ChatColor.RESET + Integer.valueOf(args[1])
                                        + ChatColor.RED + " is not larger than current final border size "
                                        + ChatColor.RESET + main.gi.getInitSize());
                        return true;
                    } else if (Integer.valueOf(args[1]) < 0) {
                        commandSender.sendMessage(ChatColor.RED + "ERROR: Requested initial border size " + ChatColor.RESET + Integer.valueOf(args[1])
                                + ChatColor.RED + " is not greater than or equal to zero.");
                        return true;
                    }
                    int initSize = Integer.valueOf(args[1]);
                    if (initSize > 60000000 || initSize == 0) { initSize = 60000000; }
                    main.gi.setInitSize(initSize);
                    main.getConfig().set("game.init-size", initSize);
                //final-size
                } else if (args[0].equalsIgnoreCase(OPTIONS[1])) {
                    if (Integer.valueOf(args[1]) >= main.gi.getInitSize()) {
                        commandSender.sendMessage(ChatColor.RED + "ERROR: Requested final border size " + ChatColor.RESET + Integer.valueOf(args[1])
                                        + ChatColor.RED + " is not smaller than current initial border size "
                                        + ChatColor.RESET + main.gi.getInitSize());
                        return true;
                    } else if (Integer.valueOf(args[1]) < 0) {
                        commandSender.sendMessage(ChatColor.RED + "ERROR: Requested minutes to border shrink " + ChatColor.RESET + Integer.valueOf(args[1])
                                + ChatColor.RED + " is not greater than or equal to zero.");
                        return true;
                    }
                    int finalSize = Integer.valueOf(args[1]);
                    if (finalSize > 59999999 || finalSize == 0) { finalSize = 59999999; }
                    main.gi.setFinalSize(finalSize);
                    main.getConfig().set("game.final-size", finalSize);
                //minx-to-shrink
                } else if (args[0].equalsIgnoreCase(OPTIONS[2])) {
                    if (Integer.valueOf(args[1]) < -1) {
                        commandSender.sendMessage(ChatColor.RED + "ERROR: Requested final border size " + ChatColor.RESET + Integer.valueOf(args[1])
                                + ChatColor.RED + " is not positive, 0, or -1.");
                        return true;
                    }
                    main.gi.setTimeToShrink(Integer.valueOf(args[1]));
                    main.getConfig().set("game.mins-to-shrink", Integer.valueOf(args[1]));
                //team-mode
                } else if (args[0].equalsIgnoreCase(OPTIONS[3])) {
                    if (args[1].equalsIgnoreCase("true")) {
                        main.gi.setTeamMode(true);
                    } else if (args[1].equalsIgnoreCase("false")) {
                        main.gi.setTeamMode(false);
                    } else {
                        commandSender.sendMessage(ChatColor.RED + "ERROR: Option " + args[1] + " is not true or false.");
                        return true;
                    }
                    main.getConfig().set("game.team-mode", Boolean.valueOf(args[1]));
                //spread-distance
                } else if (args[0].equalsIgnoreCase(OPTIONS[4])) {
                    if (Integer.valueOf(args[1]) < 0) {
                        commandSender.sendMessage(ChatColor.RED + "ERROR: Requested initial separation distance " + ChatColor.RESET + Integer.valueOf(args[1])
                                + ChatColor.RED + " is not greater than or equal to zero.");
                        return true;
                    }
                    main.gi.setSpreadDistance(Integer.valueOf(args[1]));
                    main.getConfig().set("game.spread-distance", Integer.valueOf(args[1]));
                //uhc-mode
                } else if (args[0].equalsIgnoreCase(OPTIONS[5])) {
                    if (args[1].equalsIgnoreCase("true")) {
                        UHCUtils.exeCmd(Bukkit.getServer(), main.gi.getWorld(), "gamerule naturalRegeneration false");
                    } else if (args[1].equalsIgnoreCase("false")) {
                        UHCUtils.exeCmd(Bukkit.getServer(), main.gi.getWorld(), "gamerule naturalRegeneration true");
                    } else {
                        commandSender.sendMessage(ChatColor.RED + "ERROR: Option " + args[1] + " is not true or false.");
                        return true;
                    }
                    main.gi.setUHCMode(Boolean.valueOf(args[1]));
                    main.getConfig().set("game.uhc-mode", Boolean.valueOf(args[1]));
                //respect-teams
                } else if (args[0].equalsIgnoreCase(OPTIONS[6])) {
                    if (args[1].equalsIgnoreCase("true")) {
                        main.gi.setRespectTeams(true);
                    } else if (args[1].equalsIgnoreCase("false")) {
                        main.gi.setRespectTeams(false);
                    } else {
                        commandSender.sendMessage(ChatColor.RED + "ERROR: Option " + args[1] + " is not true or false.");
                        return true;
                    }
                    main.getConfig().set("game.respect-teams", Boolean.valueOf(args[1]));
                //episode-length
                } else if (args[0].equalsIgnoreCase(OPTIONS[7])) {
                    if (Integer.valueOf(args[1]) < 0) {
                        commandSender.sendMessage(ChatColor.RED + "ERROR: Requested episode length " + ChatColor.RESET + Integer.valueOf(args[1])
                                + ChatColor.RED + " is not greater than or equal to zero.");
                        return true;
                    }
                    main.gi.setEpLength(Integer.valueOf(args[1]));
                    main.getConfig().set("game.episode-length", Integer.valueOf(args[1]));
                }
                main.saveConfig();
                commandSender.sendMessage("Successfully set " + args[0] + " to be " + args[1]);
                return true;
            } catch (NumberFormatException e) {
                commandSender.sendMessage(ChatColor.RED + "ERROR: Option " + args[0] + " only accepts integers. If you typed a number, it may be too large or too small. Try a number closer to zero.");
                return false;
            }
        } else {
            commandSender.sendMessage(ChatColor.RED + "ERROR: Option " + args[0] + " not recognized.\nValid options: " + ChatColor.RESET + StringUtils.join(OPTIONS, ", "));
            return true;
        }
    }
}
