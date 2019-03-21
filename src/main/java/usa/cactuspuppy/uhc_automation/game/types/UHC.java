package usa.cactuspuppy.uhc_automation.game.types;

import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.entity.tasks.timers.UHC_SpreadPlayers;
import usa.cactuspuppy.uhc_automation.utils.Logger;
import usa.cactuspuppy.uhc_automation.utils.UHCUtils;

import java.io.Serializable;
import java.util.*;

@Getter
public class UHC extends GameInstance implements Serializable {
    //[=== PLAY AREA INFO ===]
    /**
     * Time to delay border shrinking, in seconds.
     * -1 disables border shrinking entirely
     */
    @Setter(AccessLevel.PUBLIC)
    protected int timeToShrink;

    @Setter(AccessLevel.PUBLIC)
    protected int centerX;

    @Setter(AccessLevel.PUBLIC)
    protected int centerZ;

    /**
     * Radius of the initial play space, in blocks from 0, 0 (0.5, 0.5)
     */
    @Setter(AccessLevel.PUBLIC)
    protected int initRadius;

    @Setter(AccessLevel.PUBLIC)
    protected int minDistance;

    @Setter(AccessLevel.PUBLIC)
    protected int minSeparation;

    // [=== EPISODE INFO ===]
    /**
     * Length of episodes, in seconds. -1 to disable.
     */
    protected long epLength;

    public UHC(World world) {
        super(world);
    }

    //TODO: Implement methods
    @Override
    protected void reset() {

    }

    @Override
    protected void init() {
        try {
            spreadPlayers();
        } catch (SpreadPlayersException e) {
            new UHCUtils(this).log(Logger.Level.INFO, this.getClass(), "Failed to spread players");
        }
        //TODO: Remove lobby
        //TODO: Set gamerules
        //TODO: Begin start countdown
    }

    @Override
    protected void start() {

    }

    @Override
    protected void pause() {

    }

    @Override
    protected void resume() {

    }

    @Override
    protected void end() {

    }

    /**
     * Calculates random places to spread players and sets initial number of players
     * @throws SpreadPlayersException if the players could not be spread
     */
    protected void spreadPlayers() throws SpreadPlayersException {
        LinkedList<Location> locations = new LinkedList<>();

        double avgDistance = setLocations(locations);

        setInitNumPlayers(getAlivePlayers().size());

        new UHCUtils(this).log(Logger.Level.INFO, this.getClass(),
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
                World mainWorld = Bukkit.getWorld(getMainWorld());
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
