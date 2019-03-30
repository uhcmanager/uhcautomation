package usa.cactuspuppy.uhc_automation.game.types;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.tasks.GameStartAnnouncer;
import usa.cactuspuppy.uhc_automation.game.tasks.listeners.ListenerTask;
import usa.cactuspuppy.uhc_automation.game.tasks.listeners.UHC_ActiveListener;
import usa.cactuspuppy.uhc_automation.game.tasks.listeners.UHC_LobbyListener;
import usa.cactuspuppy.uhc_automation.game.tasks.timers.TimerTask;
import usa.cactuspuppy.uhc_automation.game.tasks.timers.UHC_BorderTask;
import usa.cactuspuppy.uhc_automation.game.tasks.timers.UHC_SpreadPlayers;
import usa.cactuspuppy.uhc_automation.utils.GameUtils;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.util.*;

@Getter
public class UHC extends GameInstance {
    //[=== PLAY AREA INFO ===]
    /**
     * Time to delay border shrinking, in seconds.
     * -1 disables border shrinking entirely
     */
    @Setter(AccessLevel.PUBLIC)
    protected int timeToShrink = 7200;

    @Setter(AccessLevel.PUBLIC)
    protected int centerX = 0;

    @Setter(AccessLevel.PUBLIC)
    protected int centerZ = 0;

    /**
     * Radius of the initial play space, in blocks from 0, 0 (0.5, 0.5)
     */
    @Setter(AccessLevel.PUBLIC)
    protected int initRadius = 2000;

    @Setter(AccessLevel.PUBLIC)
    protected int finalRadius = 25;

    /**
     * Minimum distance from center for starting locations
     */
    @Setter(AccessLevel.PUBLIC)
    protected int minDistance = 150;

    /**
     * Minimum distance between starting locations
     */
    @Setter(AccessLevel.PUBLIC)
    protected int minSeparation = 150;

    // [=== LOBBY INFO ===]
    @Setter(AccessLevel.PUBLIC)
    protected int lobbyRadius = 11;

    @Setter(AccessLevel.PUBLIC)
    protected int blocksAboveGround = 50;

    // [=== EPISODE INFO ===]
    /**
     * Length of episodes, in seconds. -1 to disable.
     */
    protected long epLength = 1200;

    // [=== BORDER INFO ==]
    /**
     * Whether the border will increase speed linearly as players die
     */
    protected boolean dynamicSpeed = true;

    public UHC(World world) {
        super(world);
    }

    //TODO: Implement methods
    @Override
    protected boolean reset() {
        TimerTask.clearInstanceTimers(this);
        ListenerTask.clearInstanceListeners(this);
        //TODO: Create lobby
        new UHC_LobbyListener(this).init();
        World main = getMainWorld();
        if (main == null) {
            getUtils().log(Logger.Level.WARNING, this.getClass(), "Null main world, cannot resolve.");
            getUtils().msgManagers(ChatColor.RED + "RESET ERROR: No main world set, cannot reset.");
            return false;
        }
        createLobby();
        // Day/weather cancel
        main.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        main.setTime(1000);
        main.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        main.setStorm(false);
        main.setThundering(false);
        // Worldborder reset
        main.getWorldBorder().setCenter(centerX + 0.5, centerZ + 0.5);
        main.getWorldBorder().reset();
        // Natural regen
        main.setGameRule(GameRule.NATURAL_REGENERATION, false);
        // Other worlds
        otherWorlds.stream().map(Bukkit::getWorld).filter(Objects::nonNull).forEach(this::resetWorld);

        return true;
    }

    private void resetWorld(World world) {
        world.setGameRule(GameRule.NATURAL_REGENERATION, false);
        if (world.getEnvironment().equals(World.Environment.NETHER)) {
            world.getWorldBorder().setCenter(centerX/8D + 0.5, centerZ/8D + 0.5);
        } else {
            world.getWorldBorder().setCenter(centerX + 0.5, centerZ + 0.5);
        }
        if (world.getEnvironment().equals(World.Environment.NORMAL)) {
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setTime(1000);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setStorm(false);
            world.setThundering(false);
        }
        world.getWorldBorder().reset();
    }

