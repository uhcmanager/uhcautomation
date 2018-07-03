package usa.cactuspuppy.uhc_automation;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;
import usa.cactuspuppy.uhc_automation.Listeners.PlayerMoveListener;
import usa.cactuspuppy.uhc_automation.Tasks.BorderAnnouncer;
import usa.cactuspuppy.uhc_automation.Tasks.BorderCountdown;
import usa.cactuspuppy.uhc_automation.Tasks.DelayReactivate;
import usa.cactuspuppy.uhc_automation.Tasks.DelayedReset;
import usa.cactuspuppy.uhc_automation.Tasks.EpisodeAnnouncer;
import usa.cactuspuppy.uhc_automation.Tasks.LoadingChunksCountdown;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class GameInstance {
    public Main main;
    private boolean active;
    private World world;
    private Set<UUID> livePlayers;
    private Set<UUID> activePlayers;
    private Set<UUID> regPlayers;
    private Set<UUID> blacklistPlayers;

    public long startT;
    private int minsToShrink;
    private int initSize;
    private int finalSize;
    int spreadDistance;
    public int epLength;
    boolean teamMode;
    private boolean respectTeams;
    private boolean uhcMode;
    
    int borderCountdown;
    boolean borderShrinking;
    public PlayerMoveListener freezePlayers;
    Set<Player> giveBoats;
    private int loadChunksCDID;
    private int teamsRemaining;

    public static final boolean DEBUG = true;

    protected GameInstance(Main p) {
        main = p;
        startT = 0;
        teamsRemaining = 0;
        world = UHCUtils.getWorldFromString(main, Bukkit.getServer(), p.getConfig().getString("world"));
        minsToShrink = p.getConfig().getInt("game.mins-to-shrink");
        initSize = p.getConfig().getInt("game.init-size");
        finalSize = p.getConfig().getInt("game.final-size");
        teamMode = p.getConfig().getBoolean("game.team-mode");
        respectTeams = p.getConfig().getBoolean("game.respect-teams");
        spreadDistance = p.getConfig().getInt("game.spread-distance");
        uhcMode = p.getConfig().getBoolean("game.uhc-mode");
        epLength = p.getConfig().getInt("game.episode-length");
        livePlayers = new HashSet<>();
        activePlayers = new HashSet<>();
        regPlayers = new HashSet<>();
        blacklistPlayers = new HashSet<>();
        borderShrinking = false;
        active = false;
        (new DelayReactivate(this)).schedule();
    }

    public void prep() {
        if (UHCUtils.isWorldData(main)) {
            UHCUtils.clearWorldData(main);
        }
        UHCUtils.exeCmd(Bukkit.getServer(), world, "fill -10 253 -10 10 255 10 barrier 0 hollow");
        UHCUtils.exeCmd(Bukkit.getServer(), world, "fill -9 255 -9 9 255 9 air");
        UHCUtils.exeCmd(Bukkit.getServer(), world, "setworldspawn 0 254 0");
        Location spawn = new Location(world, 0, 254, 0);
        for (Player p : activePlayers.stream().map(Bukkit::getPlayer).collect(Collectors.toList())) {
            p.teleport(spawn);
            p.setGameMode(GameMode.SURVIVAL);
            UHCUtils.exeCmd("effect " + p.getName() + " minecraft:weakness 1000000 255 true");
        }
        UHCUtils.exeCmd("gamerule spawnRadius 0");
        UHCUtils.exeCmd("gamerule doDaylightCycle false");
        UHCUtils.exeCmd("gamerule doWeatherCycle false");
        UHCUtils.exeCmd("time set 0");
        UHCUtils.exeCmd("weather clear");
        UHCUtils.exeCmd("worldborder center 0 0");
        UHCUtils.exeCmd("worldborder set " + initSize);
        UHCUtils.exeCmd("gamerule naturalRegeneration " + !uhcMode);
        UHCUtils.exeCmd("scoreboard objectives add Health health");
        UHCUtils.exeCmd("scoreboard objectives setDisplay list Health");
    }

    public void start(CommandSender s) {
        if (livePlayers.size() <= 1 && !DEBUG) {
            main.getLogger().warning("Not enough players are in the UHC!");
            s.sendMessage(ChatColor.RED + "UHC aborted! Not enough players in the UHC!");
            return;
        }
        long initT = System.currentTimeMillis();
        UHCUtils.broadcastMessage(this, ChatColor.GREEN + "Game starting!");
        UHCUtils.exeCmd("clear @a[m=0]");
        UHCUtils.exeCmd("fill -10 253 -10 10 255 10 air");
        boolean spread = UHCUtils.spreadplayers(this);
        if (!spread) {
            s.sendMessage(ChatColor.RED + "Unable to spread this many players within specified gamespace! Consider decreasing the spread distance between players or increasing the initial size of the border with /uhcoptions. UHC aborted.");
            UHCUtils.broadcastMessage(this, ChatColor.RED.toString() + ChatColor.BOLD + "Could not start UHC.");
            prep();
            return;
        }
        UHCUtils.saveWorldPlayers(main, livePlayers, activePlayers);
        HandlerList.unregisterAll(main.gmcl);
        UHCUtils.exeCmd("effect @a[m=0] clear");
        UHCUtils.exeCmd("effect @a[m=0] minecraft:resistance 10 10 true");
        UHCUtils.exeCmd("gamemode 2 @a[m=0]");
        if (teamMode) {
            teamsRemaining = getNumTeams();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
        main.getLogger().info("Game Initiate Time - " + sdf.format(new Date(initT)));
        active = true;
        freezePlayers = new PlayerMoveListener(main);
        Bukkit.getServer().getPluginManager().registerEvents(freezePlayers, main);
        loadChunksCDID = (new LoadingChunksCountdown(main, 5)).schedule();
    }

    public void release() {
        Bukkit.getScheduler().cancelTask(loadChunksCDID);
        startT = System.currentTimeMillis();
        UHCUtils.saveStartTime(main, startT);
        UHCUtils.exeCmd("gamemode 0 @a[m=2]");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
        main.getLogger().info("Game Start Time - " + sdf.format(new Date(startT)));
        UHCUtils.exeCmd("gamerule doDaylightCycle true");
        UHCUtils.exeCmd("gamerule doWeatherCycle true");
        if (minsToShrink > 0) {
            borderCountdown = (new BorderCountdown(main, minsToShrink * 60, startT)).schedule();
        } else if (minsToShrink == 0) {
            borderShrinking = true;
            UHCUtils.exeCmd(Bukkit.getServer(), world,
                    "worldborder set " + finalSize + " " + calcBorderShrinkTime());
            main.getLogger().info("Game border shrinking from " + initSize + " to " + finalSize
                    + " over " + calcBorderShrinkTime() + " secs");
            (new BorderAnnouncer(main)).schedule();
            UHCUtils.exeCmd("gamerule doDaylightCycle false");
            UHCUtils.exeCmd("gamerule doWeatherCycle false");
            UHCUtils.exeCmd("weather clear");
            UHCUtils.exeCmd("time set 6000");
        }
        if (epLength != 0) {
            (new EpisodeAnnouncer(main, epLength, startT)).schedule();
        }
        HandlerList.unregisterAll(freezePlayers);
        activePlayers.forEach(this::startPlayer);
        giveBoats = null;
    }

    public void stop() {
        (new DelayedReset(main)).schedule();
        long stopT = System.currentTimeMillis();
        long timeElapsed;
        if (startT == 0) {
            timeElapsed = 0;
        } else {
            timeElapsed = stopT - startT;
        }
        timeElapsed /= 1000;
        startT = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
        main.getLogger().info("Game Stop Time - " + sdf.format(new Date(stopT)));
        main.getLogger().info("Time Elapsed: " + UHCUtils.secsToFormatString((int) timeElapsed));
        active = false;
        borderShrinking = false;
        UHCUtils.clearWorldData(main);
    }

    public void startBorderShrink() {
        borderShrinking = true;
        UHCUtils.exeCmd(Bukkit.getServer(), world,
                "worldborder set " + finalSize + " " + calcBorderShrinkTime());
        main.getLogger().info("Game border shrinking from " + initSize + " to " + finalSize
                + " over " + calcBorderShrinkTime() + " secs");
        Bukkit.getScheduler().cancelTask(borderCountdown);
        for (UUID u : activePlayers) {
            alertPlayerBorder(u);
        }
        (new BorderAnnouncer(main)).schedule();
        UHCUtils.exeCmd("gamerule doDaylightCycle false");
        UHCUtils.exeCmd("gamerule doWeatherCycle false");
        UHCUtils.exeCmd("weather clear");
        UHCUtils.exeCmd("time set 6000");
    }

    @SuppressWarnings("deprecation")
    public void checkForWin() {
        if (teamMode) {
            int numTeams = getNumTeams();
            if (numTeams <= 1) {
                win();
            } else if (numTeams < teamsRemaining) {
                UHCUtils.broadcastMessage(this, ChatColor.DARK_RED.toString() + ChatColor.BOLD + "\nA team has been eliminated! " + ChatColor.RESET + "\n" + numTeams + " teams remain!");
                teamsRemaining = numTeams;
            }
        } else {
            if (livePlayers.size() <= 1) {
                win();
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void win() {
        long timeElapsed = (System.currentTimeMillis() - startT) / 1000;
        if (teamMode) {
            if (getNumTeams() > 1) {
                logStatus(Bukkit.getConsoleSender());
                main.getLogger().severe("Win condition called for game instance running on world " + world.getName() + " under invalid circumstances. Game status dumped to log.");
                return;
            }
            if (livePlayers.size() == 0) {
                UHCUtils.broadcastMessagewithTitle(this, ChatColor.RED + "\nWait... what? The game ended in a tie!",
                        ChatColor.DARK_RED.toString() + ChatColor.BOLD + "DRAW", ChatColor.RESET + "Game ended in a tie!", 0, 80, 40);
            } else {
                Team t = livePlayers.stream().findFirst().map(u -> Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(Bukkit.getPlayer(u))).orElse(null);
                if (t == null) {
                    main.getLogger().severe("Winning team verification failed.");
                    return;
                }
                List<String> onlineWinners = new ArrayList<>();
                for (OfflinePlayer p : t.getPlayers()) {
                    if (p.isOnline()) {
                        onlineWinners.add(((Player) p).getDisplayName());
                    }
                }
                StringBuilder winningTeamPlayers = new StringBuilder();
                if (onlineWinners.size() == 1) {
                    winningTeamPlayers.append(onlineWinners.get(0));
                } else if (onlineWinners.size() == 2) {
                    winningTeamPlayers.append(onlineWinners.get(0)).append(" and ").append(onlineWinners.get(1));
                } else {
                    winningTeamPlayers.append(onlineWinners.get(0));
                    for (int i = 1; i < onlineWinners.size(); i++) {
                        if (i == onlineWinners.size() - 1) {
                            winningTeamPlayers.append(", and ").append(onlineWinners.get(i));
                        } else {
                            winningTeamPlayers.append(", ").append(onlineWinners.get(i));
                        }
                    }
                }
                String winners = winningTeamPlayers.toString();
                UHCUtils.broadcastMessagewithTitle(this, "\n" + t.getName() + ChatColor.GREEN + " has emerged victorious!\nMembers: " + ChatColor.RESET + winners,
                        t.getName(), ChatColor.GREEN + "wins!", 0 , 80, 40);
                UHCUtils.broadcastMessage(this, ChatColor.AQUA + "\nTime Elapsed: " + ChatColor.RESET + WordUtils.capitalize(UHCUtils.secsToFormatString((int) timeElapsed)));
            }
        } else {
            if (livePlayers.size() == 1) {
                Player winner = livePlayers.stream().findFirst().map(Bukkit::getPlayer).orElse(null);
                if (winner == null) {
                    main.getLogger().severe("Unable to find winner. This is probably a bug.");
                    return;
                }
                UHCUtils.broadcastMessagewithTitle(this, "\n" + ChatColor.GREEN + winner.getDisplayName() + ChatColor.WHITE + " wins!\n"
                        + ChatColor.AQUA + "\nTime Elapsed: " + ChatColor.RESET + WordUtils.capitalize(UHCUtils.secsToFormatString((int) timeElapsed)),
                        winner.getDisplayName(), "Wins!", 0, 80, 40);
            } else if (livePlayers.size() == 0) {
                UHCUtils.broadcastMessagewithTitle(this, ChatColor.RED + "\nWait... what? The game ended in a tie!", ChatColor.DARK_RED.toString() + ChatColor.BOLD + "DRAW", ChatColor.YELLOW + "Game ended in a tie!", 0, 80, 40);
            } else {
                logStatus(Bukkit.getConsoleSender());
                main.getLogger().severe("Win condition called for game instance running on world " + world.getName() + " under invalid circumstances. Game status dumped to log.");
            }
        }
        stop();
    }

    /**
     *  Helper/Access methods
     */

    public void startPlayer(UUID u) {
        Player p = Bukkit.getPlayer(u);
        p.sendTitle(ChatColor.GREEN.toString() + ChatColor.BOLD + "GO!", UHCUtils.randomStartMSG(), 0, 80, 40);
        p.playSound(p.getLocation(), "minecraft:block.note.pling", 1F, 1.18F);
        p.playSound(p.getLocation(), "minecraft:entity.enderdragon.growl", 1F, 1F);
        UHCUtils.exeCmd("effect " + p.getName() + " clear");
        p.setFoodLevel(20);
        p.setSaturation(5);
        p.setHealth(20);
        if (giveBoats.contains(p)) {
            p.getInventory().addItem(new ItemStack(Material.BOAT, 1));
            p.sendMessage(ChatColor.GREEN + "Since you spawned in an ocean biome, you have received a boat to reach land faster.");
        }
    }

    private void alertPlayerBorder(UUID u) {
        Player p = Bukkit.getPlayer(u);
        if (p == null) {
            return;
        }
        p.sendMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "\nBorder shrinking!");
        p.sendTitle(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Border Shrinking!", "", 0, 80, 40);
        p.playSound(p.getLocation(), "minecraft:entity.enderdragon.death", 1F, 1F);
    }

    public boolean validate(CommandSender s) {
        boolean valid;
        valid = initSize > finalSize;
        if (!valid) {
            s.sendMessage(String.format(ChatColor.RED + "Initial size %d is not greater than final size %d. UHC aborted.", initSize, finalSize));
        }
        valid = valid && spreadDistance < initSize;
        if (!valid) {
            s.sendMessage(String.format(ChatColor.RED + "Player separation distance %d is not less than spread range %d. UHC aborted.", spreadDistance, initSize / 2));
        }
        return valid;
    }

    public void logStatus(CommandSender s) {
        String worldName;
        if (world != null) {
            worldName = world.getName();
        } else {
            worldName = "NOT BOUND";
        }
        s.sendMessage("\n" + ChatColor.GOLD.toString() + ChatColor.BOLD + "Current Status:");
        s.sendMessage("World: " + worldName);
        s.sendMessage("Event Name: " + main.getConfig().getString("event-name"));
        s.sendMessage("Active: " + active);
        s.sendMessage("Team Mode: " + teamMode);
        if (DEBUG) { s.sendMessage(ChatColor.RED + "DEBUG MODE ACTIVE"); }
        if (active) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
            s.sendMessage("Start Time: " + sdf.format(startT));
            s.sendMessage("Time Elapsed: " + UHCUtils.secsToFormatString((int) ((System.currentTimeMillis() - startT) / 1000)));
            if (teamMode) {
                s.sendMessage("Teams Remaining: " + teamsRemaining);
            } else {
                s.sendMessage("Player Remaining: " + livePlayers.size());
            }
            s.sendMessage("Border Shrinking: " + borderShrinking);
        }
        s.sendMessage("All Players (UUIDs)");
        if (regPlayers.isEmpty()) {
            s.sendMessage("  " + "NONE");
        } else {
            for (UUID u : regPlayers) {
                String name;
                String online;
                Player p = Bukkit.getPlayer(u);
                if (p == null) {
                    name = u.toString();
                    online = "Offline";
                } else {
                    name = p.getName();
                    online = "Online";
                }
                s.sendMessage("  " + name + " - " + online);
            }
        }
        s.sendMessage("Logged-In Players:");
        if (activePlayers.isEmpty()) {
            s.sendMessage("  " + "NONE");
        } else {
            for (UUID u : activePlayers) {
                s.sendMessage("  " + Bukkit.getPlayer(u).getName());
            }
        }
        s.sendMessage("Alive Players:");
        if (livePlayers.isEmpty()) {
            s.sendMessage("  " + "NONE");
        } else {
            for (UUID u : livePlayers) {
                s.sendMessage("  " + Bukkit.getPlayer(u).getName());
            }
        }
        s.sendMessage("Unregistered Players (Blacklisted):");
        if (blacklistPlayers.isEmpty()) {
            s.sendMessage("  " + "NONE");
        } else {
            for (UUID u : blacklistPlayers) {
                s.sendMessage("  " + u.toString());
            }
        }
        s.sendMessage("Initial Size: " + initSize);
        s.sendMessage("Final Size: " + finalSize);
        s.sendMessage("Mins to Border Shrink: " + minsToShrink);
        s.sendMessage("Episode Marker Interval: " + epLength + " mins");
    }

    @SuppressWarnings("deprecation")
    public int getNumTeams() {
        Map<String, Object> resultMap = new HashMap<>();
        Set<Team> teams = new HashSet<>();
        for (UUID u : livePlayers) {
            Player p = Bukkit.getPlayer(u);
            teams.add(Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(p));
        }
        return resultMap.size();
    }

    public void recalcPlayerSet() {
        Set<UUID> aPCopy = new HashSet<>(activePlayers);
        for (UUID u : aPCopy) {
            if (Bukkit.getOfflinePlayer(u) == null || !Bukkit.getOfflinePlayer(u).isOnline()) {
                activePlayers.remove(u);
                livePlayers.remove(u);
            }
        }
    }

    private int calcBorderShrinkTime() {
        double slowFactor = 2.0;
        return (int) ((initSize - finalSize) * slowFactor);
    }

    public int getInitSize() { return initSize; }

    public int getFinalSize() { return finalSize; }

    public World getWorld() { return world; }

    public Set<UUID> getLivePlayers() { return livePlayers; }

    public Set<UUID> getActivePlayers() { return activePlayers; }

    public Set<UUID> getRegPlayers() { return regPlayers; }

    public Set<UUID> getBlacklistPlayers() { return blacklistPlayers; }

    public int getMinsToShrink() { return minsToShrink; }

    public boolean isActive() {
        return active;
    }

    public boolean isStarted() {
        return startT == 0;
    }

    public boolean isRespectTeams() {
        return respectTeams;
    }


    public void registerPlayer(Player p) {
        regPlayers.add(p.getUniqueId());
        activePlayers.add(p.getUniqueId());
        if (p.getGameMode() == GameMode.SURVIVAL) {
            livePlayers.add(p.getUniqueId());
            if (!active) {
                UHCUtils.exeCmd("effect " + p.getName() + " minecraft:weakness 1000000 255 true");
            }
        }
        if (TimeModeCache.getInstance().getPlayerPref(p.getUniqueId()) == null) {
            TimeModeCache.getInstance().storePlayerPref(p.getUniqueId(), TimeDisplayMode.CHAT);
        }
    }

    public void addPlayerToLive(Player p) {
        livePlayers.add(p.getUniqueId());
    }

    public void removePlayerFromLive(Player p) {
        livePlayers.remove(p.getUniqueId());
    }

    public void lostConnectPlayer(OfflinePlayer p) {
        activePlayers.remove(p.getUniqueId());
        livePlayers.remove(p.getUniqueId());
    }

    public void blacklistPlayer(UUID u) {
        blacklistPlayers.add(u);
        regPlayers.remove(u);
        activePlayers.remove(u);
        livePlayers.remove(u);
    }

    public void setActive(boolean a) {
        active = a;
    }

    public void setEpLength(int el) {
        epLength = el;
    }

    public void setGameWorld(String worldname) {
        world = UHCUtils.getWorldFromString(main, Bukkit.getServer(), worldname);
    }

    public void setInitSize(int s) {
        initSize = s;
        UHCUtils.exeCmd("worldborder set " + s);
    }

    public void setFinalSize(int s) {
        finalSize = s;
    }

    public void setTimeToShrink(int minutes) {
        minsToShrink = minutes;
    }

    public void setTeamMode(boolean b) {
        teamMode = b;
    }

    public void setSpreadDistance(int sd) {
        spreadDistance = sd;
    }

    public void setRespectTeams(boolean rt) {
        respectTeams = rt;
    }

    public void setUHCMode(boolean um) {
        uhcMode = um;
        UHCUtils.exeCmd("gamerule naturalRegeneration " + !um);
    }

    public void setLivePlayers(Set<UUID> livePlayers1) {
        livePlayers = livePlayers1;
    }

    public void setActivePlayers(Set<UUID> activePlayers1) {
        activePlayers = activePlayers1;
    }

    public void setBorderCountdown(int cd) {
        borderCountdown = cd;
    }
}
