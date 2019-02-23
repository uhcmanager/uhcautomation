package usa.cactuspuppy.uhc_automation.commands;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import usa.cactuspuppy.uhc_automation.InfoDisplayMode;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

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
            commandSender.sendMessage(ChatColor.RED + "Usage: /uhc info [toggle]");
        } else if (args.length == 0) {
            UHCUtils.sendPlayerInfo(Main.getInstance(), commandSender);
            return;
        }
        if (args[0].equals("toggle")) {
            if (!commandSender.hasPermission("uhc.admin")) { //Deny permission
                commandSender.sendMessage(ChatColor.RED + "You do not have to toggle the scoreboard! Please contact a server administrator if you believe this is in error.");
                return;
            }
            toggleTDM();
        }
    }

    private static void toggleTDM() {
        if (mode == InfoDisplayMode.CHAT) {
            mode = InfoDisplayMode.SCOREBOARD;
        }
        mode = InfoDisplayMode.CHAT;
    }

    public static List<String> onTabComplete(String[] args) {
        List<String> options = Arrays.stream(InfoDisplayMode.values()).map(Enum::name).map(String::toLowerCase).collect(Collectors.toList());
        options.add("toggle");
        return options.stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
    }
}
