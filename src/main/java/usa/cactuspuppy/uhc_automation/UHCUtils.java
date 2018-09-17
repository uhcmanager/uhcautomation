package usa.cactuspuppy.uhc_automation;

import com.google.common.collect.Maps;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import usa.cactuspuppy.uhc_automation.Tasks.BorderCountdown;
import usa.cactuspuppy.uhc_automation.Tasks.PVPEnableCountdown;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

import static java.util.stream.Collectors.toList;

public class UHCUtils {
    /**
     * Assistance with phrases provided by: Djinnjurr, krohn67, daniodle, Aurabeth, Remuko, Wyntr, DreDeveraux
     */
    public static String[] START_MSGS =
            {"Live and let die!", "Go for broke!", "Ready Player One!", "No time like the present!",
                    "This ought to be a match to remember!", "Eliminate Other Players", "Capture Objective A", "Triumph or Die!",
                    "Hack and Slash!", "Lives Remaining: 0", "Tear them apart!", "May the odds be ever in your favor!",
                    "Once more unto the breach", "Go Get 'Em, Tiger!", "Time to kick the tires and light the fires!", "Git 'er Done!",
                    "Alea Iacta Est", "Cry havoc and let slip the dogs of war", "Let Slip the Dogs of War!", "Veni, vidi, vici.",
                    "Oooh hoo hoo hoo... this'll be good!", "Fire In The Hole!", "Fire at will!", "Fight for the Assassins!",
                    "Kill or be killed!", "THEY SHALL NOT PASS", "If anyone is gonna win, it's gonna be you.",
                    "Who's ready for some fireworks!", "LEEEROY JENKINS", "Release the hounds!",
                    "Good luck, don't die!", "Good luck, have fun!", "Just Do It!", "FOR THE HORDE!", "For The Alliance!", "Watch out for bears!",
                    "How do YOU want to do this?", "Know yourself, know thy enemy, and you shall win.", "For Aiur!",
                    "One Punch is all you need!", "Roll for Initiative!", "You know you have to do it to 'em", "Watch out for boars!", "CHAAAAAAARGE",
                    "Ready for Battle!", "Ready for Combat!", "D.Va, ready for combat!", "Fight!"};

    private static final Random random = new Random();

    //do not instantiate
    public UHCUtils() { }

    public static void broadcastMessage(GameInstance gi, String msg) {
        for (UUID u : gi.getActivePlayers()) {
            Bukkit.getPlayer(u).sendMessage(msg);
        }
    }

    public static void broadcastMessage(String msg) {
        broadcastMessage(Main.getInstance().getGameInstance(), msg);
    }

    public static void broadcastMessagewithTitle(GameInstance gi, String chat, String title, String subtitle, int in, int stay, int out) {
        for (UUID u: gi.getActivePlayers()) {
            Player p = Bukkit.getPlayer(u);
            p.sendMessage(chat);
            p.sendTitle(title, subtitle, in, stay, out);
        }
    }

    public static void broadcastMessagewithSound(GameInstance gi, String chat, String sound, float volume, float pitch) {
        for (UUID u: gi.getActivePlayers()) {
            Player p = Bukkit.getPlayer(u);
            p.sendMessage(chat);
            p.playSound(p.getLocation(), sound, volume, pitch);
        }
    }

