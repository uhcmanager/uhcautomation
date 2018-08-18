package usa.cactuspuppy.uhc_automation.Tasks;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

import java.util.logging.Level;

public class GenerateChunksHelper implements Runnable {
    @Getter private static GenerateChunksHelper instance;
    @Getter private static boolean running;

    private long startTime;
    private int minChunkX, maxChunkX, minChunkZ, maxChunkZ;
    private static int chunkX, chunkZ;
    private World world;

    @Getter
    private int schedulerID;

    public GenerateChunksHelper() {
        startTime = System.currentTimeMillis();
        world = Main.getInstance().getGameInstance().getWorld();
        int radius = blockCoordtoChunkCoord(Main.getInstance().getGameInstance().getInitSize() / 2) + 1;
        this.minChunkX = -radius;
        this.maxChunkX = radius;
        this.minChunkZ = -radius;
        this.maxChunkZ = radius;
        if (!running) {
            chunkX = minChunkX;
            chunkZ = minChunkZ;
        }
        instance = this;
        Main.getInstance().getLogger().info(String.format("Chunk pre-generation %s. Lag may occur during this time...", (running ? "resumed" : "initiated")));
        UHCUtils.broadcastMessage("[" + ChatColor.GOLD + "UHC" + ChatColor.RESET + "] " + ChatColor.DARK_RED + ChatColor.BOLD + "Chunk pre-generation beginning. Severe lag may occur.");
    }

    @Override
    public void run() {
        if (chunkX == maxChunkX && chunkZ == maxChunkZ) {
            generateChunk(world.getChunkAt(chunkX, chunkZ));
            Main.getInstance().getLogger().fine("Generated chunk at chunk coords X: " + chunkX + ", Z: " + chunkZ);
            long timeElapsed = System.currentTimeMillis() - startTime;
            Main.getInstance().getLogger().info(ChatColor.GREEN + "Chunk pre-generation complete! Took " + timeElapsed / 1000 + " seconds (" + timeElapsed + " ms)");
            Bukkit.getScheduler().cancelTask(schedulerID);
            instance = null;
            return;
        }
        generateChunk(world.getChunkAt(chunkX, chunkZ));
        Main.getInstance().getLogger().log(Level.FINE, "Generated chunk at chunk coords X: " + chunkX + ", Z: " + chunkZ);
        if (chunkZ == maxChunkZ) {
            double completion = (chunkX - minChunkX) / (double) (maxChunkX - minChunkX);
            Main.getInstance().getLogger().info(String.format("Chunk pre-generation %.2f%% complete", completion * 100));
            chunkX++;
            chunkZ = minChunkZ;
        } else {
            chunkZ++;
        }
    }

    private static void generateChunk(Chunk chunk) {
        if (chunk.isLoaded()) {
            return;
        }
        chunk.load(true);
        chunk.unload(true);
    }

    private static int blockCoordtoChunkCoord(int i) {
        return (i - Math.floorMod(i, 16)) / 16;
    }

    public void schedule() {
        schedulerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), this, 0L, 2L);
    }

    public static void stop() {
        UHCUtils.broadcastMessage(ChatColor.YELLOW + "Chunk pre-generation stopped.");
        Bukkit.getScheduler().cancelTask(getInstance().schedulerID);
        running = false;
        instance = null;
    }

    public static void pause() {
        UHCUtils.broadcastMessage(ChatColor.YELLOW + "Chunk pre-generation paused.");
        Bukkit.getScheduler().cancelTask(getInstance().schedulerID);
        instance = null;
    }
}
