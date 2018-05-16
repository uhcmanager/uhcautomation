package usa.cactuspuppy.uhc_automation;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DelayReactivate implements Runnable {
    private GameInstance g;

    public DelayReactivate(GameInstance gi) {
        g = gi;
    }

    @Override
    public void run() {
        if (!UHCUtils.isWorldData(g.main)) {
            return;
        }
        g.main.getLogger().info("Found previous game data, attempting to load...");
        Map<String, Set<UUID>> playerSets = UHCUtils.loadWorldPlayers(g.main);
        if (!playerSets.isEmpty()) {
            g.livePlayers = playerSets.get("livePlayers");
            g.allPlayers = playerSets.get("allPlayers");
        }
        g.startT = UHCUtils.loadStartTime(g.main);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerDeathListener(g.main), g.main);
        UHCUtils.exeCmd("gamemode 0 @a[m=2]");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
        g.main.getLogger().info("Game Reinitiate Time - " + sdf.format(new Date(System.currentTimeMillis())));
        UHCUtils.exeCmd("gamerule doDaylightCycle true");
        UHCUtils.exeCmd("gamerule doWeatherCycle true");
        g.borderCountdown = (new BorderCountdown(g.main, g.minsToShrink * 60, g.startT)).schedule();
        (new EpisodeAnnouncer(g.main, g.epLength, g.startT)).schedule();
        HandlerList.unregisterAll(g.freezePlayers);
        g.setActive(true);
    }

    public void schedule() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(g.main, this, 1L);
    }
}
