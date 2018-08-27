package usa.cactuspuppy.uhc_automation.Commands;

import lombok.Getter;
import net.md_5.bungee.api.chat.*;
import org.bukkit.ChatColor;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@org.bukkit.plugin.java.annotation.command.Command(name = "uhc", desc = "Accesses the functionality of the UHC plugin", usage = "/uhc <subcommand> [args]")
public class CommandHandler implements CommandExecutor, TabCompleter {
    @Getter private static final String[] SUBCOMMANDS = {"help", "info", "options", "prep", "register", "reset", "rules", "setworld", "start", "status", "stop", "team", "unregister"};
    @Getter private static final String[] REGISTER_ALIASES = {"reg", "join", "add"};
    @Getter private static final String[] UNREGISTER_ALIASES = {"unreg", "remove", "rm"};
    @Getter private static final String[] OPTIONS_ALIASES = {"opt", "optn", "option"};

    public static boolean validSubcommand(String subcommand) { return Arrays.asList(SUBCOMMANDS).contains(subcommand); }

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

        //permission check
        boolean hasPerm = sender.hasPermission("uhc.admin");

        //alias handling
        if (Arrays.asList(REGISTER_ALIASES).contains(subcommand)) {
            if (!hasPerm) denyPermission(sender);
            CommandRegister.onCommand(sender, subcommand, Arrays.copyOfRange(args, 1, args.length));
        }
        if (Arrays.asList(UNREGISTER_ALIASES).contains(subcommand)) {
            if (!hasPerm) denyPermission(sender);
            CommandUnregister.onCommand(sender, subcommand, Arrays.copyOfRange(args, 1, args.length));
        }
        if (Arrays.asList(OPTIONS_ALIASES).contains(subcommand)) {
            if (!hasPerm) denyPermission(sender);
            CommandOptions.onCommand(sender, subcommand, Arrays.copyOfRange(args, 1, args.length));
        }

        if (subcommand.equalsIgnoreCase("help")) {
            help(sender);
        } else if (subcommand.equalsIgnoreCase("info")) {
            CommandInfo.onCommand(sender, Arrays.copyOfRange(args, 1, args.length));
        } else if (subcommand.equalsIgnoreCase("options")) {
            if (!hasPerm) denyPermission(sender);
            CommandOptions.onCommand(sender, subcommand, Arrays.copyOfRange(args, 1, args.length));
        } else if (subcommand.equalsIgnoreCase("prep")) {
            if (!hasPerm) denyPermission(sender);
            CommandPrep.onCommand(sender, Arrays.copyOfRange(args, 1, args.length));
        } else if (subcommand.equalsIgnoreCase("register")) {
            if (!hasPerm) denyPermission(sender);
            CommandRegister.onCommand(sender, subcommand, Arrays.copyOfRange(args, 1, args.length));
        } else if (subcommand.equals("reset") || subcommand.equalsIgnoreCase("stop")) {
            if (!hasPerm) denyPermission(sender);
            CommandReset.onCommand(sender);
        } else if (subcommand.equals("rules")) {
            CommandRules.onCommand(sender);
        } else if (subcommand.equals("setworld")) {
            if (!hasPerm) denyPermission(sender);
            CommandSetWorld.onCommand(sender, Arrays.copyOfRange(args, 1, args.length));
        } else if (subcommand.equals("start")) {
            if (!hasPerm) denyPermission(sender);
            CommandStart.onCommand(sender, Arrays.copyOfRange(args, 1, args.length));
        } else if (subcommand.equals("status")) {
            if (!hasPerm) denyPermission(sender);
            CommandStatus.onCommand(sender);
        } else if (subcommand.equals("team")) {
            if (!hasPerm) denyPermission(sender);
            CommandTeam.onCommand(sender, Arrays.copyOfRange(args, 1, args.length));
        } else if (subcommand.equalsIgnoreCase("unregister")) {
            if (!hasPerm) denyPermission(sender);
            CommandUnregister.onCommand(sender, subcommand, Arrays.copyOfRange(args, 1, args.length));
        }

