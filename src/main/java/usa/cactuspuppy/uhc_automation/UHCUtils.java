package usa.cactuspuppy.uhc_automation;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.logging.Level;

import static java.util.stream.Collectors.toList;

public class UHCUtils {
    /**
     * Assistance with phrases provided by: Djinnjurr, krohn67, daniodle, Aurabeth, Remuko, Wyntr, DreDeveraux
     */
    public static String[] START_MSGS =
            {"Live and let die!", "Go for broke!", "Ready Player One!", "No time like the present!",
                    "This ought to be a match to remember!", "Eliminate Other Players", "Capture Objective A", "Triumph or Die!",
                    "Hack and Slash!", "Lives Remaining: 1", "Tear them apart!", "May the odds be ever in your favor!",
                    "Once more unto the breach", "Go Get 'Em, Tiger!", "Time to kick the tires and light the fires!", "Git 'er Done!",
                    "Alea Iacta Est", "Cry havoc and let slip the dogs of war", "Let Slip the Dogs of War!", "Veni, vidi, vici.",
                    "Oooh hoo hoo hoo... this'll be good!", "Fire In The Hole!", "Fire at will!", "Fight for the Assassins!",
                    "Kill or be killed!", "THEY SHALL NOT PASS", "If anyone is gonna win, it's gonna be you.",
                    "Who's ready for some fireworks!", "LEEEROY JENKINS", "[inspirational message]", "Release the hounds!",
                    "Good luck, don't die!", "Good luck, have fun!", "Just Do It!", "FOR THE HORDE!", "For The Alliance!", "Watch out for bears!",
                    "How do YOU want to do this?", "Know yourself, know thy enemy, and you shall win.", "For Aiur!",
                    "One Punch is all you need!", "Roll for Initiative!", "You know you have to do it to 'em", "Watch out for boars!", "CHAAAAAAARGE", "Ready for Battle!"};

    private static final Random random = new Random();

    //do not instantiate
    public UHCUtils() { }

    public static void broadcastMessage(GameInstance gi, String msg) {
        for (UUID u : gi.activePlayers) {
            Bukkit.getPlayer(u).sendMessage(msg);
        }
    }

    public static void broadcastMessagewithTitle(GameInstance gi, String chat, String title, String subtitle, int in, int stay, int out) {
        for (UUID u: gi.activePlayers) {
            Player p = Bukkit.getPlayer(u);
            p.sendMessage(chat);
            p.sendTitle(title, subtitle, in, stay, out);
        }
    }

    public static void broadcastMessagewithSound(GameInstance gi, String chat, String sound, float volume, float pitch) {
        for (UUID u: gi.activePlayers) {
            Player p = Bukkit.getPlayer(u);
            p.sendMessage(chat);
            p.playSound(p.getLocation(), sound, volume, pitch);
        }
    }

    public static void broadcastMessagewithSoundandTitle(GameInstance gi, String chat, String title, String subtitle, int in, int stay, int out, String sound, float volume, float pitch) {
        for (UUID u: gi.activePlayers) {
            Player p = Bukkit.getPlayer(u);
            p.sendMessage(chat);
            p.sendTitle(title, subtitle, in, stay, out);
            p.playSound(p.getLocation(), sound, volume, pitch);
        }
    }

    public static boolean isWorldData(Main m) {
        String location = m.getDataFolder() + "/" + m.getConfig().getString("data-location").replaceAll("<worldname>", m.getConfig().getString("world"));
        File locFolder = new File(location);
        return locFolder.exists();
    }

    public static long loadStartTime(Main m) {
        String location = m.getDataFolder() + "/" + m.getConfig().getString("data-location").replaceAll("<worldname>", m.getConfig().getString("world"));
        File locFolder = new File(location);
        if (!locFolder.exists()) {
            m.getLogger().severe("The data folder has mysteriously vanished!");
        } else {
            m.getLogger().info("Found data folder '" + location + "', proceeding to try to extract start time");
        }
        String startTimeFileName = location + "/startTime.txt";
        File startTimeFile = new File(startTimeFileName);
        if (startTimeFile.isFile()) {
            try {
                FileReader startTFileR = new FileReader(startTimeFileName);
                BufferedReader startTBuffR = new BufferedReader(startTFileR);
                String line = startTBuffR.readLine();
                return Long.valueOf(line);
            } catch (IOException e) {
                m.getLogger().warning("Could not load start time from '" + startTimeFileName + "', using current time as start time instead.");
                return System.currentTimeMillis();
            }
        } else {
            m.getLogger().warning("Could not find start time file, using current time as start time instead.");
            return System.currentTimeMillis();
        }
    }

