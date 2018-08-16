package usa.cactuspuppy.uhc_automation.Commands;

import lombok.NoArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.Tasks.GenerateChunksHelper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class CommandPrep {
    private static String[] OPTIONS = {"noload", "load", "cancel"};

    public static void onCommand(CommandSender sender, String[] args) {
        if (Main.getInstance().getGameInstance().isActive()) {
            sender.sendMessage(ChatColor.RED + "Game is currently active, use /uhcstop to stop the game or wait until the current game is finished before attempt to prep the world.");
        }
        if (args.length == 0) {
            Main.getInstance().getGameInstance().prep();
            return;
        }
        if (args[0].equalsIgnoreCase("load")) {
            if (GenerateChunksHelper.getInstance() != null) {
                sender.sendMessage(ChatColor.RED + "Chunk pre-generation is already in progress! Use " + ChatColor.RESET + "/uhc prep cancel");
                return;
            }
            (new GenerateChunksHelper()).schedule();
        } else if (args[0].equalsIgnoreCase("cancel")) {
            if (GenerateChunksHelper.getInstance() == null) {
                sender.sendMessage(ChatColor.RED + "No chunk generation is currently in progress.");
                return;
            }
        } else if (!args[0].equalsIgnoreCase("noload")) {
            sender.sendMessage(ChatColor.RED + "Unrecognized argument " + ChatColor.RESET + args[0] + ".\nValid arguments: " + ChatColor.RESET + "noload, load, cancel");
        }
        Main.getInstance().getGameInstance().prep();
    }

    public static List<String> onTabComplete(String[] args) {
        return Arrays.stream(OPTIONS).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
    }
}
