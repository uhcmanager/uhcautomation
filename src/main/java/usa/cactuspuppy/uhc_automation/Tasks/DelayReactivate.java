package usa.cactuspuppy.uhc_automation.Tasks;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import usa.cactuspuppy.uhc_automation.Commands.CommandSurface;
import usa.cactuspuppy.uhc_automation.GameInstance;
import usa.cactuspuppy.uhc_automation.Listeners.PlayerDeathListener;
import usa.cactuspuppy.uhc_automation.Listeners.WorldChangeListener;
import usa.cactuspuppy.uhc_automation.Main;
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
        g.setInfoAnnouncer(new InfoAnnouncer());
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
        Bukkit.getServer().getPluginManager().registerEvents(new WorldChangeListener(), Main.getInstance());
        g.getLivePlayers().stream().map(Bukkit::getPlayer).forEach(p -> p.setGameMode(GameMode.SURVIVAL));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
        long now = System.currentTimeMillis();
        g.getMain().getLogger().info("Game Reinitiate Time - " + sdf.format(new Date(now)));
        if (g.getEpLength() != 0) {
            (new EpisodeAnnouncer(g.getEpLength(), g.getStartT())).schedule();
        }
        if (now >= g.getStartT() + g.getMinsToShrink() * 60000) {
            Main.getInstance().getLogger().info("Border was shrinking before restart, initiating post-border-shrink mechanics now...");
            World world = g.getWorld();
            (new BorderAnnouncer()).schedule();
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setTime(6000L);
            world.setStorm(false);
            CommandSurface.startHelper();
            g.setBorderShrinking(true);
        } else if (g.getMinsToShrink() > 0) {
            g.setBorderCountdown((new BorderCountdown(g.getMinsToShrink() * 60, g.getStartT(), Main.getInstance().getConfig().getBoolean("warnings.border", true))).schedule());
        }
        if (g.getSecsToPVP() == 0) {
            g.getWorld().setPVP(true);
        } else if (g.getSecsToPVP() > 0) {
            (new PVPEnableCountdown(g.getSecsToPVP(), g.getStartT(), Main.getInstance().getConfig().getBoolean("warnings.pvp", true))).schedule();
        }
        HandlerList.unregisterAll(g.getFreezePlayers());
        g.getInfoAnnouncer().schedule();
        g.getInfoAnnouncer().showBoard();
        g.setActive(true);
    }

    public void schedule() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(g.getMain(), this, 0L);
    }
}