    public static void saveStartTime(Main m, long sT) {
        String location =  m.getDataFolder() + "/" + m.getConfig().getString("data-location").replaceAll("<worldname>", m.getConfig().getString("world"));
        File dataFolder = new File(location);
        if (!dataFolder.exists()) {
            m.getLogger().info("Could not find world data folder '" + location + "', creating...");
            boolean created = dataFolder.mkdirs();
            if (!created) {
                m.getLogger().severe("Could not create data folder to save game instance data!");
                return;
            } else {
                m.getLogger().info("Created world data folder: '" + location + "', saving data...");
            }
        }
        String startTimeFileName = location + "/startTime.txt";
        File startTimeFile = new File(startTimeFileName);
        if (startTimeFile.isFile()) {
            final boolean delete = startTimeFile.delete();
            if (!delete) {
                m.getLogger().severe("Could not delete " + startTimeFileName);
            }
        }
        try {
            FileWriter sTFW = new FileWriter(startTimeFileName);
            BufferedWriter sTBW = new BufferedWriter(sTFW);

            sTBW.write(String.valueOf(sT));
            sTBW.close();
        } catch (IOException e) {
            m.getLogger().severe("Could not save start time to '" + startTimeFileName + "'!");
        }
    }

    public static Map<String, Set<UUID>> loadWorldPlayers(Main m) {
        Map<String, Set<UUID>> rv = new HashMap<>();
        String location = m.getDataFolder() + "/" + m.getConfig().getString("data-location").replaceAll("<worldname>", m.getConfig().getString("world"));
        File locFolder = new File(location);
        if (!locFolder.exists()) {
            m.getLogger().info("Attempting to make directory: " + location);
            boolean created = locFolder.mkdirs();
            if (!created) {
                m.getLogger().severe("Could not create data folder: " + location);
            } else {
                m.getLogger().info("Created world data folder: " + location);
            }
            return rv;
        } else {
            m.getLogger().info("Found data folder '" + location + "', proceeding to try to extract UUID sets");
        }
        String lPFileName = location + "/livePlayers.txt";
        String aPFileName = location + "/activePlayers.txt";
        String line;
        //Live Players read-in
        try {
            File lPFile = new File(lPFileName);
            if (lPFile.isFile()) {
                FileReader lPFileR = new FileReader(lPFileName);
                BufferedReader lPBuffR = new BufferedReader(lPFileR);
                Set<UUID> livePlayers = new HashSet<>();
                while ((line = lPBuffR.readLine()) != null) {
                    livePlayers.add(UUID.fromString(line));
                }
                lPBuffR.close();
                rv.put("livePlayers", livePlayers);
                m.getLogger().info("Successfully extracted livePlayers UUID set");
            } else {
                m.getLogger().info("Game data missing! Cleansing directory.");
                FileUtils.cleanDirectory(new File(location));
                return new HashMap<>();
            }
        } catch (IOException e) {
            m.getLogger().severe("Error while loading in livePlayers file: " + lPFileName + ", cleansing directory!");
            try {
                FileUtils.cleanDirectory(new File(location));
            } catch (IOException f) {
                f.printStackTrace();
            }
            return new HashMap<>();
        }
        //All Players read-in
        try {
            File aPFile = new File(aPFileName);
            if (aPFile.isFile()) {
                FileReader aPFileR = new FileReader(aPFileName);
                BufferedReader aPBuffR = new BufferedReader(aPFileR);
                Set<UUID> allPlayers = new HashSet<>();
                while ((line = aPBuffR.readLine()) != null) {
                    allPlayers.add(UUID.fromString(line));
                }
                aPBuffR.close();
                rv.put("activePlayers", allPlayers);
                m.getLogger().info("Successfully extracted activePlayers UUID set");
            } else {
                m.getLogger().info("Could not find activePlayers UUID set, cleansing directory");
                FileUtils.cleanDirectory(new File(location));
                return new HashMap<>();
            }
        } catch (IOException e) {
            m.getLogger().severe("Error while loading in activePlayers file: " + aPFileName + ", cleansing directory!");
            try {
                FileUtils.cleanDirectory(new File(location));
            } catch (IOException f) {
                f.printStackTrace();
            }
            return new HashMap<>();
        }
        return rv;
    }

