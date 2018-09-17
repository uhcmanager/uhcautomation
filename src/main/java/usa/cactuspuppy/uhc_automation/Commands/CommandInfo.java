package usa.cactuspuppy.uhc_automation.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.InfoDisplayMode;
import usa.cactuspuppy.uhc_automation.InfoModeCache;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandInfo extends UHCCommand {
    public CommandInfo() {
        name = "info";
        adminOnly = false;
    }

    @Override
    public void onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length > 1) {
            commandSender.sendMessage(ChatColor.RED + "Usage: /uhc info [toggle:chat:scoreboard]");
        } else if (args.length == 0) {
            if (commandSender instanceof Player) {
                Player p = (Player) commandSender;
                InfoDisplayMode tdm = InfoModeCache.getInstance().getPlayerPref(p.getUniqueId());
                if (tdm == InfoDisplayMode.CHAT) {
                    UHCUtils.sendPlayerInfo(Main.getInstance(), commandSender);
                } else {
                    p.sendMessage(ChatColor.YELLOW + "Your current info display option is not set to chat. Use " + ChatColor.RESET + ChatColor.ITALIC + "/uhc info chat" + ChatColor.RESET.toString() + ChatColor.YELLOW + " to make chat your info display preference.");
                }
            } else {
                UHCUtils.sendPlayerInfo(Main.getInstance(), commandSender);
            }
        } else {
            Player p;
            if (commandSender instanceof Player) {
                p = (Player) commandSender;
                InfoDisplayMode curr = InfoModeCache.getInstance().getPlayerPref(p.getUniqueId());
                InfoDisplayMode next;
                if (args[0].equalsIgnoreCase("toggle")) {
                    next = toggleTDM(curr);
                } else {
                    next = InfoDisplayMode.fromString(args[0]);
                }
                if (next == null) {
                    p.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Requested info display mode " + ChatColor.RESET + args[0] + ChatColor.RED.toString() + ChatColor.BOLD + " not found. Acceptable options: " + ChatColor.RESET + "chat, scoreboard");
                    return;
                }
                if (curr == next) {
                    return;
                } else if (curr == InfoDisplayMode.SCOREBOARD) {
                    Main.getInstance().getGameInstance().getInfoAnnouncer().removePlayerFromObjectiveSet(p);
                }
                InfoModeCache.getInstance().storePlayerPref(p.getUniqueId(), next);
                commandSender.sendMessage(ChatColor.GREEN + "Changed info display preference to " + ChatColor.RESET + next.name().toLowerCase());
            } else {
                commandSender.sendMessage(ChatColor.RED + "Custom info display options are only allowed for in-game players!");
            }
        }
    }

    private static InfoDisplayMode toggleTDM(InfoDisplayMode tdm) {
        if (tdm == InfoDisplayMode.CHAT) {
            return InfoDisplayMode.SCOREBOARD;
        }
        return InfoDisplayMode.CHAT;
    }

    public static List<String> onTabComplete(String[] args) {
        List<String> options = Arrays.stream(InfoDisplayMode.values()).map(Enum::name).map(String::toLowerCase).collect(Collectors.toList());
        options.add("toggle");
        return options.stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
    }
}
