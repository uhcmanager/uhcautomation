package usa.cactuspuppy.uhc_automation.Tasks;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import usa.cactuspuppy.uhc_automation.GameInstance;
import usa.cactuspuppy.uhc_automation.Listeners.PlayerDeathListener;
import usa.cactuspuppy.uhc_automation.UHCUtils;

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
        g.setTimeAnnouncer(new TimeAnnouncer(g.main));
        if (!UHCUtils.isWorldData(g.main)) {
            return;
        }
        g.main.getLogger().info("Found previous game data, attempting to load...");
        Map<String, Set<UUID>> playerSets = UHCUtils.loadWorldPlayers(g.main);
        if (playerSets.isEmpty()) {
            g.main.getLogger().info("Failed to load game data, game not restarted.");
            return;
        } else {
            g.setBlacklistPlayers(playerSets.get("blacklistPlayers"));
            g.setRegPlayers(playerSets.get("regPlayers"));
        }
        g.recalcPlayerSet();
        Map<String, Object> auxData = UHCUtils.loadAuxData(g.main);
        if (auxData.isEmpty()) {
            g.main.getLogger().info("Failed to load auxiliary data, game not restarted.");
            return;
        } else {
            g.setTeamMode((boolean) auxData.get("teamMode"));
            g.startT = (long) auxData.get("sT");
        }
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerDeathListener(g.main), g.main);
        UHCUtils.exeCmd("gamemode 0 @a[m=2]");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
        g.main.getLogger().info("Game Reinitiate Time - " + sdf.format(new Date(System.currentTimeMillis())));
        UHCUtils.exeCmd("gamerule doDaylightCycle true");
        UHCUtils.exeCmd("gamerule doWeatherCycle true");
        if (g.getEpLength() != 0) {
            (new EpisodeAnnouncer(g.main, g.getEpLength(), g.startT)).schedule();
        }
        if (g.getMinsToShrink() > 0) {
            g.setBorderCountdown((new BorderCountdown(g.main, g.getMinsToShrink() * 60, g.startT)).schedule());
        }
        HandlerList.unregisterAll(g.getFreezePlayers());
        g.setActive(true);
    }

    public void schedule() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(g.main, this, 1L);
    }
}
