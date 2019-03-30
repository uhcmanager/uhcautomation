package usa.cactuspuppy.uhc_automation.game.tasks.timers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.game.types.UHC;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class UHC_BorderTask extends TimerTask {
    private UHC uhc;
    /**
     * Timestamp at which to begin shrinking
     */
    long beginShrink;
    /**
     * Whether the border is currently shrinking
     */
    boolean shrinking;
    /**
     * Speed of one side of the border in blocks per second
     */
    double currentShrinkSpeed;
    /**
     * Number of players remaining at shrink
     */
    int playersAtShrink;
    /**
     * List of countdown marks that will go to chat
     */
    LinkedList<Long> chatMarks = new LinkedList<>();
    /**
     * List of countdown marks that will go to title
     */
    LinkedList<Long> titleMarks = new LinkedList<>();


    private static double baseSpeed = 0.3;
    private static double maxSpeed = 4.0;

    public UHC_BorderTask(UHC uhc, long initDelay) {
        super(uhc, true, 0L, 5L);
        this.uhc = uhc;
        currentShrinkSpeed = baseSpeed;
        beginShrink = uhc.getStartTime() + initDelay * 1000;
        parseConfigList();
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
        //TODO: Set timer for shrinking
        long currTime = System.currentTimeMillis();
        //If time to shrink, start shrinking
        if (currTime >= beginShrink) {
            playersAtShrink = uhc.getAlivePlayers().size();
            updateBorderSpeed(baseSpeed);
            uhc.getUtils().broadcastChatSoundTitle(ChatColor.RED + ChatColor.BOLD + "Border shrinking!", Sound.ENTITY_ENDER_DRAGON_DEATH, 1F, ChatColor.RED + "Border Shrinking!", String.format(
                    "Center: %d, %d | Final Radius: %d blocks", uhc.getCenterX(), uhc.getCenterZ(), uhc.getFinalRadius()
            ));
            shrinking = true;
        }
    }

    public void speedUp() {
        if (!shrinking || !uhc.isDynamicSpeed()) {
            return;
        }
        int initPlayer = gameInstance.getInitNumPlayers();
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
