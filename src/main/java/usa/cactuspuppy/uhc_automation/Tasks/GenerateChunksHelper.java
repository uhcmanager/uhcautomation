package usa.cactuspuppy.uhc_automation.Tasks;

import lombok.Getter;
import org.bukkit.ChatColor;
import usa.cactuspuppy.uhc_automation.Main;

public class GenerateChunksHelper implements Runnable {
    @Getter private static GenerateChunksHelper instance;
    private long startTime;

    public GenerateChunksHelper() {
        instance = this;
        startTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        long timeElapsed = System.currentTimeMillis() - startTime;
        Main.getInstance().getLogger().info(ChatColor.GREEN + "Chunk pre-load complete! Took " + timeElapsed / 1000 + " seconds (" + timeElapsed + " ms)");
    }
}
