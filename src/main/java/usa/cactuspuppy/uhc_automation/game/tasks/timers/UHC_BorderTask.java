package usa.cactuspuppy.uhc_automation.game.tasks.timers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.game.types.UHC;
import usa.cactuspuppy.uhc_automation.utils.GameUtils;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class UHC_BorderTask extends TimerTask {
    private UHC uhc;
    /**
     * Timestamp at which to begin shrinking
     */
    private long beginShrink;
    /**
     * Whether the border is currently shrinking
     */
    private boolean shrinking;
    /**
     * Speed of one side of the border in blocks per second
     */
    private double currentShrinkSpeed;
    /**
     * Number of players remaining at shrink
     */
    private int playersAtShrink;
    /**
     * List of countdown marks that will go to chat
     */
    private LinkedList<Long> chatMarks = new LinkedList<>();
    private long nextChatMark;
    /**
     * List of countdown marks that will go to title
     */
    private LinkedList<Long> titleMarks = new LinkedList<>();
    private long nextTitleMark;


    private static double baseSpeed = 0.3;
    private static double maxSpeed = 4.0;

    public UHC_BorderTask(UHC uhc, long initDelay) {
        super(uhc, true, 0L, 5L);
        this.uhc = uhc;
        currentShrinkSpeed = baseSpeed;
        beginShrink = uhc.getStartTime() + initDelay * 1000;
        parseConfigList();
        if (!chatMarks.isEmpty()) {
            nextChatMark = chatMarks.removeLast();
        } else {
            nextChatMark = -1;
        }
        if (!titleMarks.isEmpty()) {
            nextTitleMark = titleMarks.removeLast();
        } else {
            nextTitleMark = -1;
        }
    }

    private void parseConfigList() {
        String border = Main.getMainConfig().get("countdown.border");
        if (border == null) {
            border = Main.getMainConfig().get("countdown.default");
            if (border == null) {
                uhc.getUtils().log(Logger.Level.SEVERE, this.getClass(), "No countdown marks found, cannot set up countdown");
                return;
            }
        }
        String[] strings = border.split(",");
        for (String s : strings) {
            boolean title = false;
            if (s.startsWith("+")) {
                title = true;
                s = s.replaceFirst("\\+", "");
            }
            long value;
            try {
                value = Long.parseLong(s);
            } catch (NumberFormatException e) {
                try { //Distinguish if overflow
                    new BigInteger(s);
                } catch (NumberFormatException e1) {
                    uhc.getUtils().log(Logger.Level.WARNING, this.getClass(), "Could not parse " + s + " in countdown list, skipping...");
                    continue;
                }
                uhc.getUtils().log(Logger.Level.WARNING, this.getClass(), s + " is too large of a value, skipping...");
                continue;
            }
            if (title) {
                titleMarks.add(value);
            } else {
                chatMarks.add(value);
            }
        }
        //Sort resulting lists
        titleMarks = titleMarks.stream().sorted().collect(Collectors.toCollection(LinkedList::new));
        chatMarks = chatMarks.stream().sorted().collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        long currTime = System.currentTimeMillis();
        //If time to shrink, start shrinking
        if (currTime >= beginShrink) {
            playersAtShrink = uhc.getAlivePlayers().size();
            updateBorderSpeed(baseSpeed);
            uhc.getUtils().broadcastChatSoundTitle(ChatColor.RED.toString() + ChatColor.BOLD + "[ALERT] Border shrinking!", Sound.ENTITY_ENDER_DRAGON_DEATH, 1F, ChatColor.RED + "Border Shrinking!", String.format(
                    "Center: %d, %d | Final Radius: %d blocks", uhc.getCenterX(), uhc.getCenterZ(), uhc.getFinalRadius()
            ), 0, 80, 40);
            shrinking = true;
            //TODO: Border info display
            cancel();
            return;
        }
        //Check for chat mark
        long chatMark = beginShrink - nextChatMark * 1000;
        if (nextChatMark != -1 && currTime >= chatMark) {
            uhc.getUtils().broadcastChatSound(ChatColor.YELLOW + "[INFO] Border shrinks in " + ChatColor.WHITE + GameUtils.secsToFormatString(nextChatMark), Sound.ENTITY_PLAYER_LEVELUP, 0.5F);
            if (!chatMarks.isEmpty()) {
                nextChatMark = chatMarks.removeLast();
            } else {
                nextChatMark = -1;
            }
        }
        //Check for title mark
        long titleMark = beginShrink - nextTitleMark * 1000;
        if (nextTitleMark != -1 && currTime >= titleMark) {
            uhc.getUtils().broadcastSoundTitle(Sound.BLOCK_NOTE_BLOCK_PLING, 1.17F, String.valueOf(nextTitleMark), ChatColor.RED + "Border shrinks in...", 0, 20, 10);
            if (!titleMarks.isEmpty()) {
                nextTitleMark = titleMarks.removeLast();
            } else {
                nextTitleMark = -1;
            }
        }
    }

    public void speedUp() {
        if (!shrinking || !uhc.isDynamicSpeed()) {
            return;
        }
        int initPlayer = playersAtShrink;
        if (initPlayer < 3) {
            initPlayer = 3; //Avoid zero or negative numbers in addSpeed calculation
        }
        if (currentShrinkSpeed == maxSpeed) {
            return;
        }
        double addSpeed = 1D / (initPlayer - 2) * (maxSpeed - baseSpeed);
        double newSpeed = currentShrinkSpeed + addSpeed;
        if (newSpeed > maxSpeed) {
            newSpeed = maxSpeed;
        }
        updateBorderSpeed(newSpeed);
    }

    private void updateBorderSpeed(double shrinkSpeed) {
        Set<World> worldSet = uhc.getOtherWorlds().stream().map(Bukkit::getWorld).filter(Objects::nonNull).collect(Collectors.toSet());
        worldSet.add(uhc.getMainWorld());
        int finalRadius = uhc.getFinalRadius();
        double remainingDistance = uhc.getMainWorld().getWorldBorder().getSize() - 2 * finalRadius;
        long overSecs = (long) Math.floor(remainingDistance / shrinkSpeed) + (remainingDistance % shrinkSpeed == 0 ? 0 : 1);
        if (remainingDistance / overSecs > maxSpeed) {
            overSecs = (long) (remainingDistance / maxSpeed);
        }
        for (World w : worldSet) {
            double realRadius = finalRadius;
            if (w.getEnvironment().equals(World.Environment.NETHER)) {
                realRadius = Math.floor(finalRadius / 8D);
                if (realRadius < 0.5) {
                    realRadius = 0.5;
                }
            }
            w.getWorldBorder().setSize(2 * realRadius, overSecs);
        }
    }
}
