package usa.cactuspuppy.uhc_automation.game.tasks.timers;

import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import usa.cactuspuppy.uhc_automation.game.tasks.listeners.UHC_PreStartFreeze;
import usa.cactuspuppy.uhc_automation.game.GameStateEvent;
import usa.cactuspuppy.uhc_automation.game.types.UHC;
import usa.cactuspuppy.uhc_automation.utils.GameUtils;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.util.*;

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
     * List of locations to use for spreading. Assumes that the list has already looked for highest block at that location.
     */
    private List<Location> locations;

    //BLOCKLISTS
    public static final List<Material> noStand = new ArrayList<>();
    static {
        noStand.add(Material.CACTUS);
        noStand.add(Material.MAGMA_BLOCK);
    }

    //TIMING CONSTANTS
    private int trans0 = 20; //No coords to one coord
    private int trans1 = 40; //One coord to two coords
    private int trans2 = 60; //Two coords to launch

    private int tpDelay = 2000; //milliseconds to wait after launch to tp

    private Random rng = new Random();
    private int runs = 0;
    private long launchTime = -1;

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
        boolean shouldTeleport = launchTime != -1 && System.currentTimeMillis() >= launchTime + tpDelay;

        //Generate random coords once because we don't need separate ones for everyone
        int randXInt = rng.nextInt(2 * uhcInstance.getInitRadius() + 1) - uhcInstance.getInitRadius();
        int randYInt = rng.nextInt(256);
        int randZInt = rng.nextInt(2 * uhcInstance.getInitRadius() + 1) - uhcInstance.getInitRadius();

        //Check for launch init
        if (runs == trans2) {
            launchTime = System.currentTimeMillis();
        }

        int index = 0;
        for (UUID u : gameInstance.getSpectators()) {
            Player p = Bukkit.getPlayer(u); //Get player
            if (p == null) { continue; }

            p.sendTitle(ChatColor.GOLD + "Starting game...", ChatColor.WHITE + "Spreading players...", 0, 20, 10);
        }
        for (UUID u : gameInstance.getAlivePlayers()) {
            Player p = Bukkit.getPlayer(u); //Get player
            if (p == null) { continue; }

            //Get location
            if (index >= locations.size()) {
                gameInstance.getUtils().log(Logger.Level.WARNING, this.getClass(), "Exceeded locations size during spreadplayers");
                cancel();
                return;
            }
            Location l = locations.get(index);
            index++;
            String x = StringUtils.center(Integer.toString((runs < trans0 ? randXInt : l.getBlockX())), 9);
            String y = StringUtils.center(Integer.toString((runs < trans1 ? randYInt : l.getBlockY())), 5);
            String z = StringUtils.center(Integer.toString((runs < trans2 ? randZInt : l.getBlockZ())), 9);

            StringJoiner subtitle = new StringJoiner(",");
            subtitle.add(x).add(y).add(z);
            String title = (runs < trans2 ? ChatColor.RED : ChatColor.GREEN) + (runs < trans2 ? "Finding Location..." : "Final Location");
            ChatColor subColor = (runs < trans2 ? ChatColor.GRAY : ChatColor.WHITE);

            if (runs == trans0 || runs == trans1 || runs == trans2) { //Coord lock sounds
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 1.17F);
            }

            //Show title
            p.sendTitle(title, subColor + subtitle.toString(), 0, 20, 10);
            //Play clicks if coords still locking
            if (runs < trans2) {
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1F, 0.3F);
            }

            //Levitation
            if (runs < trans2) {
                p.addPotionEffect(
                        new PotionEffect(PotionEffectType.LEVITATION, 1, 0, true, false, false), true
                );
            } else {
                p.addPotionEffect(
                        new PotionEffect(PotionEffectType.LEVITATION, 1, 100, true, false, false), true
                );
            }

            if (shouldTeleport) {
                teleport(p, l);
            }
        }

        if (shouldTeleport) { //Initiate pre-game countdown
            new UHC_PreStartFreeze(gameInstance).init();
            new UHC_StartCountdown(gameInstance, 10).init();
            cancel();
            return;
        }

        runs++;
    }

    /**
     * Helper method to safely teleport player to 74 blocks above spread location (74 for 10 sec drop)
     * @param p Player to teleport
     * @param l Location to teleport to
     */
    private void teleport(Player p, Location l) {
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();
        World world = l.getWorld();
        if (world == null) {
            world = Bukkit.getWorld(gameInstance.getMainWorldUID());
            if (world == null) {
                gameInstance.getUtils().log(Logger.Level.SEVERE, this.getClass(), "Unable to find world to teleport in, reseting game...");
                gameInstance.updateState(GameStateEvent.RESET);
                return;
            }
        }

        Block feet = world.getBlockAt(x, y, z);
        while (!feet.isPassable() || !world.getBlockAt(feet.getLocation().add(0, 1, 0)).isPassable()) { //Get actual highest block
            y++;
            feet = world.getBlockAt(x, y, z);
        }
        Block belowFeet = world.getBlockAt(feet.getLocation().add(0, -1, 0));
        if (belowFeet.isPassable() || belowFeet.isLiquid() || noStand.contains(belowFeet.getType())) {
            belowFeet.setBlockData(Bukkit.createBlockData(Material.STONE_SLAB, "type=top"));
        }
        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 10, true, false, false), true);
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 1000000, 0, true, false, false), true);
        p.setGameMode(GameMode.ADVENTURE);
        GameUtils.relativeTeleport(feet.getLocation().add(0, 74, 0), p);
    }
}
