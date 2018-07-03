package usa.cactuspuppy.uhc_automation.Tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.TimeDisplayMode;
import usa.cactuspuppy.uhc_automation.TimeModeCache;

public class TimeAnnouncer implements Runnable {
    private Main m;
    private boolean overrride;

    public TimeAnnouncer(Main main) {
        m = main;
        overrride = false;
    }

    @Override
    public void run() {
        if (overrride) {
            return;
        }
        m.gi.getActivePlayers().stream().map(Bukkit::getPlayer).forEach(this::showTimetoPlayer);
    }

    private void showTimetoPlayer(Player player) {
        TimeDisplayMode tdm = TimeModeCache.getInstance().getPlayerPref(player.getUniqueId());
        if (tdm == null) {
            Bukkit.getLogger().warning(player.getName() + " possess an invalid TimeDisplayMode. Setting to default (CHAT)...");
            TimeModeCache.getInstance().storePlayerPref(player.getUniqueId(), TimeDisplayMode.CHAT);
        } else if (tdm == TimeDisplayMode.SCOREBOARD) {

        } else if (tdm == TimeDisplayMode.SUBTITLE) {
            //TODO: Show player time in subtitle
        }
    }

    public void schedule() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(m, this, 0L, 5L);
    }
}