    public static void clearWorldData(Main m) {
        String location =  m.getDataFolder() + "/" + m.getConfig().getString("data-location").replaceAll("<worldname>", m.getConfig().getString("world"));
        m.getLogger().info("Deleting world data folder: " + location);
        try {
            FileUtils.deleteDirectory(new File(location));
        } catch (IOException e) {
            m.getLogger().severe("Could not delete world data folder '" + location + "'!");
        }
    }

    public static void saveWorldPlayers(Main m, Set<UUID> livePlayers, Set<UUID> allPlayers) {
        String location =  m.getDataFolder() + "/" + m.getConfig().getString("data-location").replaceAll("<worldname>", m.getConfig().getString("world"));
        File dataFolder = new File(location);
        if (!dataFolder.exists()) {
            m.getLogger().info("Could not find world data folder '" + location + "', creating...");
            boolean created = dataFolder.mkdirs();
            if (!created) {
                m.getLogger().severe("Could not create data folder to save game instance data!");
                return;
            } else {
                m.getLogger().info("Created world data folder: '" + location + "', savind data...");
            }
        }
        String livePlayersName = location + "/livePlayers.txt";
        String allPlayersName = location + "/activePlayers.txt";
        File lPFile = new File(livePlayersName);
        File aPFile = new File(allPlayersName);
        if (lPFile.isFile()) {
            lPFile.delete();
        }
        if (aPFile.isFile()) {
            aPFile.delete();
        }
        //Live Players save
        try {
            FileWriter lPFileW = new FileWriter(livePlayersName);
            BufferedWriter lPBuffW = new BufferedWriter(lPFileW);

            for (UUID u : livePlayers) {
                lPBuffW.write(u.toString());
                lPBuffW.newLine();
            }

            lPBuffW.close();
        } catch (IOException e) {
            m.getLogger().severe("Could not save livePlayers to file " + livePlayersName);
        }
        //All Players save
        try {
            FileWriter aPFileW = new FileWriter(allPlayersName);
            BufferedWriter aPBuffW = new BufferedWriter(aPFileW);

            for (UUID u : allPlayers) {
                aPBuffW.write(u.toString());
                aPBuffW.newLine();
            }

            aPBuffW.close();
        } catch (IOException e) {
            m.getLogger().severe("Could not save activePlayers to file " + allPlayersName);
        }
        m.getLogger().info("Saved player data to world data folder: " + location);
    }

    public static World getWorldFromString(Main m, Server s, String name) {
        for (World w : s.getWorlds()) {
            if (w.getName().equals(name)) {
                return w;
            }
        }
        World w = s.getWorlds().get(0);
        m.getLogger().log(Level.WARNING, "Could not find world " + name
                + ". Defaulting to world " + w.getName());
        m.getConfig().set("world", w.getName());
        m.saveConfig();
        return w;
    }

    public static boolean worldEqualsExt(World in, World check) {
        String inName = in.getName();
        String checkName = check.getName();
        String checkNether = checkName + "_nether";
        String checkEnd = checkName + "_the_end";
        return inName.equals(checkName) || inName.equals(checkNether) || inName.equals(checkEnd);
    }

    public static void exeCmd(Server s, World w, String cmd) {
        /*s.getLogger().log(Level.INFO, "Attempting to run command: '" + cmd + "'");
        MinecraftServer nmsServer = ((CraftServer) s).getServer();
        WorldServer nmsWorld = ((CraftWorld) w).getHandle();
        EntityPlayer ep = new EntityPlayer(nmsServer, nmsWorld, new GameProfile(UUID.randomUUID(), "UHC_Automation"), new PlayerInteractManager(nmsWorld));
        ep.setLocation(0, 0, 0, 0, 0);
        CraftPlayer d = new CraftPlayer((CraftServer) s, ep);
        d.setOp(true);
        try {
           d.performCommand(cmd);
        } catch (CommandException e) { }*/
        s.dispatchCommand(s.getConsoleSender(), cmd);
    }

