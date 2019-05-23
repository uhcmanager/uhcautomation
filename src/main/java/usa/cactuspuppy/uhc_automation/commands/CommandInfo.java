package usa.cactuspuppy.uhc_automation.commands;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import usa.cactuspuppy.uhc_automation.InfoDisplayMode;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;
import usa.cactuspuppy.uhc_automation.task.InfoAnnouncer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandInfo extends UHCCommand {
    @Getter private static InfoDisplayMode mode = InfoDisplayMode.SCOREBOARD;
    public CommandInfo() {
        name = "info";
        adminOnly = false;
    }

    @Override
    public void onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length > 1) {
            commandSender.sendMessage(ChatColor.RED + "Usage: /uhc info [toggle:chat:scoreboard]");
        } else if (args.length == 0) {
            UHCUtils.sendPlayerInfo(Main.getInstance(), commandSender);
            return;
        }
        String modeArg = args[0];
        if (!commandSender.hasPermission("uhc.admin")) { //Deny permission
            commandSender.sendMessage(ChatColor.RED + "You do not have to alter the scoreboard! Please contact a server administrator if you believe this is in error.");
            return;
        }
        if (modeArg.equalsIgnoreCase("toggle")) {
            toggleTDM();
            commandSender.sendMessage(ChatColor.GREEN + "Toggled display mode to: " + ChatColor.RESET + mode.name().toLowerCase());
        } else if (modeArg.equalsIgnoreCase("chat")) {
            if (mode == InfoDisplayMode.CHAT) {
                commandSender.sendMessage(ChatColor.YELLOW + "Display mode did not change");
                return;
            }
            mode = InfoDisplayMode.CHAT;
            commandSender.sendMessage(ChatColor.GREEN + "Set display mode to chat-only");
        } else if (modeArg.equalsIgnoreCase("scoreboard")) {
            if (mode == InfoDisplayMode.SCOREBOARD) {
                commandSender.sendMessage(ChatColor.YELLOW + "Display mode did not change");
                return;
            }
            mode = InfoDisplayMode.SCOREBOARD;
            commandSender.sendMessage(ChatColor.GREEN + "Set display mode to scoreboard");
        } else {
            commandSender.sendMessage(ChatColor.RED + "Unknown display mode");
        }
    }

    private static void toggleTDM() {
        if (mode == InfoDisplayMode.CHAT) {
            mode = InfoDisplayMode.SCOREBOARD;
            return;
        }
        mode = InfoDisplayMode.CHAT;
    }

    public static List<String> onTabComplete(String[] args) {
        List<String> options = Arrays.stream(InfoDisplayMode.values()).map(Enum::name).map(String::toLowerCase).collect(Collectors.toList());
        options.add("toggle");
        return options.stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
    }
}
