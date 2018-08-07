package usa.cactuspuppy.uhc_automation.Commands;

import org.bukkit.ChatColor;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

import java.util.Arrays;
import java.util.StringJoiner;

@org.bukkit.plugin.java.annotation.command.Command(name = "uhc", desc = "Accesses the functionality of the UHC plugin", usage = "/uhc <subcommand> [args]")
public class CommandHandler implements CommandExecutor {
    private static final String[] SUBCOMMANDS = {"help", "info", "options", "prep", "register", "reset", "rules", "setworld", "start", "status", "unregister"};
    private static final String[] REGISTER_ALIASES = {"reg", "join", "add"};
    private static final String[] UNREGISTER_ALIASES = {"unreg", "remove", "rm"};
    private static final String[] OPTIONS_ALIASES = {"opt", "optn", "option"};

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof CommandBlock && !Main.getInstance().getConfig().getBoolean("allow-command-blocks", false)) { return true; }
        if (args.length == 0) {
            UHCUtils.sendPluginInfo(sender);
            return true;
        }
        String subcommand = args[0];
        //check subcommand is valid
        if (!Arrays.asList(SUBCOMMANDS).contains(subcommand) && !Arrays.asList(REGISTER_ALIASES).contains(subcommand) && !Arrays.asList(UNREGISTER_ALIASES).contains(subcommand) && !Arrays.asList(OPTIONS_ALIASES).contains(subcommand)) {
            StringJoiner joiner = new StringJoiner(", ");
            Arrays.stream(SUBCOMMANDS).forEach(joiner::add);
            sender.sendMessage(ChatColor.RED + "Unknown subcommand " + ChatColor.WHITE + subcommand.toLowerCase() + ChatColor.RED + ". Valid subcommands: " + ChatColor.WHITE + joiner.toString());
            return true;
        }


        //alias handling
        if (Arrays.asList(REGISTER_ALIASES).contains(subcommand)) {
            //TODO: register subcommand
//            (new CommandRegister()).onCommand(sender, command, args[0], Arrays.copyOfRange(args, 1, args.length));
        }
        if (Arrays.asList(UNREGISTER_ALIASES).contains(subcommand)) {
            //TODO: unregister subcommand
//            (new CommandRegister()).onCommand(sender, command, args[0], Arrays.copyOfRange(args, 1, args.length));
        }
        if (Arrays.asList(OPTIONS_ALIASES).contains(subcommand)) {
            //TODO: options subcommand
        }

        if (subcommand.equalsIgnoreCase("help")) {
            help(sender));
        }

        return true;
    }

    private void help(CommandSender sender) {
        UHCUtils.sendHelpMessage(sender);
    }
}