    public static void broadcastMessagewithSoundandTitle(GameInstance gi, String chat, String title, String subtitle, int in, int stay, int out, String sound, float volume, float pitch) {
        for (UUID u: gi.getActivePlayers()) {
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

    public static Map<String, Object> loadAuxData(Main m) {
        Map<String, Object> rv = new HashMap<>();
        String location = m.getDataFolder() + "/" + m.getConfig().getString("data-location").replaceAll("<worldname>", m.getConfig().getString("world"));
        File locFolder = new File(location);
        if (!locFolder.exists()) {
            m.getLogger().severe("The data folder has mysteriously vanished!");
        } else {
            m.getLogger().info("Found data folder '" + location + "', proceeding to try to extract auxiliary data");
        }
        String auxDataName = location + "/auxData.txt";
        File auxDataFile = new File(auxDataName);
        if (auxDataFile.isFile()) {
            try {
                FileReader auxDataFileR = new FileReader(auxDataName);
                BufferedReader auxDataBuffR = new BufferedReader(auxDataFileR);
                String line = auxDataBuffR.readLine();
                rv.put("sT", Long.valueOf(line));
                line = auxDataBuffR.readLine();
                rv.put("teamMode", Boolean.valueOf(line));
                return rv;
            } catch (IOException e) {
                m.getLogger().warning("Could not load auxiliary data from '" + auxDataName + "'. Clearing world data.");
                return new HashMap<>();
            }
        } else {
            m.getLogger().warning("Could not find auxiliary data. Clearing worlc data...");
            clearWorldData(m);
            return new HashMap<>();
        }
    }

    public static void saveAuxData(Main m) {
        String location =  m.getDataFolder() + "/" + m.getConfig().getString("data-location").replaceAll("<worldname>", m.getConfig().getString("world"));
        File dataFolder = new File(location);
        if (!checkForDataFolder(m, location, dataFolder)) return;
        String auxDataName = location + "/auxData.txt";
        File auxDataFile = new File(auxDataName);
        if (auxDataFile.isFile()) {
            final boolean delete = auxDataFile.delete();
            if (!delete) {
                m.getLogger().severe("Could not delete existing aux data file: " + auxDataName);
            }
        }
        try {
            FileWriter aDFW = new FileWriter(auxDataName);
            BufferedWriter aDBW = new BufferedWriter(aDFW);

            aDBW.write(String.valueOf(m.getGameInstance().getStartT()));
            aDBW.newLine();
            aDBW.write(String.valueOf(m.getGameInstance().isTeamMode()));
            aDBW.close();
        } catch (IOException e) {
            m.getLogger().severe("Could not save aux data to '" + auxDataName + "'!");
        }
    }

    private static boolean checkForDataFolder(Main m, String location, File dataFolder) {
        if (!dataFolder.exists()) {
            m.getLogger().info("Could not find world data folder '" + location + "', creating...");
            boolean created = dataFolder.mkdirs();
            if (!created) {
                m.getLogger().severe("Could not create data folder to save game instance data!");
                return false;
            } else {
                m.getLogger().info("Created world data folder: '" + location + "', saving data...");
            }
        }
        return true;
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
        String rPFileName = location + "/regPlayers.txt";
        String bPFileName = location + "/blacklistPlayers.txt";
        String line;
        //Reg Players read-in
        try {
            File lPFile = new File(rPFileName);
            if (lPFile.isFile()) {
                FileReader rPFileR = new FileReader(rPFileName);
                BufferedReader rPBuffR = new BufferedReader(rPFileR);
                Set<UUID> regPlayers = new HashSet<>();
                while ((line = rPBuffR.readLine()) != null) {
                    regPlayers.add(UUID.fromString(line));
                }
                rPBuffR.close();
                rv.put("regPlayers", regPlayers);
                m.getLogger().info("Successfully extracted regPlayers UUID set");
            } else {
                m.getLogger().info("Could not find regPlayers UUID set, clearing world data...");
                clearWorldData(m);
                return new HashMap<>();
            }
        } catch (IOException e) {
            m.getLogger().severe("Error while loading in regPlayers file: " + rPFileName + "! Clearing world data...");
            clearWorldData(m);
            return new HashMap<>();
        }
        //Blacklist Players read-in
        try {
            File bPFile = new File(bPFileName);
            if (bPFile.isFile()) {
                FileReader bPFileR = new FileReader(bPFileName);
                BufferedReader bPBuffR = new BufferedReader(bPFileR);
                Set<UUID> blacklistPlayers = new HashSet<>();
                while ((line = bPBuffR.readLine()) != null) {
                    blacklistPlayers.add(UUID.fromString(line));
                }
                bPBuffR.close();
                rv.put("blacklistPlayers", blacklistPlayers);
                m.getLogger().info("Successfully extracted blacklistPlayers UUID set");
            } else {
                m.getLogger().info("Could not find blacklistPlayers UUID set. Clearing world data...");
                clearWorldData(m);
                return new HashMap<>();
            }
        } catch (IOException e) {
            m.getLogger().severe("Error while loading in blacklistPlayers file: " + bPFileName + "! clearing world data...");
            clearWorldData(m);
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
            e.printStackTrace();
        }
    }

    public static void saveWorldPlayers(Main m) {
        Set<UUID> regPlayers = m.getGameInstance().getRegPlayers();
        Set<UUID> blacklistPlayers = m.getGameInstance().getBlacklistPlayers();
        String location =  m.getDataFolder() + "/" + m.getConfig().getString("data-location").replaceAll("<worldname>", m.getConfig().getString("world"));
        File dataFolder = new File(location);
        if (!checkForDataFolder(m, location, dataFolder)) return;
        String regPlayersName = location + "/regPlayers.txt";
        String blacklistPlayersName = location + "/blacklistPlayers.txt";
        File rPFile = new File(regPlayersName);
        File bPFile = new File(blacklistPlayersName);
        if (rPFile.isFile()) {
            if (!rPFile.delete()) m.getLogger().severe("Could not clear old player files");
        }
        if (bPFile.isFile()) {
            if (!bPFile.delete()) m.getLogger().severe("Could not clear old player files");
        }
        //Reg Players save
        try {
            FileWriter rPFileW = new FileWriter(regPlayersName);
            BufferedWriter rPBuffW = new BufferedWriter(rPFileW);

            for (UUID u : regPlayers) {
                rPBuffW.write(u.toString());
                rPBuffW.newLine();
            }

            rPBuffW.close();
        } catch (IOException e) {
            m.getLogger().severe("Could not save regPlayers to file " + regPlayersName);
        }
        //Blacklist Players save
        try {
            FileWriter bPFileW = new FileWriter(blacklistPlayersName);
            BufferedWriter bPBuffW = new BufferedWriter(bPFileW);

            for (UUID u : blacklistPlayers) {
                bPBuffW.write(u.toString());
                bPBuffW.newLine();
            }

            bPBuffW.close();
        } catch (IOException e) {
            m.getLogger().severe("Could not save blacklistPlayers to file " + blacklistPlayersName);
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
        double range = g.getInitSize() / 2.0;
        boolean teams = g.isTeamMode() && g.isRespectTeams();

        //May be allowed to be changed in future, TBD
        int x = 0;
        int z = 0;

        final double xRangeMin = x - range;
        final double zRangeMin = z - range;
        final double xRangeMax = x + range;
        final double zRangeMax = z + range;

        List<Player> players = g.getLivePlayers().stream().map(Bukkit::getPlayer).collect(toList());

        final int spreadSize = teams ? Main.getInstance().getGameInstance().getNumTeams() : players.size();

        final Location[] locations = getSpreadLocations(g.getWorld(), spreadSize, xRangeMin, zRangeMin, xRangeMax, zRangeMax);
        final int rangeSpread = range(g.getWorld(), g.getSpreadDistance(), xRangeMin, zRangeMin, xRangeMax, zRangeMax, locations);

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
    private static int range(World world, double distance, double xRangeMin, double zRangeMin, double xRangeMax, double zRangeMax, Location[] locations) {
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

    private static double spread(World world, List<Player> list, Location[] locations, boolean teams, GameInstance g) {
        double distance = 0.0D;
        int i = 0;
        Map<Team, Location> hashmap = Maps.newHashMap();
        g.setGiveBoats(new HashSet<>());

        for (Player player : list) {
            Location location;

            if (teams) {
                Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName());

                if (!hashmap.containsKey(team)) {
                    hashmap.put(team, locations[i++]);
                }

                location = hashmap.get(team);
            } else {
                location = locations[i++];
            }

            Location loc = new Location(world, Math.floor(location.getX()) + 0.5D, world.getHighestBlockYAt((int) location.getX(), (int) location.getZ()) - 1, Math.floor(location.getZ()) + 0.5D);
            Material m = world.getBlockAt(loc).getType();
            if (m.equals(Material.WATER)) {
                loc.setY(loc.getY() + 1D);
                loc.getBlock().setType(Material.STONE_BRICK_SLAB);
                if (isOceanBiome(world.getBiome(loc.getBlockX(), loc.getBlockZ()))) {
                    g.getGiveBoats().add(player);
                }
            }
            loc.setY(loc.getY() + 1D);
            player.teleport(loc);

            double value = Double.MAX_VALUE;

            for (Location location1 : locations) {
                if (location != location1) {
                    double d = location.distanceSquared(location1);
                    value = Math.min(d, value);
                }
            }

            distance += value;
        }

        distance /= list.size();
        return distance;
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

    public static boolean isOceanBiome(Biome biome) {
        return biome.name().toLowerCase().contains("ocean");
    }

    public static int getSecsElapsed(Main m) {
        long currTime = System.currentTimeMillis();
        if (!(m.getGameInstance().isStarted())) {
            return -1;
        }
        long timeElapsed = currTime - m.getGameInstance().getStartT();
        return (int) timeElapsed / 1000;
    }

    public static Map<String, Integer> secsToHMS(int secs) {
        return secsToHMS((long) secs);
    }

    public static Map<String, Integer> secsToHMS(long secs) {
        Map<String, Integer> hms = new HashMap<>();

        hms.put("hrs", (int) (secs / 3600));
        hms.put("mins", (int) ((secs / 60) % 60));
        hms.put("secs", (int) (secs % 60));

        return hms;
    }

    public static String secsToFormatString(int secs) {
        return secsToFormatString((long) secs);
    }

    public static String secsToFormatString(long secs) {
        Map<String, Integer> hms = secsToHMS(secs);
        return hmsToFormatString(hms.get("hrs"), hms.get("mins"), hms.get("secs"));
    }

    public static String hmsToFormatString(int hrs, int mins, int secs) {
        assert (hrs >= 0) && (mins >= 0) && (secs >= 0);

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

    public static String secsToFormatString2(int secs) {
        Map<String, Integer> hms = secsToHMS(secs);
        return hmsToFormatString2(hms.get("hrs"), hms.get("mins"), hms.get("secs"));
    }

    public static String hmsToFormatString2(int hrs, int mins, int secs) {
        assert hrs >= 0 && mins >= 0 && secs >= 0;

        return hrs + ":" + mins + "'" + secs +"\"";
    }

    public static void sendPlayerInfo(Main m, CommandSender commandSender) {
        int timeElapsedSecs = getSecsElapsed(m);
        long currTime = System.currentTimeMillis();
        if (timeElapsedSecs == -1) {
            commandSender.sendMessage(ChatColor.RED + "Game has not started yet!");
            return;
        }
        StringBuilder message = new StringBuilder();
        boolean pvp = m.getGameInstance().getWorld().getPVP();
        boolean border = m.getGameInstance().isBorderShrinking();
        message.append(ChatColor.GOLD).append(ChatColor.BOLD).append(ChatColor.UNDERLINE).append("\n").append(m.getConfig().getString("event-name", "CURRENT GAME")).append(":\n")
                .append(ChatColor.DARK_GREEN).append(ChatColor.BOLD).append("Time Elapsed: ").append(ChatColor.RESET).append(secsToFormatString(timeElapsedSecs))
                .append(ChatColor.AQUA).append(ChatColor.BOLD).append(m.getGameInstance().isTeamMode() ? "\nTeams Remaining: " : "\nPlayers Remaining: ").append(ChatColor.RESET).append(m.getGameInstance().isTeamMode() ? m.getGameInstance().getNumTeams() : m.getGameInstance().getLivePlayers().size());
        if (pvp) {
            message.append(ChatColor.DARK_RED).append(ChatColor.BOLD).append("\nPVP is Enabled");
        } else {
            long timeToPVP = PVPEnableCountdown.getInstance().getEnableTime() - currTime;
            message.append(ChatColor.YELLOW).append(ChatColor.BOLD).append("\nPVP Enabled in ").append(ChatColor.RESET).append(secsToFormatString(timeToPVP / 1000));
        }
        if (border) {
            message.append(ChatColor.DARK_PURPLE).append(ChatColor.BOLD).append("\nWorld border shrinking!");
        } else {
            long timeToBorder = BorderCountdown.getInstance().getEnd() - currTime;
            message.append(ChatColor.GREEN).append(ChatColor.BOLD).append("\nBorder begins shrinking in ").append(ChatColor.RESET).append(secsToFormatString(timeToBorder / 1000));
        }
        commandSender.sendMessage(message.toString());
    }

    public static String getRules(Main m) {
        String numDelimiter = ") ";
        String rulesTitlePrefix = "\n" + ChatColor.GOLD.toString() + ChatColor.BOLD.toString();
        String ruleNumPrefix = ChatColor.GREEN.toString();

        String rulesLocation = m.getDataFolder() + "/rules.txt";
        File rulesFile = new File(rulesLocation);
        if (!rulesFile.isFile()) {
            m.getLogger().info("Could not find rules file at: " + rulesLocation + ". Generating rules.txt for you now.");
            Main.getInstance().createRules();
        }
        String rulesTitle = rulesTitlePrefix + "Rules:";
        StringJoiner rules = new StringJoiner("\n");
        rules.add(rulesTitle);
        try {
            FileReader rulesFileR = new FileReader(rulesLocation);
            BufferedReader rulesBuffR = new BufferedReader(rulesFileR);
            String line;
            int ruleNum = 0;

            while ((line = rulesBuffR.readLine()) != null) {
                ruleNum++;
                rules.add(ruleNumPrefix + ruleNum + numDelimiter + ChatColor.RESET + line);
            }
            return rules.toString();
        } catch (IOException e) {
            m.getLogger().severe("Error while reading from rules file " + rulesLocation);
            e.printStackTrace();
            return "";
        }
    }

    public static void sendPluginInfo(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "\nUHC Automation by CactusPuppy\n"
                + "Version " + Main.getInstance().getDescription().getVersion() + "\n"
                + ChatColor.GREEN + "For command usage, type " + ChatColor.WHITE + ChatColor.ITALIC + "/uhc help");
    }

    public static void announcePlayerJoin(Player p) {
        UHCUtils.broadcastMessage(Main.getInstance().getGameInstance(), "[" + ChatColor.GOLD + ChatColor.BOLD + "UHC" + ChatColor.RESET + "] " + ChatColor.BOLD + p.getDisplayName() + ChatColor.GREEN +  ChatColor.ITALIC + " has joined the game!");
    }

    public static void announcePlayerSpectate(Player p) {
        UHCUtils.broadcastMessage(Main.getInstance().getGameInstance(), "[" + ChatColor.GOLD + ChatColor.BOLD + "UHC" + ChatColor.RESET + "] " + ChatColor.BOLD + p.getDisplayName() + ChatColor.YELLOW +  ChatColor.ITALIC + " is now spectating.");
    }

    public static Optional<List<Long>> getConfigCSLongs(String path) {
        String list = Main.getInstance().getConfig().getString(path);
        if (list == null) { return Optional.empty(); }
        List<Long> longs = new ArrayList<>();
        List<String> strings = Arrays.asList(list.split("\\s*,\\s*"));
        try {
            strings.stream().map(Long::valueOf).sorted().forEachOrdered(longs::add);
        } catch (NumberFormatException e) {
            Main.getInstance().getLogger().warning("List at " + path + " contains something other than numbers!");
            return Optional.empty();
        }
        return Optional.of(longs);
    }

    public static List<Long> getDefaultTimes() {
        long[] defaultTimes = {1,2,3,4,5,6,7,8,9,10,15,20,30,45,60,120,180,300,600,900,1200,1800,3600};
        ArrayList<Long> times = new ArrayList<>();
        Arrays.stream(defaultTimes).forEach(times::add);
        return times;
    }
}
