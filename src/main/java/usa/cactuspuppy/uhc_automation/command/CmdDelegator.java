package usa.cactuspuppy.uhc_automation.command;

import com.google.common.base.Strings;
import net.md_5.bungee.api.chat.*;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import usa.cactuspuppy.uhc_automation.Constants;
import usa.cactuspuppy.uhc_automation.command.commands.*;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.util.*;
import java.util.stream.Collectors;

@org.bukkit.plugin.java.annotation.command.Command(name = "uhc", desc = "Accesses the functionality of the UHC plugin", usage = "/uhc <subcommand> [args]")
public class CmdDelegator implements CommandExecutor, TabCompleter {
    private static Map<String, UHCCommand> commandMap = new HashMap<>();
    private static Map<String, UHCCommand> aliasMap = new HashMap<>();

    static {
        addCmd(new Create());
        addCmd(new Debug());
        addCmd(new Join());
        addCmd(new Leave());
        addCmd(new ListGames());
        addCmd(new Option());
        addCmd(new Reset());
        addCmd(new Start());
        addCmd(new Surface());
        //Add aliases
        addAlias("s", Surface.class);
        addAlias("d", Debug.class);
        addAlias("j", Join.class);
        addAlias("l", Leave.class);
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
        aliasMap.put(alias, handler);
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length < 1) return false;
        String subCmd = args[0];
        if (subCmd.equalsIgnoreCase("help")) {
            help(commandSender);
            return true;
        }
        UHCCommand handler = commandMap.get(subCmd);
        if (handler == null) {
            //Try for alias
            handler = aliasMap.get(subCmd);
            if (handler == null) {
                commandSender.sendMessage(ChatColor.RED + "Unknown command " + subCmd);
                return true;
            }
        }
        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
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
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        List<String> empty = new ArrayList<>();
        if (args.length == 0) {
            return empty;
        }
        if (args.length == 1) {
            Set<String> subcmds = new HashSet<>(commandMap.keySet());
            subcmds.add("help");
            return subcmds.stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        }
        UHCCommand handler = getHandler(args[0]);
        if (handler == null) {
            return empty;
        }
        return handler.onTabComplete(commandSender, command, args[0], Arrays.copyOfRange(args, 1, args.length));
    }

    /**
     * Gets the handler for this subcommand or alias, or null if there is none
     *
     * @param arg Name of subcommand or alias thereof
     * @return Handler for the argument
     */
    public UHCCommand getHandler(String arg) {
        UHCCommand handler = commandMap.get(arg);
        if (handler == null) {
            handler = aliasMap.get(arg);
        }
        return handler;
    }

    private void help(CommandSender sender) {
        //Constants
        int maxHelpLineLength = 40; //Max characters per line in hover help
        //TODO
        ComponentBuilder helpMsg = new ComponentBuilder("");

        String separator = ChatColor.YELLOW + "<" + ChatColor.WHITE + Strings.repeat("=", 7) + ChatColor.GOLD + "UHC Help" + ChatColor.WHITE + Strings.repeat("=", 7) + ChatColor.YELLOW + ">";
        String help = ChatColor.GRAY + "Hover over a command to see more information, click to insert it into chat.";
        helpMsg.append(separator).append("\n").append(help).append("\n");
        for (String subcommand : commandMap.keySet().stream().sorted().collect(Collectors.toList())) {
            UHCCommand command = commandMap.get(subcommand);
            if (command == null || !command.hasPermission(sender, subcommand, new String[0])) {
                continue;
            }
            String bullet = ChatColor.WHITE + "-";
            String usage = ChatColor.AQUA + command.getUsage();
            String purpose = ChatColor.GREEN + command.getPurpose();
            String info = command.getMoreInfo();
            info = WordUtils.wrap(info, maxHelpLineLength);

            BaseComponent interact = new TextComponent(usage);
            interact.setHoverEvent(new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(usage).append("\n").append(info).create()
            ));
            interact.setClickEvent(new ClickEvent(
                    ClickEvent.Action.SUGGEST_COMMAND,
                    "/uhc " + subcommand
            ));

            helpMsg.append(bullet).append(" ").append(interact).retain(ComponentBuilder.FormatRetention.NONE).append(" ").append(purpose).append("\n");
        }

        sender.spigot().sendMessage(helpMsg.create());
    }
}
