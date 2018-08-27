package usa.cactuspuppy.uhc_automation.Commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import usa.cactuspuppy.uhc_automation.Main;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandTeam {

    public static final String[] SUBCOMMANDS = {"add", "remove", "join", "leave", "option"};

    public static void onCommand(CommandSender commandSender, String[] args) {
        if (args.length < 2) {
            commandSender.sendMessage(ChatColor.RED + "Usage: /uhc team <subcommand> <args>");
            return;
        }
        String subcommand = args[0];
        if (!Arrays.asList(SUBCOMMANDS).contains(subcommand)) {
            commandSender.sendMessage(ChatColor.RED + "Subcommand " + ChatColor.WHITE + subcommand + ChatColor.RED + " not recognized. Acceptable options: " + ChatColor.WHITE + "add, remove, join, leave, option");
            return;
        }
        Scoreboard mainSB = Main.getInstance().getGameInstance().getScoreboard();
        /*Add Team*/
        if (subcommand.equals("add")) {
            String teamName = args[1];
            if (!StringUtils.isAlphanumericSpace(teamName.replace("_", " "))) {
                commandSender.sendMessage(ChatColor.RED + "Team name must only contain numbers, letters, and underscores!");
                return;
            }
            if (teamName.length() > 16) {
                commandSender.sendMessage(ChatColor.RED + "Team name cannot be longer than 16 characters!");
                return;
            }
            if (mainSB.getTeam(teamName) != null) {
                commandSender.sendMessage(ChatColor.RED + "A team with the name " + ChatColor.WHITE + teamName + ChatColor.RED + " already exists!");
                return;
            }
            mainSB.registerNewTeam(teamName);
            commandSender.sendMessage(ChatColor.GREEN + "Successfully created team " + ChatColor.WHITE + teamName);
            Main.getInstance().getLogger().info("Created team " + teamName + " in " + Main.getInstance().getConfig().getString("event-name"));
            return;
        }
        Team team = mainSB.getTeam(args[1]);
        if (team == null) {
            commandSender.sendMessage(ChatColor.RED + "Could not find team " + ChatColor.WHITE + args[1]);
            return;
        }
        /*Remove Team*/
        if (subcommand.equals("remove")) {
            team.unregister();
            commandSender.sendMessage(ChatColor.GREEN + "Successfully removed team " + ChatColor.WHITE + args[1]);
            Main.getInstance().getLogger().info("Removed team " + args[1] + " in " + Main.getInstance().getConfig().getString("event-name"));
            return;
        }
        /*Join Team*/
        if (subcommand.equals("join")) {
            if (args.length < 3) {
                commandSender.sendMessage(ChatColor.RED + "Usage: /uhc team join <team name> <player name>");
                return;
            }
            if (Bukkit.getPlayerExact(args[2]) == null) {
                commandSender.sendMessage(ChatColor.RED + "Could not find player " + ChatColor.WHITE + args[2]);
                return;
            }
            team.addEntry(args[2]);
            commandSender.sendMessage(ChatColor.GREEN + "Successfully added " + ChatColor.WHITE + args[2] + ChatColor.GREEN + " to team " + ChatColor.WHITE + team.getName());
            Main.getInstance().getLogger().info("Removed " + args[2] + "from team " + team.getName() + " in " + Main.getInstance().getConfig().getString("event-name"));
            return;
        }
        /*Leave Team*/
        if (subcommand.equals("leave")) {
            if (args.length < 3) {
                commandSender.sendMessage(ChatColor.RED + "Usage: /uhc team leave <team name> <player name>");
                return;
            }
            if (Bukkit.getPlayerExact(args[2]) == null) {
                commandSender.sendMessage(ChatColor.RED + "Could not find player " + ChatColor.WHITE + args[2]);
                return;
            }
            team.removeEntry(args[2]);
            commandSender.sendMessage(ChatColor.GREEN + "Successfully removed " + ChatColor.WHITE + args[2] + ChatColor.GREEN + " from team " + ChatColor.WHITE + team.getName());
        }
        /*Options*/
        if (subcommand.equals("option")) {
            if (args.length < 3) {
                commandSender.sendMessage(ChatColor.RED + "Usage: /uhc team option <team name> <option> [value]");
                return;
            }
            if (Arrays.stream(Team.Option.values()).noneMatch((v) -> v.name().equals(args[2].toUpperCase())) && !args[3].equals("color")) {
                commandSender.sendMessage(ChatColor.RED + "Unknown ");
            }
            if (args.length == 3) {
                Team.Option option = Team.Option.valueOf(args[2]);
            }
        }
    }

    public static List<String> onTabComplete(String[] args) {
        if (args.length == 1) {
            return Arrays.stream(CommandTeam.SUBCOMMANDS).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        } else if (args.length == 0) {
            return Arrays.asList(CommandTeam.SUBCOMMANDS);
        } else {
            return null;
        }
    }
}
