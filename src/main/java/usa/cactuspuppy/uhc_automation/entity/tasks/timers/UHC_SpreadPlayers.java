package usa.cactuspuppy.uhc_automation.entity.tasks.timers;

import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.types.UHC;
import usa.cactuspuppy.uhc_automation.utils.UHCUtils;

import java.util.List;
import java.util.Random;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * Inspired by Reddit user Iron_Zealot's Random TP Plugin:
 * @see <a href="https://www.reddit.com/r/Minecraft/comments/a47koc/random_tp_plugins_are_boring_lets_spice_it_up/">Original Reddit post</a>
 */
public class UHC_SpreadPlayers extends TimerTask {
    /**
     * Store instance as UHC class to allow access to UHC-specific fields
     */
    private UHC uhcInstance;

    /**
     * List of locations to use for spreading
     */
    private List<Location> locations;
    /**
     * Marks whether the state changed on last run
     */
    private boolean changedState = true;
    /**
     * Current phase of spreading
     */
    private Phase phase = Phase.NO_COORDS;

    private Random rng = new Random();
    private int runs = 0;
    private long launchTime;

    public UHC_SpreadPlayers(UHC instance, List<Location> locations) {
        super(instance, true, 0L, 1L);
        uhcInstance = instance;
        this.locations = locations;
    }

    @Override
    public void cancel() {
        if (taskID == null) { return; }
        Bukkit.getScheduler().cancelTask(taskID);
    }

    @Override
    public void run() {
        String delimiter = ", ";

        int randX = rng.nextInt(2 * uhcInstance.getInitRadius() + 1) - uhcInstance.getInitRadius();
        int randY = rng.nextInt(256);
        int randZ = rng.nextInt(2 * uhcInstance.getInitRadius() + 1) - uhcInstance.getInitRadius();
        String randXS = StringUtils.center(Integer.toString(randX), 9);
        String randYS = StringUtils.center(Integer.toString(randY), 5);
        String randZS = StringUtils.center(Integer.toString(randZ), 9);
        if (phase == Phase.TELEPORTED) {
            if (changedState) {
                new UHCUtils(gameInstance).broadcastTitle(ChatColor.GOLD + "Loading Chunks", "Please wait...", 0, 40, 20);
            }
        } else if (phase == Phase.NO_COORDS) {
            for (UUID u : gameInstance.getAlivePlayers()) {
                Player p = Bukkit.getPlayer(u);
                if (p == null) {
                    continue;
                }
                p.sendTitle(ChatColor.RED + "Final Destination",
                        ChatColor.GRAY + new StringJoiner(delimiter).add(randXS).add(randYS).add(randZS).toString(),
                        0, 20, 10);
                p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 1, 0, true, false, false), true);
            }
        } else if (phase == Phase.X_COORD || phase == Phase.XY_COORD) {
            int index = 0;
            for (UUID u : gameInstance.getAlivePlayers()) {
                Player p = Bukkit.getPlayer(u);
                if (p == null) {
                    continue;
                }
                Location l = locations.get(index);

                String actX = StringUtils.center(Integer.toString(l.getBlockX()), 9);
                String actY = StringUtils.center(Integer.toString(l.getBlockY()), 5);
                String subtitle = "";

                if (phase == Phase.X_COORD) {
                    subtitle = new StringJoiner(delimiter).add(actX).add(randYS).add(randZS).toString();
                } else if (phase == Phase.XY_COORD) {
                    subtitle = new StringJoiner(delimiter).add(actX).add(actY).add(randZS).toString();
                }

                p.sendTitle(ChatColor.RED + "Final Destination", subtitle, 0, 20, 10);
                index++;
            }
        } else if (phase == Phase.XYZ_COORD) {
            if (changedState) {
                launchTime = System.currentTimeMillis();
            }

            int index1 = 0;
            for (UUID u : gameInstance.getAlivePlayers()) {
                Player p = Bukkit.getPlayer(u);
                if (p == null) {
                    continue;
                }
                Location l = locations.get(index1);

                String actX = StringUtils.center(Integer.toString(l.getBlockX()), 9);
                String actY = StringUtils.center(Integer.toString(l.getBlockY()), 5);
                String actZ = StringUtils.center(Integer.toString(l.getBlockZ()), 9);
                p.sendTitle(ChatColor.GREEN + "Final Destination", new StringJoiner(delimiter).add(actX).add(actY).add(actZ).toString(), 0, 20, 10);
            }
        }

        if (changedState) { changedState = false; }

        //Tick-based changing
        if (runs > 20 && phase == Phase.NO_COORDS) {
            phase = Phase.X_COORD;
            changedState = true;
        } else if (runs > 40 && phase == Phase.X_COORD) {
            phase = Phase.XY_COORD;
            changedState = true;
        } else if (runs > 60 && phase == Phase.XY_COORD) {
            phase = Phase.XYZ_COORD;
            changedState = true;
        }
    }

    public enum Phase {
        NO_COORDS,
        X_COORD,
        XY_COORD,
        XYZ_COORD,
        TELEPORTED
    }
}
