package usa.cactuspuppy.uhc_automation.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.InfoDisplayMode;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.InfoModeCache;
import usa.cactuspuppy.uhc_automation.UHCUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandInfo implements CommandExecutor, TabCompleter {
    private Main m;

    public CommandInfo(Main main) {
        m = main;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length > 1) {
            return false;
        } else if (args.length == 0) {
            if (commandSender instanceof Player) {
                Player p = (Player) commandSender;
                InfoDisplayMode tdm = InfoModeCache.getInstance().getPlayerPref(p.getUniqueId());
                if (tdm == InfoDisplayMode.CHAT) {
                    UHCUtils.sendPlayerInfo(m, commandSender);
                } else {
                    p.sendMessage(ChatColor.YELLOW + "Your current info display option is not set to chat. Use " + ChatColor.RESET + ChatColor.ITALIC + "/uhcinfo chat" + ChatColor.RESET.toString() + ChatColor.YELLOW + " to make chat your info display preference.");
                }
            } else {
                UHCUtils.sendPlayerInfo(m, commandSender);
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
                    return true;
                }
                if (curr == next) {
                    return true;
                } else if (curr == InfoDisplayMode.SCOREBOARD) {
                    m.gi.getTimeAnnouncer().removePlayerfromObjectiveSet(p);
                }
                InfoModeCache.getInstance().storePlayerPref(p.getUniqueId(), next);
                commandSender.sendMessage(ChatColor.GREEN + "Changed info display preference to " + ChatColor.RESET + next.name().toLowerCase());
            } else {
                commandSender.sendMessage(ChatColor.RED + "Custom info display options are only allowed for in-game players!");
                return true;
            }
        }
        return true;
    }

    private InfoDisplayMode toggleTDM(InfoDisplayMode tdm) {
        if (tdm == InfoDisplayMode.CHAT) {
            return InfoDisplayMode.SCOREBOARD;
        }
        return InfoDisplayMode.CHAT;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.stream(InfoDisplayMode.values()).map(Enum::name).map(String::toLowerCase).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        }
        return null;
    }
}
