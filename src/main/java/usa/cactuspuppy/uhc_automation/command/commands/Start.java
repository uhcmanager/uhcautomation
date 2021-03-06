package usa.cactuspuppy.uhc_automation.command.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.GameManager;
import usa.cactuspuppy.uhc_automation.game.GameState;
import usa.cactuspuppy.uhc_automation.game.GameStateEvent;
import usa.cactuspuppy.uhc_automation.game.tasks.timers.UHC_InitCountdown;
import usa.cactuspuppy.uhc_automation.utils.Logger;
import usa.cactuspuppy.uhc_automation.utils.MiscUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Start implements UHCCommand {
    private static Map<UUID, UHC_InitCountdown> starters = new HashMap<>();
    private static Pattern argsParser = Pattern.compile("^(\\d+)(?: .*)?");

    public static void startComplete(GameInstance instance) {
        starters.remove(instance.getGameID());
    }

    @Override
    public String getUsage() {
        return "/uhc start [secs] [name/ID]";
    }

    @Override
    public String getPurpose() {
        return "Start or count down to start of game";
    }

    @Override
    public String getMoreInfo() {
        return "Initiate a game, optionally with a countdown beforehand of n seconds. Will only work if the game is in lobby mode.";
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length > 1) {
            String[] nameArr = Arrays.copyOfRange(args, 1, args.length);
            String name = String.join(" ", nameArr);
            return GameManager.getActiveGames().values().stream().map(GameInstance::getName).distinct().filter(n -> n.startsWith(name)).sorted().collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String alias, String[] args) {
        //Extract seconds, if there are any
        String seconds = "";
        String test = StringUtils.join(args, " ");
        Matcher matcher = argsParser.matcher(test);
        if (matcher.matches()) {
            Logger.logFineMsg(this.getClass(), "Found seconds in args", 0);
            seconds = matcher.group(1);
            args = Arrays.copyOfRange(args, 1, args.length);
        }
        //Get correct game instance
        MiscUtils.GetInstanceResult result = MiscUtils.getGameInstance(commandSender, args);
        if (!result.isUsageCorrect()) {
            return false;
        }
        GameInstance instance = result.getInstance();
        if (instance == null) { //Handle graceful failure
            return true;
        }
        //Check that we can actually start the game
        if (!instance.getGameState().equals(GameState.LOBBY)) {
            commandSender.sendMessage(ChatColor.RED + "Game is starting or in progress");
            return true;
        }
        if (!seconds.equals("")) {
            if (starters.containsKey(instance.getGameID())) {
                starters.get(instance.getGameID()).cancel();
                starters.remove(instance.getGameID());
            }
            try {
                int delay = Integer.valueOf(seconds);
                UHC_InitCountdown task = new UHC_InitCountdown(instance, delay);
                starters.put(instance.getGameID(), task);
                task.init();
                Logger.logFineMsg(this.getClass(), "Started init countdown", 0);
                return true;
            } catch (NumberFormatException e) {
                commandSender.sendMessage(ChatColor.RED + "Problem parsing for integer here: " + args[0] +
                        "\nIssue: " + e.getCause());
                return true;
            }
        } else {
            instance.updateState(GameStateEvent.INIT);
        }
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender, String alias, String[] args) {
        return commandSender.hasPermission("uhc.manager");
    }
}