    private void startWorld(World world) {
        if (world.getEnvironment().equals(World.Environment.NORMAL)) {
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
            world.setTime(0);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, true);
            int ticksInDay = 24000;
            int timeToClear = new Random().nextInt(2 * ticksInDay) + ticksInDay;
            world.setWeatherDuration(timeToClear);
        }
    }

    private void createLobby() {
        setLobby(false);
    }

    private void clearLobby() {
        setLobby(true);
    }

    private void setLobby(boolean destroy) {
        World main = getMainWorld();
        if (main == null) {
            return;
        }
        int y = main.getHighestBlockYAt(getCenterX(), getCenterZ()) + blocksAboveGround;
        if (y > 253) { y = 253; }
        Material material = (destroy ? Material.AIR : Material.BARRIER);
        for (int x = centerX - lobbyRadius; x <= centerX + lobbyRadius; x++) {
            for (int z = centerZ - lobbyRadius; z <= centerZ + lobbyRadius; z++) {
                main.getBlockAt(x, y, z).setType(material, true);
                if (Math.abs(x - centerX) == lobbyRadius || Math.abs(z - centerZ) == lobbyRadius) {
                    main.getBlockAt(x, y+1, z).setType(material, true);
                    main.getBlockAt(x, y+2, z).setType(material, true);
                }
            }
        }
        //Set spawnpoint and spawn radius
        if (!destroy) {
            main.setSpawnLocation(centerX, y + 1, centerZ);
            main.setGameRule(GameRule.SPAWN_RADIUS, 0);
        } else {
            main.setSpawnLocation(centerX, y - blocksAboveGround, centerZ);
            main.setGameRule(GameRule.SPAWN_RADIUS, 10);
        }
    }

    @Override
    protected boolean init() {
        initTime = System.currentTimeMillis();
        TimerTask.clearInstanceTimers(this);
        ListenerTask.clearInstanceListeners(this);
        World main = Bukkit.getWorld(mainWorldUID);
        if (main == null) {
            getUtils().log(Logger.Level.WARNING, this.getClass(), "No main world set on init, aborting start");
            getUtils().broadcastChatSound(ChatColor.RED + "Error initiating game", Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F);
            getUtils().msgManagers(ChatColor.RED + "INIT ERR: No main world set");
            return false;
        }
        clearLobby();
        getAllPlayers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(p -> p.addPotionEffect(
                new PotionEffect(PotionEffectType.LEVITATION, 1, 255, true, false, false), true
        ));
        try {
            spreadPlayers();
        } catch (SpreadPlayersException e) {
            new GameUtils(this).log(Logger.Level.INFO, this.getClass(), "Failed to spread players: " + e.getLocalizedMessage());
        }
        return true;
    }

    @Override
    protected boolean start() {
        startTime = System.currentTimeMillis();
        //Clear tasks to reset behavior
        ListenerTask.clearInstanceListeners(this);
        TimerTask.clearInstanceTimers(this);
        Set<UUID> worlds = new HashSet<>(getOtherWorlds());
        worlds.add(mainWorldUID);
        worlds.stream().map(Bukkit::getWorld).filter(Objects::nonNull).forEach(this::startWorld);
        new GameStartAnnouncer(this).init();
        return true;
    }

    private void startTasks() {
        //TODO:
        // Set border shrink timer
        UHC_BorderTask borderTask = new UHC_BorderTask(this, timeToShrink);
        borderTask.init();
        new UHC_ActiveListener(this, borderTask).init();

    }

    @Override
    protected boolean pause() {
        //TODO
        return true;
    }

    @Override
    protected boolean resume() {
        //TODO
        return true;
    }

    @Override
    protected boolean end() {
        //TODO
        return true;
    }

    /**
     * Calculates random places to spread players and sets initial number of players
     * @throws SpreadPlayersException if the players could not be spread
     */
    protected void spreadPlayers() throws SpreadPlayersException {
        LinkedList<Location> locations = new LinkedList<>();

        double avgDistance = setLocations(locations);

        setInitNumPlayers(getAlivePlayers().size());

        new GameUtils(this).log(Logger.Level.INFO, this.getClass(),
                String.format("Successfully spread %d players around %s,%s | " +
                        "(Average distance between players is %s blocks)",
                locations.size(), getCenterX(), getCenterZ(), avgDistance));

        //TODO: Create new Runnable and schedule for countdown
        new UHC_SpreadPlayers(this, locations).init();
    }

    /**
     * Generate spread locations with the following parameters. The input list will be emptied to ensure that all locations meet the parameters.
     * @param locations List of locations to put locations in
     * @return Average distance between locations
     */
    protected double setLocations(LinkedList<Location> locations) {
        locations.clear();

        int numLocations = getAlivePlayers().size();
        int maxDistance = getInitRadius();
        int minDistance = getMinDistance();
        int minSeparation = getMinSeparation();

        Random rng = new Random();
        int maxRegenAttempts = 10000;

        for (int i = 0; i < numLocations; i++) {
            boolean success = false;
            Location loc;
            for (int attempt = 0; attempt < maxRegenAttempts; attempt++) {
                int x = rng.nextInt(2 * maxDistance + 1) - maxDistance;
                int z = rng.nextInt(2 * maxDistance + 1) - maxDistance;

                //Check min distance
                if (distance(x, z, 0, 0) < minDistance) {
                    continue;
                }

                //Check min separation
                boolean flag = false;
                for (Location l : locations) {
                    if (distance(x, z, l.getBlockX(), l.getBlockZ()) < minSeparation) {
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    continue;
                }

                //Add this location to the list
                World mainWorld = Bukkit.getWorld(this.getMainWorldUID());
                loc = new Location(mainWorld, x, mainWorld.getHighestBlockYAt(x, z), z);
                locations.addLast(loc);
                success = true;
            }

            //Check for success
            if (!success) {
                throw new SpreadPlayersException("Took too long to find a random location");
            }
        }

        //Calculate average distances to closest other location
        double sumMinDist = 0.0; //Sum minimum distances
        for (Location l : locations) {
            double minDist = Double.MAX_VALUE;
            for (Location l1 : locations) {
                if (l.equals(l1)) continue;
                minDist = Math.min(
                        distance(l1.getBlockX(), l1.getBlockZ(), l.getBlockX(), l.getBlockZ()),
                        minDist
                );
            }
            sumMinDist += minDist;
        }

        return sumMinDist / locations.size();
    }

    public class SpreadPlayersException extends RuntimeException {
        public SpreadPlayersException(String msg) {
            super(msg);
        }
    }

    private double distance(int x1, int z1, int x2, int z2) {
        return Math.pow(
                Math.pow(x1 - x2, 2) + Math.pow(z1 - z2, 2),
                0.5
        );
    }
}
