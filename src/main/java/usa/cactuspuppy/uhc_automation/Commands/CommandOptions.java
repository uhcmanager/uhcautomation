package usa.cactuspuppy.uhc_automation.Commands;

import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import usa.cactuspuppy.uhc_automation.Main;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor
public class CommandOptions implements CommandExecutor, TabCompleter {
    private static final String[] OPTIONS =
            {"init-size", "final-size", "mins-to-shrink", "team-mode", "spread-distance", "uhc-mode", "respect-teams", "episode-length", "event-name"};

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            return false;
        //event-name handling
        } else if (args[0].equalsIgnoreCase(OPTIONS[8])) {
            if (args.length < 2) {
                return false;
            }
            String eventName = StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " ");
            Main.getInstance().getConfig().set("event-name", eventName);
            Main.getInstance().saveConfig();
            commandSender.sendMessage("Successfully set " + args[0] + " to be " + eventName);
            return true;
        } else if (args.length != 2) {
            return false;
        }
        if (Arrays.asList(OPTIONS).contains(args[0])) {
            try {
                //init-size
                if (args[0].equalsIgnoreCase(OPTIONS[0])) {
                    if (Integer.valueOf(args[1]) <= Main.getInstance().getGameInstance().getFinalSize()) {
                        commandSender.sendMessage(ChatColor.RED + "ERROR: Requested initial border size " + ChatColor.RESET + Integer.valueOf(args[1])
                                        + ChatColor.RED + " is not larger than current final border size "
                                        + ChatColor.RESET + Main.getInstance().getGameInstance().getFinalSize());
                        return true;
                    } else if (Integer.valueOf(args[1]) < 0) {
                        commandSender.sendMessage(ChatColor.RED + "ERROR: Requested initial border size " + ChatColor.RESET + Integer.valueOf(args[1])
                                + ChatColor.RED + " is not greater than or equal to zero.");
                        return true;
                    }
                    int initSize = Integer.valueOf(args[1]);
                    if (initSize > 60000000 || initSize == 0) { initSize = 60000000; }
                    Main.getInstance().getGameInstance().setInitSize(initSize);
                    Main.getInstance().getConfig().set("game.init-size", initSize);
                //final-size
                } else if (args[0].equalsIgnoreCase(OPTIONS[1])) {
                    if (Integer.valueOf(args[1]) >= Main.getInstance().getGameInstance().getInitSize()) {
                        commandSender.sendMessage(ChatColor.RED + "ERROR: Requested final border size " + ChatColor.RESET + Integer.valueOf(args[1])
                                        + ChatColor.RED + " is not smaller than current initial border size "
                                        + ChatColor.RESET + Main.getInstance().getGameInstance().getInitSize());
                        return true;
                    } else if (Integer.valueOf(args[1]) < 0) {
                        commandSender.sendMessage(ChatColor.RED + "ERROR: Requested minutes to border shrink " + ChatColor.RESET + Integer.valueOf(args[1])
                                + ChatColor.RED + " is not greater than or equal to zero.");
                        return true;
                    }
                    int finalSize = Integer.valueOf(args[1]);
                    if (finalSize > 59999999 || finalSize == 0) { finalSize = 59999999; }
                    Main.getInstance().getGameInstance().setFinalSize(finalSize);
                    Main.getInstance().getConfig().set("game.final-size", finalSize);
                //minx-to-shrink
                } else if (args[0].equalsIgnoreCase(OPTIONS[2])) {
                    if (Integer.valueOf(args[1]) < -1) {
                        commandSender.sendMessage(ChatColor.RED + "ERROR: Requested final border size " + ChatColor.RESET + Integer.valueOf(args[1])
                                + ChatColor.RED + " is not positive, 0, or -1.");
                        return true;
                    }
                    Main.getInstance().getGameInstance().setMinsToShrink(Integer.valueOf(args[1]));
                    Main.getInstance().getConfig().set("game.mins-to-shrink", Integer.valueOf(args[1]));
                //team-mode
                } else if (args[0].equalsIgnoreCase(OPTIONS[3])) {
                    if (args[1].equalsIgnoreCase("true")) {
                        Main.getInstance().getGameInstance().setTeamMode(true);
                    } else if (args[1].equalsIgnoreCase("false")) {
                        Main.getInstance().getGameInstance().setTeamMode(false);
                    } else {
                        commandSender.sendMessage(ChatColor.RED + "ERROR: Option " + args[1] + " is not true or false.");
                        return true;
                    }
                    Main.getInstance().getConfig().set("game.team-mode", Boolean.valueOf(args[1]));
                //spread-distance
                } else if (args[0].equalsIgnoreCase(OPTIONS[4])) {
                    if (Integer.valueOf(args[1]) < 0) {
                        commandSender.sendMessage(ChatColor.RED + "ERROR: Requested initial separation distance " + ChatColor.RESET + Integer.valueOf(args[1])
                                + ChatColor.RED + " is not greater than or equal to zero.");
                        return true;
                    }
                    Main.getInstance().getGameInstance().setSpreadDistance(Integer.valueOf(args[1]));
                    Main.getInstance().getConfig().set("game.spread-distance", Integer.valueOf(args[1]));
                //uhc-mode
                } else if (args[0].equalsIgnoreCase(OPTIONS[5])) {
                    if (!(args[1].equals("true") || args[1].equals("false"))) {
                        commandSender.sendMessage(ChatColor.RED + "ERROR: Option " + args[1] + " is not true or false.");
                        return true;
                    }
                    Main.getInstance().getGameInstance().setUHCMode(Boolean.valueOf(args[1]));
                    Main.getInstance().getConfig().set("game.uhc-mode", Boolean.valueOf(args[1]));
                //respect-teams
                } else if (args[0].equalsIgnoreCase(OPTIONS[6])) {
                    if (args[1].equalsIgnoreCase("true")) {
                        Main.getInstance().getGameInstance().setRespectTeams(true);
                    } else if (args[1].equalsIgnoreCase("false")) {
                        Main.getInstance().getGameInstance().setRespectTeams(false);
                    } else {
                        commandSender.sendMessage(ChatColor.RED + "ERROR: Option " + args[1] + " is not true or false.");
                        return true;
                    }
                    Main.getInstance().getConfig().set("game.respect-teams", Boolean.valueOf(args[1]));
                //episode-length
                } else if (args[0].equalsIgnoreCase(OPTIONS[7])) {
                    if (Integer.valueOf(args[1]) < 0) {
                        commandSender.sendMessage(ChatColor.RED + "ERROR: Requested episode length " + ChatColor.RESET + Integer.valueOf(args[1])
                                + ChatColor.RED + " is not greater than or equal to zero.");
                        return true;
                    }
                    Main.getInstance().getGameInstance().setEpLength(Integer.valueOf(args[1]));
                    Main.getInstance().getConfig().set("game.episode-length", Integer.valueOf(args[1]));
                }
                Main.getInstance().saveConfig();
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

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.stream(OPTIONS).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        } else if (args.length == 2) {
            String option = args[0];
            if (!Arrays.asList(OPTIONS).contains(option)) {
                return null;
            } else if (isBooleanOption(option)) {
                return Stream.of("true", "false").filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
            }
            return null;
        }
        return null;
    }

    private boolean isBooleanOption(String s) {
        String[] booleans = {"team-mode", "uhc-mode", "respect-teams"};
        return Arrays.asList(booleans).contains(s);
    }
}
