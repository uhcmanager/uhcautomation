package usa.cactuspuppy.uhc_automation;

import net.minecraft.server.v1_12_R1.ChatMessageType;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

public class UHCUtils {
    /**
     * Assistance with phrases provided by: Djinnjurr, krohn67, daniodle, Aurabeth, Remuko, Wyntr, DreDeveraux
     */
    public static final String[] START_MSGS =
            {"Live and let die!", "Go for broke!", "Ready Player One!", "No time like the present!",
                    "This ought to be a match to remember!", "Eliminate Other Players", "Capture Objective A", "Triumph or Die!",
                    "Hack and Slash!", "Lives Remaining: 1", "Tear them apart!", "May the odds be ever in your favor!",
                    "Once more unto the breach", "Go Get 'Em, Tiger!", "Time to kick the tires and light the fires!", "Git 'er Done!",
                    "Alea Iacta Est", "Cry havoc and let slip the dogs of war", "Let Slip the Dogs of War!", "Veni, vidi, vici.",
                    "Oooh hoo hoo hoo... this'll be good!", "Fire In The Hole!", "Fire at will!", "Fight for the Assassins!",
                    "Kill or be killed!", "THEY SHALL NOT PASS", "If anyone is gonna win, it's gonna be you.",
                    "Who's ready for some fireworks!", "LEEEROY JENKINS", "[inspirational message]", "Release the hounds!",
                    "Good luck, don't die!", "Just Do It!", "FOR THE HORDE!", "For The Alliance!", "Watch out for bears!",
                    "How do YOU want to do this?", "Know yourself, know thy enemy, and you shall win.", "For Aiur!",
                    "One Punch is all you need!", "Roll for Initiative!", "You know you have to do it to 'em", "Watch out for boars!"};
    //do not instantiate
    public UHCUtils() { }

    public static Set<UUID> getWorldPlayers(World w) {
        HashSet<UUID> rv = new HashSet<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getWorld().equals(w)) {
                rv.add(p.getUniqueId());
            }
        }
        return rv;
    }

    public static Set<UUID> getWorldLivePlayers(World w, Set<UUID> players) {
        HashSet<UUID> rv = new HashSet<>();
        for (UUID u : players) {
            Player p = Bukkit.getPlayer(u);
            if (p.getGameMode().equals(GameMode.SURVIVAL) && p.getWorld().equals(w)) {
                rv.add(p.getUniqueId());
            }
        }
        return rv;
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
                m.getLogger().info("Created world data folder: '" + location + "', savind data...");
            }
        }
        String startTimeFileName = location + "/startTime.txt";
        File startTimeFile = new File(startTimeFileName);
        if (startTimeFile.isFile()) {
            startTimeFile.delete();
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
        String aPFileName = location + "/allPlayers.txt";
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
                rv.put("allPlayers", allPlayers);
                m.getLogger().info("Successfully extracted allPlayers UUID set");
            } else {
                m.getLogger().info("Could not find allPlayers UUID set, cleansing directory");
                FileUtils.cleanDirectory(new File(location));
                return new HashMap<>();
            }
        } catch (IOException e) {
            m.getLogger().severe("Error while loading in allPlayers file: " + aPFileName + ", cleansing directory!");
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
        String allPlayersName = location + "/allPlayers.txt";
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
            m.getLogger().severe("Could not save allPlayers to file " + allPlayersName);
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

    /**
     * @source ConnorLinfoot
     * @param player to send title actionbar to
     * @param message to display
     */
    public static void sendActionBar(Player player, String message){
        CraftPlayer p = (CraftPlayer) player;
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, ChatMessageType.CHAT);
        p.getHandle().playerConnection.sendPacket(ppoc);
    }

    public static String randomStartMSG() {
        Random r = new Random();
        int msg = r.nextInt(START_MSGS.length);
        return START_MSGS[msg];
    }
}
