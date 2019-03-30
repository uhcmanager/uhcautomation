package usa.cactuspuppy.uhc_automation.game.tasks.timers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.tasks.listeners.PVPCanceller;
import usa.cactuspuppy.uhc_automation.game.types.UHC;
import usa.cactuspuppy.uhc_automation.utils.MiscUtils;

public class PVPGracePeriod extends AlertTimer {
    private long pvpEnableTime;

    private UHC uhc;

    private PVPCanceller canceller;

    public PVPGracePeriod(UHC uhcInstance, long pvpDelay, PVPCanceller pvpCanceller) {
        super(uhcInstance, 5L, "countdown.default");
        long currTime = System.currentTimeMillis();
        pvpEnableTime = currTime + pvpDelay * 1000;
        canceller = pvpCanceller;
        uhc = uhcInstance;
    }

    @Override
    public void run() {
        long currTime = System.currentTimeMillis();
        //If time has come, enable PVP
        if (currTime >= pvpEnableTime) {
            canceller.cancel();
            gameInstance.getUtils().broadcastChatSoundTitle(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "\nPVP ENABLED!", Sound.ENTITY_ENDER_DRAGON_GROWL, 1F, ChatColor.DARK_RED.toString() + ChatColor.BOLD + "PVP", "enabled", 0, 40, 20);
            cancel();
        }
        //Check for chat mark
        long chatMark = pvpEnableTime - nextChatMark * 1000;
        if (nextChatMark != -1 && currTime >= chatMark) {
            uhc.getUtils().broadcastChatSound("[" + ChatColor.YELLOW + "INFO" + ChatColor.WHITE + "] PVP enabled in " + ChatColor.AQUA + MiscUtils.secsToFormatString(nextChatMark), Sound.ENTITY_PLAYER_LEVELUP, 0.5F);
            if (!chatMarks.isEmpty()) {
                nextChatMark = chatMarks.removeLast();
            } else {
                nextChatMark = -1;
            }
        }
        //Check for title mark
        long titleMark = pvpEnableTime - nextTitleMark * 1000;
        if (nextTitleMark != -1 && currTime >= titleMark) {
            uhc.getUtils().broadcastSoundTitle(Sound.BLOCK_NOTE_BLOCK_PLING, 1.17F, String.valueOf(nextTitleMark), ChatColor.RED + "PVP enabled in...", 0, 20, 10);
            if (!titleMarks.isEmpty()) {
                nextTitleMark = titleMarks.removeLast();
            } else {
                nextTitleMark = -1;
            }
        }
    }
}
