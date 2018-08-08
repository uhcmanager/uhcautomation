package usa.cactuspuppy.uhc_automation.Commands;

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
        BaseComponent[] infoHeader = new ComponentBuilder("Hovering over a command provides more info, clicking inserts the command into your chat.\n<option> denotes a required value, [option] is an optional value.\n").color(net.md_5.bungee.api.ChatColor.GRAY).create();
        BaseComponent[] listHeader = new ComponentBuilder("Commands:\n").color(net.md_5.bungee.api.ChatColor.YELLOW).create();

        BaseComponent helpInteract = new TextComponent("/uhc help\n");
        helpInteract.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        helpInteract.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Shows this help page").color(net.md_5.bungee.api.ChatColor.GREEN).create()));
        helpInteract.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/uhc help"));
        BaseComponent[] help = new ComponentBuilder("- ").color(net.md_5.bungee.api.ChatColor.RED).append("Info: ").color(net.md_5.bungee.api.ChatColor.GREEN).append(helpInteract).create();

        BaseComponent infoInteract = new TextComponent("/uhc info [toggle:chat:scoreboard]\n");
        infoInteract.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        infoInteract.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Displays information to the player or modifies how that information is shown").color(net.md_5.bungee.api.ChatColor.GREEN).create()));
        infoInteract.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/uhc info "));
        BaseComponent[] info = new ComponentBuilder("- ").color(net.md_5.bungee.api.ChatColor.RED).append("Info: ").color(net.md_5.bungee.api.ChatColor.GREEN).append(infoInteract).create();

        BaseComponent optionInteract = new TextComponent("/uhc options <option> <value>\n");
        optionInteract.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        optionInteract.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Modifies game options such as world size, whether natural regeneration is on or not, what the game name is, and so on. Full list can be found via tab complete (just click me and press tab)").color(net.md_5.bungee.api.ChatColor.GREEN).create()));
        optionInteract.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/uhc options "));
        BaseComponent[] option = new ComponentBuilder("- ").color(net.md_5.bungee.api.ChatColor.RED).append("Options: ").color(net.md_5.bungee.api.ChatColor.GREEN).append(optionInteract).create();

        BaseComponent prepInteract = new TextComponent("/uhc prep [nogen:gen]\n");
        prepInteract.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        prepInteract.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Re-prepares the game world for the game.\n\n").color(net.md_5.bungee.api.ChatColor.GREEN).append("nogen").color(net.md_5.bungee.api.ChatColor.AQUA).append(" - ").color(net.md_5.bungee.api.ChatColor.GRAY).append("No attempt any world generation\n").color(net.md_5.bungee.api.ChatColor.WHITE).append("gen").color(net.md_5.bungee.api.ChatColor.GREEN).append(" - ").color(net.md_5.bungee.api.ChatColor.GRAY).append("Generates any chunks within the initial play area that haven not yet been generated.").color(net.md_5.bungee.api.ChatColor.WHITE).create()));
        prepInteract.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/uhc prep "));
        BaseComponent[] prep = new ComponentBuilder("- ").color(net.md_5.bungee.api.ChatColor.RED).append("Prep: ").color(net.md_5.bungee.api.ChatColor.GREEN).append(prepInteract).create();

        BaseComponent registerInteract = new TextComponent("/uhc reg\n");
        prepInteract.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        prepInteract.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Adds a player to the game, even if said player has been blacklisted.\n").color(net.md_5.bungee.api.ChatColor.GREEN).append("T must be online to use this command").create()));
        prepInteract.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/uhc register "));
        BaseComponent[] register = new ComponentBuilder("- ").color(net.md_5.bungee.api.ChatColor.RED).append("Register: ").color(net.md_5.bungee.api.ChatColor.GREEN).append(registerInteract).create();
        if (hasPerm) {
            return message.append(infoHeader).append(listHeader).append(help).append(info).append(option).append(prep).create();
        }
        return message.append(infoHeader).append(listHeader).append(help).append(info).append(option).append(prep).create();
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
