package usa.cactuspuppy.uhc_automation.Tasks;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
        g.setTimeAnnouncer(new TimeAnnouncer());
        if (!UHCUtils.isWorldData(g.getMain())) {
            return;
        }
        g.getMain().getLogger().info("Found previous game data, attempting to load...");
        Map<String, Set<UUID>> playerSets = UHCUtils.loadWorldPlayers(g.getMain());
        if (playerSets.isEmpty()) {
            g.getMain().getLogger().info("Failed to load game data, game not restarted.");
            return;
        } else {
            g.setBlacklistPlayers(playerSets.get("blacklistPlayers"));
            g.setRegPlayers(playerSets.get("regPlayers"));
        }
        g.recalcPlayerSet();
        Map<String, Object> auxData = UHCUtils.loadAuxData(g.getMain());
        if (auxData.isEmpty()) {
            g.getMain().getLogger().info("Failed to load auxiliary data, game not restarted.");
            return;
        } else {
            g.setTeamMode((boolean) auxData.get("teamMode"));
            g.setStartT((long) auxData.get("sT"));
        }
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerDeathListener(), g.getMain());
        g.getLivePlayers().stream().map(Bukkit::getPlayer).forEach(p -> p.setGameMode(GameMode.SURVIVAL));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
        g.getMain().getLogger().info("Game Reinitiate Time - " + sdf.format(new Date(System.currentTimeMillis())));
        if (g.getEpLength() != 0) {
            (new EpisodeAnnouncer(g.getEpLength(), g.getStartT())).schedule();
        }
        if (g.getMinsToShrink() > 0) {
            g.setBorderCountdown((new BorderCountdown(g.getMinsToShrink() * 60, g.getStartT())).schedule());
        }
        HandlerList.unregisterAll(g.getFreezePlayers());
        g.setActive(true);
    }

    public void schedule() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(g.getMain(), this, 1L);
    }
}
