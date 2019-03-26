package usa.cactuspuppy.uhc_automation.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import usa.cactuspuppy.uhc_automation.command.commands.*;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.bukkit.plugin.java.annotation.command.Command(name = "uhc", desc = "Accesses the functionality of the UHC plugin", usage = "/uhc <subcommand> [args]")
public class CmdDelegator implements CommandExecutor, TabCompleter {
    private static Map<String, UHCCommand> commandMap = new HashMap<>();
    static {
        addCmd(new Surface());
        addCmd(new Start());
        addCmd(new Create());
        addCmd(new Debug());
        //TODO: Add aliases
    }

    private static void addCmd(UHCCommand c) {
        commandMap.put(c.getClass().getSimpleName().toLowerCase(), c);
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length < 1) return false;
        String subCmd = args[0];
        UHCCommand handler = commandMap.get(subCmd);
        if (handler == null) {
            commandSender.sendMessage(ChatColor.RED + "Unknown command " + subCmd);
            return true;
        }
        String[] newArgs = new String[0];
        System.arraycopy(args, 1, newArgs, 0, args.length - 1);
        if (!handler.hasPermission(commandSender, subCmd, newArgs)) {
            commandSender.sendMessage(ChatColor.RED + "Sorry, but you are not permitted to use this command.\nContact an administrator if you believe this is in error.");
            return true;
        }
        if (!handler.onCommand(commandSender, subCmd, newArgs)) {
            commandSender.sendMessage(ChatColor.RED + "Usage: " + handler.getUsage());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] strings) {
        return null;
    }

    //TODO: Add help function
}
