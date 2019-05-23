package usa.cactuspuppy.uhc_automation.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.task.GenerateChunksHelper;
import usa.cactuspuppy.uhc_automation.task.PreGameCountdown;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandPrep extends UHCCommand {
    private static final String[] OPTIONS = {"pause", "load", "reset"};

    public CommandPrep() {
        name = "prep";
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (Main.getInstance().getGameInstance().isActive()) {
            sender.sendMessage(ChatColor.RED + "Game is currently active, use " + ChatColor.WHITE + "/uhc reset" + ChatColor.RED + " to stop the game or wait until the current game is finished before attempt to prep the world.");
            return;
        }
        if (args.length == 0) {
            Main.getInstance().getGameInstance().prep();
            sender.sendMessage(ChatColor.GREEN + "Successfully prepared game world for game!");
            return;
        }
        if (!Arrays.asList(OPTIONS).contains(args[0])) {
            sender.sendMessage(ChatColor.RED + "Unrecognized argument " + ChatColor.RESET + args[0] + ".\nValid arguments: " + ChatColor.RESET + "pause, load, stop");
            return;
        }
        if (args[0].equalsIgnoreCase("load")) {
            if (GenerateChunksHelper.getInstance() != null) {
                sender.sendMessage(ChatColor.RED + "Chunk pre-generation is already in progress! Use " + ChatColor.RESET + "/uhc prep cancel" + ChatColor.RED + " to stop it.");
                return;
            } else if (PreGameCountdown.getInstance() != null || Main.getInstance().getGameInstance().isActive()) {
                sender.sendMessage(ChatColor.RED + "Game is starting or has started! Halt the game first with " + ChatColor.RESET + "/uhc reset" + ChatColor.RED + " or wait for the game to complete before pre-generating chunks!");
                return;
            }
            (new GenerateChunksHelper()).start();
        } else if (args[0].equalsIgnoreCase("reset")) {
            if (GenerateChunksHelper.getInstance() == null) {
                sender.sendMessage(ChatColor.RED + "No chunk generation is currently in progress.");
                return;
            }
            GenerateChunksHelper.stop();
            sender.sendMessage(ChatColor.GREEN + "Successfully halted chunk pre-generation.");
        } else if (args[0].equalsIgnoreCase("pause")) {
            if (GenerateChunksHelper.getInstance() == null) {
                sender.sendMessage(ChatColor.RED + "No chunk generation is currently in progress.");
                return;
            }
            GenerateChunksHelper.pause();
        }
        Main.getInstance().getGameInstance().prep();
    }

    public static List<String> onTabComplete(String[] args) {
        return Arrays.stream(OPTIONS).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
    }
}
