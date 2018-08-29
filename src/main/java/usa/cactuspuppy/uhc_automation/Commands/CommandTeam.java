package usa.cactuspuppy.uhc_automation.Commands;

import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import usa.cactuspuppy.uhc_automation.Main;

import java.util.ArrayList;
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
            Main.getInstance().getLogger().info("Added " + args[2] + "to team " + team.getName() + " in " + Main.getInstance().getConfig().getString("event-name"));
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
            if (Arrays.stream(Team.Option.values()).noneMatch((v) -> v.name().equals(args[2].toUpperCase())) && !args[2].equals("color")) {
                commandSender.sendMessage(ChatColor.RED + "Unknown option " + ChatColor.WHITE + args[2] + ChatColor.RED + ".\n"
                        + ChatColor.YELLOW + "Acceptable options: " + ChatColor.WHITE + "color, name_tag_visibility, death_message_visibility, collision_rule");
                return;
            }
            // color first
            if (args[2].equals("color")) {
                if (args.length == 3) {
                    commandSender.sendMessage(ChatColor.GOLD + "The color of team " + ChatColor.WHITE + team.getName() + ChatColor.GOLD + " is " + ChatColor.WHITE + team.getColor().name());
                    return;
                }
                if (Arrays.stream(ChatColor.values()).noneMatch((v) -> v.name().equalsIgnoreCase(args[3]))) {
                    commandSender.sendMessage(ChatColor.RED + "Unknown color " + ChatColor.WHITE + args[3]);
                    return;
                }
                ChatColor color = ChatColor.valueOf(args[3].toUpperCase());
                team.setColor(color);
                commandSender.sendMessage(ChatColor.GREEN + "Successfully set team " + ChatColor.WHITE + ChatColor.GREEN + "'s color to " + ChatColor.WHITE + color.name());
                return;
            }
            // other options
            Team.Option option = Team.Option.valueOf(args[2].toUpperCase());
            if (args.length == 3) {
                commandSender.sendMessage(ChatColor.GOLD + "Option " + ChatColor.WHITE + option.name() + ChatColor.GOLD + " for team " + ChatColor.WHITE + team.getName() + ChatColor.GREEN + " is " + ChatColor.WHITE + team.getOption(option).name());
            } else {
                if (Arrays.stream(Team.OptionStatus.values()).noneMatch((v) -> v.name().equalsIgnoreCase(args[3].toUpperCase()))) {
                    commandSender.sendMessage(ChatColor.RED + "Value must be" + ChatColor.WHITE + " always, never, for_other_teams, or for_own_team");
                    return;
                }
                team.setOption(option, Team.OptionStatus.valueOf(args[3].toUpperCase()));
                commandSender.sendMessage(ChatColor.GREEN + "Set option " + ChatColor.WHITE + option.name() + ChatColor.GREEN + " for team " + ChatColor.WHITE + team.getName() + ChatColor.GREEN + " to " + ChatColor.WHITE + args[3].toUpperCase());
            }
        }
    }

    public static List<String> onTabComplete(String[] args) {
        if (args.length == 1) {
            return Arrays.stream(CommandTeam.SUBCOMMANDS).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        } else if (args.length == 2) {
            if (!args[0].equals("add")) {
                return Main.getInstance().getGameInstance().getScoreboard().getTeams().stream().map(Team::getName).filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
            }
        } else if (args.length == 3) {
            if (args[0].equals("join") || args[0].equals("leave")) {
                return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
            }
            if (args[0].equals("option")) {
                List<String> options = Arrays.stream(Team.Option.values()).map(Enum::name).collect(Collectors.toList());
                options.add("COLOR");
                return options.stream().filter(s -> s.startsWith(args[2].toUpperCase())).map(String::toLowerCase).collect(Collectors.toList());
            }
        } else if (args.length == 4) {
            if (args[0].equals("option")) {
                if (args[2].equals("color")) {
                    return Arrays.stream(ChatColor.values()).map(Enum::name).filter(s -> s.startsWith(args[3].toUpperCase())).map(String::toLowerCase).collect(Collectors.toList());
                }
                return Arrays.stream(Team.OptionStatus.values()).map(Enum::name).filter(s -> s.startsWith(args[3].toUpperCase())).map(String::toLowerCase).collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }
}
