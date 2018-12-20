package usa.cactuspuppy.uhc_automation.Commands;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

import java.util.*;
import java.util.stream.Collectors;

@org.bukkit.plugin.java.annotation.command.Command(name = "uhc", desc = "Accesses the functionality of the UHC plugin", usage = "/uhc <subcommand> [args]")
public class CommandHandler implements CommandExecutor, TabCompleter {
    @Getter private static final Map<String, UHCCommand> SUBCOMMANDS = new HashMap<>();
    @Getter private static final String[] REGISTER_ALIASES = {"reg", "join", "add"};
    @Getter private static final String[] UNREGISTER_ALIASES = {"unreg", "remove", "rm"};
    @Getter private static final String[] OPTIONS_ALIASES = {"opt", "optn", "option"};

    public static boolean validSubcommand(String subcommand) { return SUBCOMMANDS.keySet().contains(subcommand); }

    public CommandHandler() {
        addCmd(new CommandHelp());
        addCmd(new CommandInfo());
        addCmd(new CommandOptions());
        addCmd(new CommandPrep());
        addCmd(new CommandRegister());
        addCmd(new CommandReset());
        addCmd(new CommandRules());
        addCmd(new CommandSetWorld());
        addCmd(new CommandStart());
        addCmd(new CommandStatus());
        addCmd(new CommandUnregister());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof CommandBlock && !Main.getInstance().getConfig().getBoolean("allow-command-blocks", false)) { return true; }
        if (args.length == 0) {
            UHCUtils.sendPluginInfo(sender);
            return true;
        }
        String subcommand = args[0];
        //check subcommand is valid
        if (!SUBCOMMANDS.keySet().contains(subcommand) && !Arrays.asList(REGISTER_ALIASES).contains(subcommand) && !Arrays.asList(UNREGISTER_ALIASES).contains(subcommand) && !Arrays.asList(OPTIONS_ALIASES).contains(subcommand)) {
            StringJoiner joiner = new StringJoiner(", ");
            SUBCOMMANDS.keySet().forEach(joiner::add);
            sender.sendMessage(ChatColor.RED + "Unknown subcommand " + ChatColor.WHITE + subcommand.toLowerCase() + ChatColor.RED + ". Valid subcommands: " + ChatColor.WHITE + joiner.toString());
            return true;
        }

        UHCCommand executor = SUBCOMMANDS.get(subcommand);
        //alias handling
        if (Arrays.asList(REGISTER_ALIASES).contains(subcommand)) {
            executor = SUBCOMMANDS.get("register");
        } else if (Arrays.asList(UNREGISTER_ALIASES).contains(subcommand)) {
            executor = SUBCOMMANDS.get("unregister");
        } else if (Arrays.asList(OPTIONS_ALIASES).contains(subcommand)) {
            executor = SUBCOMMANDS.get("options");
        }

        //No executor check
        if (executor == null) {
            Main.getInstance().getLogger().severe("Could not find subcommand executor for subcommand '" + subcommand + "'");
            return true;
        }

        //permission check
        boolean hasPerm = sender.hasPermission("uhc.admin");
        if (executor.isAdminOnly() && !hasPerm) {
            denyPermission(sender);
            return true;
        }

        executor.onCommand(sender, command, args[0], Arrays.copyOfRange(args, 1, args.length));

        return true;
    }

    private void addCmd(UHCCommand command) {
        SUBCOMMANDS.put(command.getName(), command);
    }

    private void denyPermission(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "You do not have permission to run this subcommand!");
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        boolean hasPerm = sender.hasPermission("uhc.admin");
        return SUBCOMMANDS.values().stream().filter(s -> hasPerm || !s.isAdminOnly()).map(UHCCommand::getName).filter(s -> s.startsWith(args[0])).map(String::toLowerCase).collect(Collectors.toList());
    }


}
