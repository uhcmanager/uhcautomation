package usa.cactuspuppy.uhc_automation.game.tasks.timers;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import usa.cactuspuppy.uhc_automation.game.types.UHC;
import usa.cactuspuppy.uhc_automation.utils.MiscUtils;

public class UHC_EpisodeMarker extends TimerTask {
    private UHC uhc;
    private long epLength;
    private long nextEpMark;

    public UHC_EpisodeMarker(UHC gameInstance, long epLength) {
        super(gameInstance, true, 0L, 5L);
        long currTime = System.currentTimeMillis();
        this.epLength = epLength;
        nextEpMark = currTime + epLength * 1000;
    }

    @Override
    public void run() {
        long currTime = System.currentTimeMillis();
        if (currTime >= nextEpMark) {
            long timeElapsed = (nextEpMark - getGameInstance().getStartTime()) / 1000;
            long epCount = timeElapsed / epLength;
            getGameInstance().getUtils().broadcastChatSound(
                    String.format(ChatColor.GOLD + "\nMARK: " + ChatColor.WHITE + "End of Episode " + ChatColor.GREEN + "%d\n"
                                    + ChatColor.AQUA + "Time Elapsed: " + ChatColor.WHITE + "%s\n"
                                    + ChatColor.GREEN + "=================="
                            , epCount, WordUtils.capitalize(MiscUtils.secsToFormatString(timeElapsed)))
                    , Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1F);
            nextEpMark = nextEpMark + epLength * 1000;
        }
    }
}
