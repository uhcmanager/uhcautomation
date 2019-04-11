package usa.cactuspuppy.uhc_automation.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import usa.cactuspuppy.uhc_automation.Constants;
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
        addCmd(new Join());
        addCmd(new Leave());
        addCmd(new Option());
        //Add aliases
        addAlias("s", Surface.class);
        addAlias("d", Debug.class);
    }

    private static void addCmd(UHCCommand c) {
        String subcommand = c.getClass().getSimpleName().toLowerCase();
        if (commandMap.containsKey(subcommand)) {
            Logger.logSevere(CmdDelegator.class, "Attempt to register two handlers to the same subcommand");
            return;
        }
        commandMap.put(subcommand, c);
    }

    private static void addAlias(String alias, Class<? extends UHCCommand> command) {
        if (commandMap.containsKey(alias)) {
            Logger.logSevere(CmdDelegator.class, "Alias  " + alias + " already registered as alias or subcommand, please choose a different alias.");
            return;
        }
        String subcommand = command.getSimpleName().toLowerCase();
        if (!commandMap.containsKey(subcommand)) {
            Logger.logSevere(CmdDelegator.class, "Attempt to register alias " + alias + " for unregistered subcommand " + subcommand);
            return;
        }
        UHCCommand handler = commandMap.get(subcommand);
        commandMap.put(alias, handler);
    }


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length < 1) return false;
        String subCmd = args[0];
        UHCCommand handler = commandMap.get(subCmd);
        if (handler == null) {
            commandSender.sendMessage(ChatColor.RED + "Unknown command " + subCmd);
            return true;
        }
        String[] newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, args.length - 1);
        if (!handler.hasPermission(commandSender, subCmd, newArgs)) {
            commandSender.sendMessage(ChatColor.RED + Constants.getDenyPermission());
            return true;
        }
        if (!handler.onCommand(commandSender, subCmd, newArgs)) {
            commandSender.sendMessage(ChatColor.RED + "Usage: " + handler.getUsage());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }

    //TODO: Add help function
}