        return true;
    }

    private void help(CommandSender sender) {
        sender.spigot().sendMessage(buildHelpMsg(sender.hasPermission("uhc.admin")));
    }

    private void denyPermission(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "You do not have permission to run this subcommand!");
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Arrays.stream(SUBCOMMANDS).filter(s -> s.startsWith(args[0])).map(String::toLowerCase).collect(Collectors.toList());
    }

    public BaseComponent[] buildHelpMsg(boolean hasPerm) {
        ComponentBuilder message = new ComponentBuilder("<").color(net.md_5.bungee.api.ChatColor.GOLD).append("--------------").color(net.md_5.bungee.api.ChatColor.WHITE).append("UHC_Automation Help").color(net.md_5.bungee.api.ChatColor.GOLD).append("--------------").color(net.md_5.bungee.api.ChatColor.WHITE).append(">\n").color(net.md_5.bungee.api.ChatColor.GOLD);
        BaseComponent[] infoHeader = new ComponentBuilder("Hovering over a command provides more info, clicking inserts the command into your chat.\nAfter inserting a command into chat, pressing TAB will bring up a list of acceptable values, if possible.\n<...> denotes a required value, [...] is an optional value.\n").color(net.md_5.bungee.api.ChatColor.GRAY).create();
        BaseComponent[] listHeader = new ComponentBuilder("Commands:\n").color(net.md_5.bungee.api.ChatColor.YELLOW).create();

        //Template
        /*BaseComponent FILLIN_Interact = new TextComponent("/uhc SUBCOMMAND");
        FILLIN_Interact.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        FILLIN_Interact.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("DESCRIPTION OF SUBCOMMAND\n").color(net.md_5.bungee.api.ChatColor.WHITE)
                .append("Requires uhc.admin: ").color(net.md_5.bungee.api.ChatColor.GOLD).append("YES/NO").color(net.md_5.bungee.api.ChatColor.CHANGEME).bold(true)
                .append("\nConsole: ").color(net.md_5.bungee.api.ChatColor.GOLD).bold(false).append("YES/NO").color(net.md_5.bungee.api.ChatColor.CHANGEME).bold(true).create()));
        FILLIN_Interact.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/uhc SUBCOMMAND\n"));
        BaseComponent[] FILLIN = new ComponentBuilder("- ").color(net.md_5.bungee.api.ChatColor.RED).append(FILLIN_Interact).retain(ComponentBuilder.FormatRetention.NONE).append(" DESCRIPTION\n").color(net.md_5.bungee.api.ChatColor.GREEN).create();*/

        BaseComponent helpInteract = new TextComponent("/uhc help");
        helpInteract.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        helpInteract.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Displays this help page to the user who requested it.\nOnly displays command requester can access.\n").color(net.md_5.bungee.api.ChatColor.WHITE)
                .append("Requires uhc.admin: ").color(net.md_5.bungee.api.ChatColor.YELLOW).append("NO").color(net.md_5.bungee.api.ChatColor.RED).bold(true)
                .append("\nConsole: ").color(net.md_5.bungee.api.ChatColor.GOLD).bold(false).append("YES (hover and click events not supported)").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true).create()));
        helpInteract.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/uhc help"));
        BaseComponent[] help = new ComponentBuilder("- ").color(net.md_5.bungee.api.ChatColor.RED).append(helpInteract).append(" Shows this help page\n").retain(ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.GREEN).create();

        BaseComponent infoInteract = new TextComponent("/uhc info [toggle:chat:scoreboard]");
        infoInteract.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        infoInteract.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Displays game information to the player, or changes\nhow that information is shown, if given an argument.\n").color(net.md_5.bungee.api.ChatColor.WHITE)
                .append("Will not display information if the game is not active.\n").color(net.md_5.bungee.api.ChatColor.RED)
                .append("Arguments:\n").color(net.md_5.bungee.api.ChatColor.BLUE).underlined(true).bold(true)
                .append("toggle").color(net.md_5.bungee.api.ChatColor.GOLD).underlined(false).bold(true).append(" - ").color(net.md_5.bungee.api.ChatColor.DARK_GRAY).bold(false).append("Switches to the display mode that is not currently active.\n").color(net.md_5.bungee.api.ChatColor.WHITE)
                .append("chat ").color(net.md_5.bungee.api.ChatColor.GOLD).bold(true).append(" - ").color(net.md_5.bungee.api.ChatColor.DARK_GRAY).bold(false).append("The chat display prints current game information upon request.\n").color(net.md_5.bungee.api.ChatColor.WHITE)
                .append("scoreboard ").color(net.md_5.bungee.api.ChatColor.GOLD).bold(true).append(" - ").color(net.md_5.bungee.api.ChatColor.DARK_GRAY).bold(false).append("The scoreboard display shows current game information in the side scoreboard.\n").color(net.md_5.bungee.api.ChatColor.WHITE)
                .append("Requires uhc.admin: ").color(net.md_5.bungee.api.ChatColor.GOLD).append("NO").color(net.md_5.bungee.api.ChatColor.RED).bold(true)
                .append("\nConsole: ").color(net.md_5.bungee.api.ChatColor.GOLD).bold(false).append("Base command allowed, arguments not supported").color(net.md_5.bungee.api.ChatColor.RED).bold(true).create()));
        infoInteract.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/uhc info "));
        BaseComponent[] info = new ComponentBuilder("- ").color(net.md_5.bungee.api.ChatColor.RED).append(infoInteract).append(" Displays or requests game information\n").retain(ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.GREEN).create();

        BaseComponent optionInteract = new TextComponent("/uhc options <option> <value>");
        StringJoiner optionAliasJoiner = new StringJoiner(", ");
        Arrays.stream(OPTIONS_ALIASES).forEach(optionAliasJoiner::add);
        optionInteract.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        optionInteract.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Sets specified game option to the specified value.\nValues are validated before being applied.\nUse tab complete (or check the wiki) for a full list of options\n").color(net.md_5.bungee.api.ChatColor.WHITE)
                .append("Requires uhc.admin: ").color(net.md_5.bungee.api.ChatColor.GOLD).append("YES").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true)
                .append("\nConsole: ").color(net.md_5.bungee.api.ChatColor.GOLD).bold(false).append("YES").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true)
                .append("\nAliases: ").color(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE).bold(false).append(optionAliasJoiner.toString()).color(net.md_5.bungee.api.ChatColor.WHITE).create()));
        optionInteract.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/uhc options "));
        BaseComponent[] option = new ComponentBuilder("- ").color(net.md_5.bungee.api.ChatColor.RED).append(optionInteract).append(" Modifies game options\n").retain(ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.GREEN).create();

        BaseComponent prepInteract = new TextComponent("/uhc prep [load:pause:stop]");
        prepInteract.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        prepInteract.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Prepares the game world for the game. If no second argument\nis specified, defaults to noload.\n").color(net.md_5.bungee.api.ChatColor.WHITE)
                .append("Arguments:\n").color(net.md_5.bungee.api.ChatColor.BLUE).underlined(true).bold(true)
                .append("load").color(net.md_5.bungee.api.ChatColor.GOLD).bold(true).append(" - ").color(net.md_5.bungee.api.ChatColor.DARK_GRAY).bold(false).append("Pre-generates all chunks which have not been loaded within the game area, or resumes said pre-generation.\n").color(net.md_5.bungee.api.ChatColor.WHITE)
                .append("pause").color(net.md_5.bungee.api.ChatColor.GOLD).underlined(false).bold(true).append(" - ").color(net.md_5.bungee.api.ChatColor.DARK_GRAY).bold(false).append("Pauses chunk pre-generation, saving progress for future resumption.\n").color(net.md_5.bungee.api.ChatColor.WHITE)
                .append("Requires uhc.admin: ").color(net.md_5.bungee.api.ChatColor.GOLD).append("YES").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true)
                .append("\nConsole: ").color(net.md_5.bungee.api.ChatColor.GOLD).bold(false).append("YES").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true).create()));
        prepInteract.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/uhc prep "));
        BaseComponent[] prep = new ComponentBuilder("- ").color(net.md_5.bungee.api.ChatColor.RED).append(prepInteract).append(" Prepares the game world\n").retain(ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.GREEN).create();

        BaseComponent registerInteract = new TextComponent("/uhc register <player>");
        StringJoiner registerAliasJoiner = new StringJoiner(", ");
        Arrays.stream(REGISTER_ALIASES).forEach(registerAliasJoiner::add);
        registerInteract.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        registerInteract.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Forcibly adds an online player to the game.\n").color(net.md_5.bungee.api.ChatColor.WHITE)
                .append("Requires uhc.admin: ").color(net.md_5.bungee.api.ChatColor.GOLD).append("YES").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true)
                .append("\nConsole: ").color(net.md_5.bungee.api.ChatColor.GOLD).bold(false).append("YES").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true)
                .append("\nAliases: ").color(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE).bold(false).append(registerAliasJoiner.toString()).color(net.md_5.bungee.api.ChatColor.WHITE).create()));
        registerInteract.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/uhc register "));
        BaseComponent[] register = new ComponentBuilder("- ").color(net.md_5.bungee.api.ChatColor.RED).append(registerInteract).append(" Adds player to the game.\n").retain(ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.GREEN).create();

        BaseComponent resetInteract = new TextComponent("/uhc reset");
        resetInteract.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        resetInteract.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Stops the game, if active, and resets the game to lobby state.\n").color(net.md_5.bungee.api.ChatColor.WHITE)
                .append("Requires uhc.admin: ").color(net.md_5.bungee.api.ChatColor.GOLD).append("YES").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true)
                .append("\nConsole: ").color(net.md_5.bungee.api.ChatColor.GOLD).bold(false).append("YES").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true).create()));
        resetInteract.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/uhc reset"));
        BaseComponent[] reset = new ComponentBuilder("- ").color(net.md_5.bungee.api.ChatColor.RED).append(resetInteract).append(" Resets game to lobby state.\n").retain(ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.GREEN).create();

        BaseComponent rulesInteract = new TextComponent("/uhc rules");
        rulesInteract.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        rulesInteract.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Displays the game rules to requester\n").color(net.md_5.bungee.api.ChatColor.WHITE)
                .append("Requires uhc.admin: ").color(net.md_5.bungee.api.ChatColor.GOLD).append("NO").color(net.md_5.bungee.api.ChatColor.RED).bold(true)
                .append("\nConsole: ").color(net.md_5.bungee.api.ChatColor.GOLD).bold(false).append("YES").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true).create()));
        rulesInteract.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/uhc rules"));
        BaseComponent[] rules = new ComponentBuilder("- ").color(net.md_5.bungee.api.ChatColor.RED).append(rulesInteract).append(" Displays game rules\n").retain(ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.GREEN).create();

        BaseComponent setworldInteract = new TextComponent("/uhc setworld [world name]");
        setworldInteract.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        setworldInteract.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Sets the game world to the specified world, or the world the executor is in, if no world is specified.\n").color(net.md_5.bungee.api.ChatColor.WHITE)
                .append("Plugin will not accept any worlds with nether or end\nenvironments, to avoid UHCs accidentally being created in those worlds\n").color(net.md_5.bungee.api.ChatColor.RED)
                .append("Requires uhc.admin: ").color(net.md_5.bungee.api.ChatColor.GOLD).append("YES").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true)
                .append("\nConsole: ").color(net.md_5.bungee.api.ChatColor.GOLD).bold(false).append("YES (world name must be specified)").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true).create()));
        setworldInteract.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/uhc setworld "));
        BaseComponent[] setworld = new ComponentBuilder("- ").color(net.md_5.bungee.api.ChatColor.RED).append(setworldInteract).append(" Sets the game world\n").retain(ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.GREEN).create();

        BaseComponent startInteract = new TextComponent("/uhc start [secs]");
        startInteract.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        startInteract.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Starts the game, optionally counting down the specified\nnumber of seconds before doing so\n").color(net.md_5.bungee.api.ChatColor.WHITE)
                .append("While a starting countdown is active, running this command again\nwithout a countdown length specified will start the game instantly.\nIf a countdown length is specified, the countdown\nwill be reset to the specified length.\n").color(net.md_5.bungee.api.ChatColor.DARK_GRAY)
                .append("Requires uhc.admin: ").color(net.md_5.bungee.api.ChatColor.GOLD).append("YES").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true)
                .append("\nConsole: ").color(net.md_5.bungee.api.ChatColor.GOLD).bold(false).append("YES").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true).create()));
        startInteract.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/uhc start\n"));
        BaseComponent[] start = new ComponentBuilder("- ").color(net.md_5.bungee.api.ChatColor.RED).append(startInteract).append(" Starts the game\n").retain(ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.GREEN).create();

        BaseComponent statusInteract = new TextComponent("/uhc status");
        statusInteract.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        statusInteract.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Informs requester of game settings and current status.\n").color(net.md_5.bungee.api.ChatColor.WHITE)
                .append("Requires uhc.admin: ").color(net.md_5.bungee.api.ChatColor.GOLD).append("YES").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true)
                .append("\nConsole: ").color(net.md_5.bungee.api.ChatColor.GOLD).bold(false).append("YES").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true).create()));
        statusInteract.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/uhc start"));
        BaseComponent[] status = new ComponentBuilder("- ").color(net.md_5.bungee.api.ChatColor.RED).append(statusInteract).append(" Informs requester of game status\n").retain(ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.GREEN).create();

        BaseComponent teamInteract = new TextComponent("/uhc team <subcommand> <team name> [args]");
        teamInteract.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        teamInteract.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("DESCRIPTION OF SUBCOMMAND\n").color(net.md_5.bungee.api.ChatColor.WHITE)
                .append("Subcommands:\n").color(net.md_5.bungee.api.ChatColor.BLUE).underlined(true).bold(true)
                .append("add").color(net.md_5.bungee.api.ChatColor.GOLD).bold(true).append(" - ").color(net.md_5.bungee.api.ChatColor.DARK_GRAY).bold(false).append("Creates a team with the specified name\n").color(net.md_5.bungee.api.ChatColor.WHITE)
                .append("remove").color(net.md_5.bungee.api.ChatColor.GOLD).underlined(false).bold(true).append(" - ").color(net.md_5.bungee.api.ChatColor.DARK_GRAY).bold(false).append("Removes the specified team, if it exists\n").color(net.md_5.bungee.api.ChatColor.WHITE)
                .append("join").color(net.md_5.bungee.api.ChatColor.GOLD).underlined(false).bold(true).append(" - ").color(net.md_5.bungee.api.ChatColor.DARK_GRAY).bold(false).append("Adds the specified player to the specified team\n").color(net.md_5.bungee.api.ChatColor.WHITE)
                .append("leave").color(net.md_5.bungee.api.ChatColor.GOLD).underlined(false).bold(true).append(" - ").color(net.md_5.bungee.api.ChatColor.DARK_GRAY).bold(false).append("Removes the specified player from the specified team\n").color(net.md_5.bungee.api.ChatColor.WHITE)
                .append("option").color(net.md_5.bungee.api.ChatColor.GOLD).underlined(false).bold(true).append(" - ").color(net.md_5.bungee.api.ChatColor.DARK_GRAY).bold(false).append("Changes the options for the specified team (i.e. color, collision, etc.)\n").color(net.md_5.bungee.api.ChatColor.WHITE)
                .append("Requires uhc.admin: ").color(net.md_5.bungee.api.ChatColor.GOLD).append("YES").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true)
                .append("\nConsole: ").color(net.md_5.bungee.api.ChatColor.GOLD).bold(false).append("YES").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true).create()));
        teamInteract.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/uhc team \n"));
        BaseComponent[] team = new ComponentBuilder("- ").color(net.md_5.bungee.api.ChatColor.RED).append(teamInteract).retain(ComponentBuilder.FormatRetention.NONE).append(" DESCRIPTION\n").color(net.md_5.bungee.api.ChatColor.GREEN).create();

        BaseComponent unregisterInteract = new TextComponent("/uhc unregister <player>");
        StringJoiner unregsiterAliasJoiner = new StringJoiner(", ");
        Arrays.stream(UNREGISTER_ALIASES).forEach(unregsiterAliasJoiner::add);
        unregisterInteract.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        unregisterInteract.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Blacklists player from participation in the game. The player\nwill still be able to spectate the game.\n").color(net.md_5.bungee.api.ChatColor.WHITE)
                .append("Requires uhc.admin: ").color(net.md_5.bungee.api.ChatColor.GOLD).append("YES").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true)
                .append("\nConsole: ").color(net.md_5.bungee.api.ChatColor.GOLD).bold(false).append("YES").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true)
                .append("\nAliases: ").color(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE).bold(false).append(unregsiterAliasJoiner.toString()).color(net.md_5.bungee.api.ChatColor.WHITE).create()));
        unregisterInteract.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/uhc unregister"));
        BaseComponent[] unregister = new ComponentBuilder("- ").color(net.md_5.bungee.api.ChatColor.RED).append(unregisterInteract).append(" Blacklists player from the game.\n").retain(ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.GREEN).create();

        BaseComponent wikiInteract = new TextComponent("here");
        wikiInteract.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        wikiInteract.setBold(true);
        wikiInteract.setUnderlined(true);
        wikiInteract.setItalic(true);
        wikiInteract.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click me to visit the wiki!").color(net.md_5.bungee.api.ChatColor.WHITE).bold(true).create()));
        wikiInteract.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/cactuspuppy/uhcautomation/wiki"));
        BaseComponent[] wiki = new ComponentBuilder("\nAlso be sure to check out the wiki ").color(net.md_5.bungee.api.ChatColor.WHITE).append(wikiInteract).append(" or go to https://github.com/cactuspuppy/uhcautomation/wiki for full information!").retain(ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.WHITE).bold(false).italic(false).underlined(false).create();

        if (!hasPerm) {
            return message.append(infoHeader).append(listHeader).append(help).append(info).append(rules).create();
        }
        return message.append(infoHeader).append(listHeader).append(help).append(info).append(option).append(prep).append(register).append(reset).append(rules).append(setworld).append(start).append(status).append(team).append(unregister).append(wiki).create();
    }
}
