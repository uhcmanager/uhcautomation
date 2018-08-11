package usa.cactuspuppy.uhc_automation.Commands;

import com.sun.org.apache.regexp.internal.RE;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@org.bukkit.plugin.java.annotation.command.Command(name = "uhc", desc = "Accesses the functionality of the UHC plugin", usage = "/uhc <subcommand> [args]")
public class CommandHandler implements CommandExecutor, TabCompleter {
    private static final String[] SUBCOMMANDS = {"help", "info", "options", "prep", "register", "reset", "rules", "setworld", "start", "status", "unregister"};
    private static final String[] REGISTER_ALIASES = {"reg", "join", "add"};
    private static final String[] UNREGISTER_ALIASES = {"unreg", "remove", "rm"};
    private static final String[] OPTIONS_ALIASES = {"opt", "optn", "option"};

    public BaseComponent[] buildHelpMsg(boolean hasPerm) {
        ComponentBuilder message = new ComponentBuilder("<").color(net.md_5.bungee.api.ChatColor.GOLD).append("--------------").color(net.md_5.bungee.api.ChatColor.WHITE).append("UHC_Automation Help").color(net.md_5.bungee.api.ChatColor.GOLD).append("--------------").color(net.md_5.bungee.api.ChatColor.WHITE).append(">\n").color(net.md_5.bungee.api.ChatColor.GOLD);
        BaseComponent[] infoHeader = new ComponentBuilder("Hovering over a command provides more info, clicking inserts the command into your chat.\n<...> denotes a required value, [...] is an optional value.\n").color(net.md_5.bungee.api.ChatColor.GRAY).create();
        BaseComponent[] listHeader = new ComponentBuilder("Commands:\n").color(net.md_5.bungee.api.ChatColor.YELLOW).create();

        BaseComponent FILLIN_Interact = new TextComponent("/uhc SUBCOMMAND\n");
        FILLIN_Interact.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        FILLIN_Interact.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("DESCRIPTION OF SUBCOMMAND\n").color(net.md_5.bungee.api.ChatColor.WHITE)
                .append("Requires uhc.admin: ").color(net.md_5.bungee.api.ChatColor.GOLD).append("YES/NO").color(net.md_5.bungee.api.ChatColor.CHANGEME).bold(true)
                .append("\nConsole: ").color(net.md_5.bungee.api.ChatColor.GOLD).bold(false).append("YES/NO").color(net.md_5.bungee.api.ChatColor.CHANGEME).bold(true).create()));
        FILLIN_Interact.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/uhc SUBCOMMAND\n"));
        BaseComponent[] FILLIN = new ComponentBuilder("- ").color(net.md_5.bungee.api.ChatColor.RED).append(FILLIN_Interact).append(" DESCRIPTION").color(net.md_5.bungee.api.ChatColor.GREEN).create();

        BaseComponent helpInteract = new TextComponent("/uhc help\n");
        helpInteract.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        helpInteract.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Displays this help page to the user who requested it.\nOnly displays command requester can access.\n").color(net.md_5.bungee.api.ChatColor.WHITE)
                .append("Requires uhc.admin: ").color(net.md_5.bungee.api.ChatColor.YELLOW).append("NO").color(net.md_5.bungee.api.ChatColor.RED).bold(true)
                .append("\nConsole: ").color(net.md_5.bungee.api.ChatColor.GOLD).bold(false).append("YES").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true).create()));
        helpInteract.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/uhc help"));
        BaseComponent[] help = new ComponentBuilder("- ").color(net.md_5.bungee.api.ChatColor.RED).append(helpInteract).append(" Shows this help page").color(net.md_5.bungee.api.ChatColor.GREEN).create();

        BaseComponent infoInteract = new TextComponent("/uhc info [Display Arguments - toggle:chat:scoreboard]\n");
        infoInteract.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        infoInteract.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Displays game information to the player, or changes\nhow that information is shown, if given a second argument.\n").color(net.md_5.bungee.api.ChatColor.WHITE)
                .append("Will not display information if the game is not active.\n").color(net.md_5.bungee.api.ChatColor.RED)
                .append("Arguments:\n").color(net.md_5.bungee.api.ChatColor.BLUE).underlined(true).bold(true)
                .append("toggle ").color(net.md_5.bungee.api.ChatColor.GOLD).underlined(false).bold(true).append("- ").color(net.md_5.bungee.api.ChatColor.DARK_GRAY).bold(false).append("Switches to other display mode")
                .append("Requires uhc.admin: ").color(net.md_5.bungee.api.ChatColor.GOLD).append("NO").color(net.md_5.bungee.api.ChatColor.RED).bold(true)
                .append("\nConsole: ").color(net.md_5.bungee.api.ChatColor.GOLD).bold(false).append("YES (display arguments not supported)").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true).create()));
        BaseComponent[] info = new ComponentBuilder("- ").color(net.md_5.bungee.api.ChatColor.RED).append(infoInteract).append(" DESCRIPTION").color(net.md_5.bungee.api.ChatColor.GREEN).create();


        if (!hasPerm) {
            return message.append(infoHeader).append(listHeader).append(help).append(info).create();
        }
        return message.append(infoHeader).append(listHeader).append(help).append(info).create();
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
        if (!Arrays.asList(SUBCOMMANDS).contains(subcommand) && !Arrays.asList(REGISTER_ALIASES).contains(subcommand) && !Arrays.asList(UNREGISTER_ALIASES).contains(subcommand) && !Arrays.asList(OPTIONS_ALIASES).contains(subcommand)) {
            StringJoiner joiner = new StringJoiner(", ");
            Arrays.stream(SUBCOMMANDS).forEach(joiner::add);
            sender.sendMessage(ChatColor.RED + "Unknown subcommand " + ChatColor.WHITE + subcommand.toLowerCase() + ChatColor.RED + ". Valid subcommands: " + ChatColor.WHITE + joiner.toString());
            return true;
        }


        //alias handling
        if (Arrays.asList(REGISTER_ALIASES).contains(subcommand)) {
            //TODO: register subcommand
        }
        if (Arrays.asList(UNREGISTER_ALIASES).contains(subcommand)) {
            //TODO: unregister subcommand
        }
        if (Arrays.asList(OPTIONS_ALIASES).contains(subcommand)) {
            //TODO: options subcommand
        }

        if (subcommand.equalsIgnoreCase("help")) {
            help(sender);
        }

        return true;
    }

    private void help(CommandSender sender) {
        if (sender instanceof CommandBlock && !Main.getInstance().getConfig().getBoolean("allow-command-blocks", false)) { return; }
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.GREEN + "<" + ChatColor.WHITE + "--------------" + ChatColor.GOLD + "UHC_Automation Help" + ChatColor.WHITE + "--------------" + ChatColor.GREEN + ">\n"
                    + ChatColor.GRAY + "<option> is a required option, [option] is an optional option\n"
                    + ChatColor.YELLOW + "Commands:\n"
                    + ChatColor.RED + "- " + ChatColor.GREEN + "Info: " + ChatColor.AQUA + "/uhc info [toggle:chat:scoreboard]\n"
                    + ChatColor.RED + "- " + ChatColor.GREEN + "Options: " + ChatColor.AQUA + "/uhc options <option> <value>\n"
                    + ChatColor.RED + "- " + ChatColor.GREEN + "Prep: " + ChatColor.AQUA + "/uhc prep\n"
                    + ChatColor.RED + "- " + ChatColor.GREEN + "Player Registration: " + ChatColor.AQUA + "/uhc register <player>\n"
                    + ChatColor.RED + "- " + ChatColor.GREEN + "Reset/Stop Game: " + ChatColor.AQUA + "/uhc reset\n"
                    + ChatColor.RED + "- " + ChatColor.GREEN + "Rules: " + ChatColor.AQUA + "/uhc rules\n"
                    + ChatColor.RED + "- " + ChatColor.GREEN + "Set Game World: " + ChatColor.AQUA + "/uhc setworld [world name]\n"
                    + ChatColor.RED + "- " + ChatColor.GREEN + "Start Game: " + ChatColor.AQUA + "/uhc start [secs to countdown]\n"
                    + ChatColor.RED + "- " + ChatColor.GREEN + "Status: " + ChatColor.AQUA + "/uhc status");
            return;
        }
        sender.spigot().sendMessage(buildHelpMsg(sender.hasPermission("uhc.admin")));
    }

    public static boolean validSubcommand(String subcommand) {
        return Arrays.asList(SUBCOMMANDS).contains(subcommand);
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Arrays.stream(SUBCOMMANDS).filter(s -> s.startsWith(args[0])).map(String::toLowerCase).collect(Collectors.toList());
    }
}
