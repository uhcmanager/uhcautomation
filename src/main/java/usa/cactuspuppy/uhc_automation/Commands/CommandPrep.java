package usa.cactuspuppy.uhc_automation.Commands;

import lombok.NoArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.Tasks.GenerateChunksHelper;
import usa.cactuspuppy.uhc_automation.Tasks.PreGameCountdown;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class CommandPrep {
    private static final String[] OPTIONS = {"pause", "load", "stop"};

    public static void onCommand(CommandSender sender, String[] args) {
        if (Main.getInstance().getGameInstance().isActive()) {
            sender.sendMessage(ChatColor.RED + "Game is currently active, use /uhcstop to stop the game or wait until the current game is finished before attempt to prep the world.");
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
            (new GenerateChunksHelper()).schedule();
        } else if (args[0].equalsIgnoreCase("stop")) {
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
