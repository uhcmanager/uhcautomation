package usa.cactuspuppy.uhc_automation.Commands;

import lombok.NoArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

@NoArgsConstructor
public class CommandPrep {
    public static boolean onCommand(CommandSender commandSender, String[] args) {
        if (Main.getInstance().getGameInstance().isActive()) {
            commandSender.sendMessage(ChatColor.RED + "Game is currently active, use /uhcstop to stop the game or wait until the current game is finished before attempt to prep the world.");
        }
        if (args.length >= 1 && args[0].equalsIgnoreCase("load")) {
            Thread worldGen = new Thread(generateChunks);
            worldGen.run();
        }
        Main.getInstance().getGameInstance().prep();
        return true;
    }

    private static Runnable generateChunks = () -> {
        Main.getInstance().getLogger().info(ChatColor.RED + "Beginning async chunk pre-load, server performance may suffer during this time.");
        UHCUtils.broadcastMessage(Main.getInstance().getGameInstance(), ChatColor.RED.toString() + ChatColor.BOLD + "Preloading game world, lag may occur during this time!");
        long startTime = System.currentTimeMillis();
        World world = Main.getInstance().getGameInstance().getWorld();
        int initSize = Main.getInstance().getGameInstance().getInitSize();
        Chunk posCorner = world.getChunkAt((initSize + 1) / 2, (initSize + 1) / 2);
        int chunkX = posCorner.getX();
        int chunkZ = posCorner.getZ();
        for (int x = -chunkX; x <= chunkX; x++) {
            for (int z = -chunkZ; z <= chunkZ; z++) {
                generateChunk(world.getChunkAt(x, z));
            }
        }
        long timeElapsed = System.currentTimeMillis() - startTime;
        Main.getInstance().getLogger().info(ChatColor.GREEN + "Chunk pre-load complete! Took " + timeElapsed / 1000 + " seconds (" + timeElapsed + " ms)");
    };

    private static void generateChunk(Chunk chunk) {
        if (chunk.isLoaded()) { return; }
        chunk.load(true);
        chunk.unload(true);
    }
}
