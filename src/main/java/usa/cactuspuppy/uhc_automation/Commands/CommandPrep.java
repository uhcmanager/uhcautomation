package usa.cactuspuppy.uhc_automation.Commands;

import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.Tasks.GenerateChunksHelper;
import usa.cactuspuppy.uhc_automation.UHCUtils;

@NoArgsConstructor
public class CommandPrep {
    public static void onCommand(CommandSender commandSender, String[] args) {
        if (Main.getInstance().getGameInstance().isActive()) {
            commandSender.sendMessage(ChatColor.RED + "Game is currently active, use /uhcstop to stop the game or wait until the current game is finished before attempt to prep the world.");
        }
        if (args.length == 0) {
            Main.getInstance().getGameInstance().prep();
            return;
        }
        if (args[0].equalsIgnoreCase("load")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new GenerateChunksHelper());
        } else if (args[0].equalsIgnoreCase("cancel")) {
            if (GenerateChunksHelper.getInstance() == null) {
                commandSender.sendMessage(ChatColor.RED + );
            }
        }
        Main.getInstance().getGameInstance().prep();
        return;
    }

    private static void generateChunk(Chunk chunk) {
        if (chunk.isLoaded()) { return; }
        chunk.load(true);
        chunk.unload(true);
    }

    private static int blockCoordtoChunkCoord(int i) {
        return (i - Math.floorMod(i, 16)) / 16;
    }
}
