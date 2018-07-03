package usa.cactuspuppy.uhc_automation.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.TimeDisplayMode;
import usa.cactuspuppy.uhc_automation.TimeModeCache;
import usa.cactuspuppy.uhc_automation.UHCUtils;

public class CommandTime implements CommandExecutor {
    private Main m;

    public CommandTime(Main main) {
        m = main;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length > 1) {
            return false;
        } else if (args.length == 0) {
            if (commandSender instanceof Player) {
                Player p = (Player) commandSender;
                TimeDisplayMode tdm = TimeModeCache.getInstance().getPlayerPref(p.getUniqueId());
                if (tdm == TimeDisplayMode.CHAT) {
                    UHCUtils.sendPlayerTime(m, commandSender);
                } else {
                    p.sendMessage(ChatColor.YELLOW + "Your current time display option is not set to chat. Use " + ChatColor.RESET + ChatColor.ITALIC + "/uhctime chat" + ChatColor.RESET.toString() + ChatColor.YELLOW + " to make chat your time display preference.");
                }
            } else {
                UHCUtils.sendPlayerTime(m, commandSender);
            }
        } else {
            Player p;
            if (commandSender instanceof Player) {
                p = (Player) commandSender;
                TimeDisplayMode curr = TimeModeCache.getInstance().getPlayerPref(p.getUniqueId());
                if (args[0].equalsIgnoreCase("toggle")) {
                    TimeModeCache.getInstance().storePlayerPref(p.getUniqueId(), toggleTDM(curr));
                } else {
                    TimeDisplayMode next = TimeDisplayMode.fromString(args[0]);
                    if (next == null) {
                        p.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Requested time display mode " + ChatColor.RESET + args[0] + ChatColor.RED.toString() + ChatColor.BOLD + " not found. Acceptable options: " + ChatColor.RESET + "chat, subtitle, scoreboard");
                    }
                }
            } else {
                commandSender.sendMessage(ChatColor.RED + "Custom time display options are only allowed for in-game players!");
                return true;
            }
        }
        return true;
    }

    private TimeDisplayMode toggleTDM(TimeDisplayMode tdm) {
        if (tdm == TimeDisplayMode.CHAT) {
            return TimeDisplayMode.SUBTITLE;
        } else if (tdm == TimeDisplayMode.SUBTITLE) {
            return TimeDisplayMode.SCOREBOARD;
        } else if (tdm == TimeDisplayMode.SCOREBOARD) {
            return TimeDisplayMode.CHAT;
        }
        return TimeDisplayMode.CHAT;
    }
}
