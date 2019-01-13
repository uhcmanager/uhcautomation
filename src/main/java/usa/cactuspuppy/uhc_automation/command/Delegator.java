package usa.cactuspuppy.uhc_automation.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import usa.cactuspuppy.uhc_automation.command.commands.Surface;
import usa.cactuspuppy.uhc_automation.command.commands.UHCCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.bukkit.plugin.java.annotation.command.Command(name = "uhc", desc = "Accesses the functionality of the UHC plugin", usage = "/uhc <subcommand> [args]")
public class Delegator implements CommandExecutor, TabCompleter {
    private static Map<String, UHCCommand> commandMap = new HashMap<>();
    static {
        addCmd(new Surface());
        //TODO: Add aliases
    }

    private static void addCmd(UHCCommand c) {
        commandMap.put(c.getClass().getSimpleName(), c);
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length < 1) return false;
        String subCmd = args[0];
        UHCCommand handler = commandMap.get(subCmd);
        if (handler == null) {
            //TODO: Give feedback
            return true;
        }
        String[] newArgs = new String[0];
        System.arraycopy(args, 1, newArgs, 0, args.length - 1);
        if (!handler.hasPermission(commandSender, subCmd, newArgs)) {
            //TODO: Deny
            return true;
        }
        return handler.onCommand(commandSender, subCmd, newArgs);
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] strings) {
        return null;
    }
}
