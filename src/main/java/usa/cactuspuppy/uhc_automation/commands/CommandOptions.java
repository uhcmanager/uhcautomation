package usa.cactuspuppy.uhc_automation.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.task.InfoAnnouncer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandOptions extends UHCCommand {
    private static final String[] OPTIONS =
            {"init-size", "final-size", "mins-to-shrink", "team-mode", "spread-distance", "uhc-mode", "respect-teams", "episode-length", "event-name", "secs-to-pvp", "one-shot"};

    public CommandOptions() {
        name = "options";
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /uhc " + alias + " <option> <value>");
        //event-name handling
        } else if (args[0].equalsIgnoreCase(OPTIONS[8])) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /uhc " + alias + " event-name <value>");
            }
            String eventName = StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " ");
            Main.getInstance().getConfig().set("event-name", eventName);
            Main.getInstance().saveConfig();
            InfoAnnouncer.getInstance().getObj().setDisplayName(ChatColor.GOLD + Main.getInstance().getConfig().getString("event-name", "Game Info"));
            sender.sendMessage("Successfully set " + args[0] + " to be " + eventName);
            return;
        } else if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /uhc " + alias + " <option> <value>");
        }
        if (Arrays.asList(OPTIONS).contains(args[0])) {
            boolean regen = false;
            try {
                //init-size
                if (args[0].equalsIgnoreCase(OPTIONS[0])) {
                    if (Integer.valueOf(args[1]) <= Main.getInstance().getGameInstance().getFinalSize()) {
                        sender.sendMessage(ChatColor.RED + "ERROR: Requested initial border size " + ChatColor.RESET + Integer.valueOf(args[1])
                                        + ChatColor.RED + " is not larger than current final border size "
                                        + ChatColor.RESET + Main.getInstance().getGameInstance().getFinalSize());
                        return;
                    } else if (Integer.valueOf(args[1]) < 0) {
                        sender.sendMessage(ChatColor.RED + "ERROR: Requested initial border size " + ChatColor.RESET + Integer.valueOf(args[1])
                                + ChatColor.RED + " is not greater than or equal to zero.");
                        return;
                    }
                    int initSize = Integer.valueOf(args[1]);
                    if (initSize > 60000000 || initSize == 0) { initSize = 60000000; }
                    Main.getInstance().getGameInstance().setInitSize(initSize);
                    Main.getInstance().getConfig().set("game.init-size", initSize);
                //final-size
                } else if (args[0].equalsIgnoreCase(OPTIONS[1])) {
                    if (Integer.valueOf(args[1]) >= Main.getInstance().getGameInstance().getInitSize()) {
                        sender.sendMessage(ChatColor.RED + "ERROR: Requested final border size " + ChatColor.RESET + Integer.valueOf(args[1])
                                        + ChatColor.RED + " is not smaller than current initial border size "
                                        + ChatColor.RESET + Main.getInstance().getGameInstance().getInitSize());
                        return;
                    } else if (Integer.valueOf(args[1]) < 0) {
                        sender.sendMessage(ChatColor.RED + "ERROR: Requested minutes to border shrink " + ChatColor.RESET + Integer.valueOf(args[1])
                                + ChatColor.RED + " is not greater than or equal to zero.");
                        return;
                    }
                    int finalSize = Integer.valueOf(args[1]);
                    if (finalSize > 59999999 || finalSize == 0) { finalSize = 59999999; }
                    Main.getInstance().getGameInstance().setFinalSize(finalSize);
                    Main.getInstance().getConfig().set("game.final-size", finalSize);
                //mins-to-shrink
                } else if (args[0].equalsIgnoreCase(OPTIONS[2])) {
                    if (Integer.valueOf(args[1]) < -1) {
                        sender.sendMessage(ChatColor.RED + "ERROR: Requested final border size " + ChatColor.RESET + Integer.valueOf(args[1])
                                + ChatColor.RED + " is not positive, 0, or -1.");
                        return;
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
                        sender.sendMessage(ChatColor.RED + "ERROR: Option " + args[1] + " is not true or false.");
                        return;
                    }
                    Main.getInstance().getConfig().set("game.team-mode", Boolean.valueOf(args[1]));
                    regen = true;
                //spread-distance
                } else if (args[0].equalsIgnoreCase(OPTIONS[4])) {
                    if (Integer.valueOf(args[1]) < 0) {
                        sender.sendMessage(ChatColor.RED + "ERROR: Requested initial separation distance " + ChatColor.RESET + Integer.valueOf(args[1])
                                + ChatColor.RED + " is not greater than or equal to zero.");
                        return;
                    }
                    Main.getInstance().getGameInstance().setSpreadDistance(Integer.valueOf(args[1]));
                    Main.getInstance().getConfig().set("game.spread-distance", Integer.valueOf(args[1]));
                //uhc-mode
                } else if (args[0].equalsIgnoreCase(OPTIONS[5])) {
                    if (!(args[1].equals("true") || args[1].equals("false"))) {
                        sender.sendMessage(ChatColor.RED + "ERROR: Option " + args[1] + " is not true or false.");
                        return;
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
                        sender.sendMessage(ChatColor.RED + "ERROR: Option " + args[1] + " is not true or false.");
                        return;
                    }
                    Main.getInstance().getConfig().set("game.respect-teams", Boolean.valueOf(args[1]));
                //episode-length
                } else if (args[0].equalsIgnoreCase(OPTIONS[7])) {
                    if (Integer.valueOf(args[1]) < 0) {
                        sender.sendMessage(ChatColor.RED + "ERROR: Requested episode length " + ChatColor.RESET + Integer.valueOf(args[1])
                                + ChatColor.RED + " is not greater than or equal to zero.");
                        return;
                    }
                    Main.getInstance().getGameInstance().setEpLength(Integer.valueOf(args[1]));
                    Main.getInstance().getConfig().set("game.episode-length", Integer.valueOf(args[1]));
                //secs-to-pvp
                } else if (args[0].equalsIgnoreCase(OPTIONS[8])) {
                    if (Integer.valueOf(args[1]) < 0) {
                        sender.sendMessage(ChatColor.RED + "ERROR: Requested seconds to pvp " + ChatColor.RESET + Integer.valueOf(args[1])
                                + ChatColor.RED + " is not greater than or equal to zero.");
                        return;
                    }
                    Main.getInstance().getGameInstance().setSecsToPVP(Integer.valueOf(args[1]));
                    Main.getInstance().getConfig().set("game.secs-to-pvp", Integer.valueOf(args[1]));
                //one-shot
                } else if (args[0].equalsIgnoreCase(OPTIONS[9])) {
                    if (!(args[1].equals("true") || args[1].equals("false"))) {
                        sender.sendMessage(ChatColor.RED + "ERROR: Option " + args[1] + " is not true or false.");
                        return;
                    }
                    Main.getInstance().getConfig().set("one-shot.enabled", false);
                }
                Main.getInstance().saveConfig();
                if (regen) InfoAnnouncer.getInstance().regenerateObjective();
                sender.sendMessage("Successfully set " + args[0] + " to be " + args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "ERROR: Option " + args[0] + " only accepts integers. If you typed a number, it may be too large or too small. Try a number closer to zero.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "ERROR: Option " + args[0] + " not recognized.\nValid options: " + ChatColor.RESET + StringUtils.join(OPTIONS, ", "));
        }
    }

    public static List<String> onTabComplete(String[] args) {
        if (args.length == 1) {
            return Arrays.stream(OPTIONS).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        } else if (args.length == 2) {
            String option = args[0];
            if (!Arrays.asList(OPTIONS).contains(option)) {
                return null;
            } else if (isBooleanOption(option)) {
                return Stream.of("true", "false").filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
            }
            return new ArrayList<>();
        }
        return null;
    }

    private static boolean isBooleanOption(String s) {
        String[] booleans = {"team-mode", "uhc-mode", "respect-teams"};
        return Arrays.asList(booleans).contains(s);
    }
}