    public static void exeCmd(String cmd) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
    }

    public static void sendActionBar(Player player, String message){
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    public static String randomStartMSG() {
        Random r = new Random();
        int msg = r.nextInt(START_MSGS.length);
        return START_MSGS[msg];
    }

    public static boolean validUsername(String name) {
        if (name.length() < 3 || name.length() > 16) {
            return false;
        }
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || (c == '_'))) {
                return false;
            }
        }
        return true;
    }

    public static boolean spreadplayers(GameInstance g) {
        double range = g.getInitSize() / 2;
        boolean teams = g.teamMode;

        //May be allowed to be changed in future, TBD
        int x = 0;
        int z = 0;

        final double xRangeMin = x - range;
        final double zRangeMin = z - range;
        final double xRangeMax = x + range;
        final double zRangeMax = z + range;

        List<Player> players = g.livePlayers.stream().map(Bukkit::getPlayer).collect(toList());

        final int spreadSize = teams ? getTeams(players) : players.size();

        final Location[] locations = getSpreadLocations(g.getWorld(), spreadSize, xRangeMin, zRangeMin, xRangeMax, zRangeMax);
        final int rangeSpread = range(g.getWorld(), g.spreadDistance, xRangeMin, zRangeMin, xRangeMax, zRangeMax, locations);

        if (rangeSpread == -1) {
            return false;
        }
        final double distanceSpread = spread(g.getWorld(), players, locations, teams, g);

        Bukkit.getLogger().info(String.format("Succesfully spread %d %s around %s,%s", locations.length, teams ? "teams" : "players", 0, 0));
        if (locations.length > 1) {
            Bukkit.getLogger().info(String.format("(Average distance between %s is %s blocks apart after %s iterations)", teams ? "teams" : "players",  String.format("%.2f", distanceSpread), rangeSpread));
        }
        return true;
    }

    /**
     * Helper Functions for spreadplayers
     * @source: https://github.com/Attano/Spigot-1.8/blob/9db48bc15e203179554b8d992ca6b0a528c8d300/org/bukkit/command/defaults/SpreadPlayersCommand.java
     */
    static int range(World world, double distance, double xRangeMin, double zRangeMin, double xRangeMax, double zRangeMax, Location[] locations) {
        boolean flag = true;
        double max;

        int i;

        for (i = 0; i < 10000 && flag; ++i) {
            flag = false;
            max = Float.MAX_VALUE;

            Location loc1;
            int j;

            for (int k = 0; k < locations.length; ++k) {
                Location loc2 = locations[k];

                j = 0;
                loc1 = new Location(world, 0, 0, 0);

                for (int l = 0; l < locations.length; ++l) {
                    if (k != l) {
                        Location loc3 = locations[l];
                        double dis = loc2.distanceSquared(loc3);

                        max = Math.min(dis, max);
                        if (dis < distance) {
                            ++j;
                            loc1.add(loc3.getX() - loc2.getX(), 0, 0);
                            loc1.add(loc3.getZ() - loc2.getZ(), 0, 0);
                        }
                    }
                }

                if (j > 0) {
                    loc2.setX(loc2.getX() / j);
                    loc2.setZ(loc2.getZ() / j);
                    double d7 = Math.sqrt(loc1.getX() * loc1.getX() + loc1.getZ() * loc1.getZ());

                    if (d7 > 0.0D) {
                        loc1.setX(loc1.getX() / d7);
                        loc2.add(-loc1.getX(), 0, -loc1.getZ());
                    } else {
                        double x = xRangeMin >= xRangeMax ? xRangeMin : random.nextDouble() * (xRangeMax - xRangeMin) + xRangeMin;
                        double z = zRangeMin >= zRangeMax ? zRangeMin : random.nextDouble() * (zRangeMax - zRangeMin) + zRangeMin;
                        loc2.setX(x);
                        loc2.setZ(z);
                    }

                    flag = true;
                }

                boolean swap = false;

                if (loc2.getX() < xRangeMin) {
                    loc2.setX(xRangeMin);
                    swap = true;
                } else if (loc2.getX() > xRangeMax) {
                    loc2.setX(xRangeMax);
                    swap = true;
                }

                if (loc2.getZ() < zRangeMin) {
                    loc2.setZ(zRangeMin);
                    swap = true;
                } else if (loc2.getZ() > zRangeMax) {
                    loc2.setZ(zRangeMax);
                    swap = true;
                }
                if (swap) {
                    flag = true;
                }
            }

            if (!flag) {
                Location[] locs = locations;
                int i1 = locations.length;

                for (j = 0; j < i1; ++j) {
                    loc1 = locs[j];
                    if (world.getHighestBlockYAt(loc1) == 0) {
                        double x = xRangeMin >= xRangeMax ? xRangeMin : random.nextDouble() * (xRangeMax - xRangeMin) + xRangeMin;
                        double z = zRangeMin >= zRangeMax ? zRangeMin : random.nextDouble() * (zRangeMax - zRangeMin) + zRangeMin;
                        locations[i] = (new Location(world, x, 0, z));
                        loc1.setX(x);
                        loc1.setZ(z);
                        flag = true;
                    }
                }
            }
        }

        if (i >= 10000) {
            return -1;
        } else {
            return i;
        }
    }

    @SuppressWarnings("deprecation")
    private static double spread(World world, List<Player> list, Location[] locations, boolean teams, GameInstance g) {
        double distance = 0.0D;
        int i = 0;
        Map<Team, Location> hashmap = Maps.newHashMap();
        g.giveBoats = new HashSet<>();

        for (int j = 0; j < list.size(); ++j) {
            Player player = list.get(j);
            Location location;

            if (teams) {
                Team team = player.getScoreboard().getPlayerTeam(player);

                if (!hashmap.containsKey(team)) {
                    hashmap.put(team, locations[i++]);
                }

                location = hashmap.get(team);
            } else {
                location = locations[i++];
            }

            Location loc = new Location(world, Math.floor(location.getX()) + 0.5D, world.getHighestBlockYAt((int) location.getX(), (int) location.getZ()) - 1, Math.floor(location.getZ()) + 0.5D);
            Material m = world.getBlockAt(loc).getType();
            if (m.equals(Material.WATER) || m.equals(Material.STATIONARY_WATER)) {
                loc.setY(loc.getY() + 1D);
                loc.getBlock().setType(Material.STEP);
                if (isOceanBiome(world.getBiome(loc.getBlockX(), loc.getBlockZ()))) {
                    g.giveBoats.add(player);
                }
            }
            loc.setY(loc.getY() + 1D);
            player.teleport(loc);

            double value = Double.MAX_VALUE;

            for (int k = 0; k < locations.length; ++k) {
                if (location != locations[k]) {
                    double d = location.distanceSquared(locations[k]);
                    value = Math.min(d, value);
                }
            }

            distance += value;
        }

        distance /= list.size();
        return distance;
    }

    @SuppressWarnings("deprecation")
    private static int getTeams(List<Player> players) {
        Set<Team> teams = Sets.newHashSet();

        for (Player player : players) {
            teams.add(player.getScoreboard().getPlayerTeam(player));
        }

        return teams.size();
    }

    private static Location[] getSpreadLocations(World world, int size, double xRangeMin, double zRangeMin, double xRangeMax, double zRangeMax) {
        Location[] locations = new Location[size];

        for (int i = 0; i < size; ++i) {
            double x = xRangeMin >= xRangeMax ? xRangeMin : random.nextDouble() * (xRangeMax - xRangeMin) + xRangeMin;
            double z = zRangeMin >= zRangeMax ? zRangeMin : random.nextDouble() * (zRangeMax - zRangeMin) + zRangeMin;
            locations[i] = (new Location(world, x, 0, z));
        }

        return locations;
    }

    private static boolean isOceanBiome(Biome biome) {
        return biome.equals(Biome.OCEAN) || biome.equals(Biome.DEEP_OCEAN) || biome.equals(Biome.FROZEN_OCEAN);
    }

    public static Map<String, Integer> secsToHMS(int secs) {
        Map<String, Integer> hms = new HashMap<>();

        hms.put("hrs", secs / 3600);
        hms.put("mins", (secs / 60) % 60);
        hms.put("secs", secs % 60);

        return hms;
    }

    public static String secsToFormatString(int secs) {
        Map<String, Integer> hms = secsToHMS(secs);
        return hmsToFormatString(hms.get("hrs"), hms.get("mins"), hms.get("secs"));
    }

    public static String hmsToFormatString(int hrs, int mins, int secs) {
        assert hrs >= 0 && mins >= 0 && secs >= 0;

        if (hrs == 0 && mins == 0 && secs == 0) {
            return "0 seconds";
        }

        StringJoiner fmtStng = new StringJoiner(" ");
        if (hrs == 1) {
            fmtStng.add(hrs + " hour");
        } else if (hrs != 0) {
            fmtStng.add(hrs + " hours");
        }

        if (mins == 1) {
            fmtStng.add(mins + " minute");
        } else if (mins != 0) {
            fmtStng.add(mins + " minutes");
        }

        if (secs == 1) {
            fmtStng.add(secs + " second");
        } else if (secs != 0) {
            fmtStng.add(secs + " seconds");
        }

        return fmtStng.toString();
    }
}
