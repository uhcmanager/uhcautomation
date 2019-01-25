package usa.cactuspuppy.uhc_automation.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.Constants;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.GameManager;

import java.util.List;

public class Start extends UHCCommand implements TabCompleter {
    @Override
    public String getUsage() {
        return "/uhc start [secs]";
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }

//    @Override
//    public boolean onCommand(CommandSender commandSender, String alias, String[] args) {
//        if (!(commandSender instanceof Player)) {
//            commandSender.sendMessage(ChatColor.RED + Constants.getDenyPermission());
//            return true;
//        }
//        GameInstance instance = GameManager.getPlayerGame(((Player) commandSender).getUniqueId());
//        if (instance == null) {
//            commandSender.sendMessage(ChatColor.RED + "You are currently not in a game world, join one to start a game.");
//            return true;
//        }
//        if (args.length > 0) {
//            try {
//                int delay = Integer.valueOf(args[0]);
//                new ToInitCountdown(delay, instance);
//                return true;
//            } catch (NumberFormatException e) {
//                commandSender.sendMessage(ChatColor.RED + "Unknown integer: " + args[0]);
//                return true;
//            }
//        }
//        instance.init();
//        return true;
//    }

    @Override
    public boolean onCommand(CommandSender commandSender, String alias, String[] args) {
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender, String alias, String[] args) {
        return commandSender.hasPermission("uhc.manager");
    }
}
