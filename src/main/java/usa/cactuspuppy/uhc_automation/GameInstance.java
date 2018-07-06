package usa.cactuspuppy.uhc_automation;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class GameInstance {
    public Main main;
    private boolean active;
    private World world;

    long startT;
    int minsToShrink;
    int initSize;
    int finalSize;
    int spreadDistance;
    int epLength;
    boolean teamMode;
    boolean respectTeams;
    boolean uhcMode;
    Set<UUID> livePlayers;
    Set<UUID> activePlayers;
    Set<UUID> regPlayers;
    Set<UUID> blacklistPlayers;
    
    int borderCountdown;
    boolean borderShrinking;
    PlayerMoveListener freezePlayers;
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
        UHCUtils.exeCmd("tp @a 0 201 0");
        UHCUtils.exeCmd(Bukkit.getServer(), world, "fill -10 200 -10 10 202 10 barrier 0 hollow");
        UHCUtils.exeCmd(Bukkit.getServer(), world, "fill -9 202 -9 9 202 9 air");
        UHCUtils.exeCmd(Bukkit.getServer(), world, "setworldspawn 0 201 0");
        UHCUtils.exeCmd("gamerule spawnRadius 0");
        UHCUtils.exeCmd("gamerule doDaylightCycle false");
        UHCUtils.exeCmd("gamerule doWeatherCycle false");
        UHCUtils.exeCmd("time set 0");
        UHCUtils.exeCmd("weather clear");
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
        UHCUtils.exeCmd("fill -10 200 -10 10 202 10 air");
        boolean spread = UHCUtils.spreadplayers(this);
        if (!spread) {
            s.sendMessage(ChatColor.RED + "Unable to spread this many players within specified gamespace! Consider decreasing the spread distance between players or increasing the initial size of the border with /uhcoptions. UHC aborted.");
            UHCUtils.broadcastMessage(this, ChatColor.RED + "Could not start UHC, settings invalid.");
            prep();
            return;
        }
        activePlayers = UHCUtils.getWorldPlayers(world);
        livePlayers = UHCUtils.getWorldLivePlayers(world, activePlayers);
        UHCUtils.saveWorldPlayers(main, livePlayers, activePlayers);
        HandlerList.unregisterAll(main.gmcl);
        UHCUtils.exeCmd("effect @a[m=0] clear");
        UHCUtils.exeCmd("effect @a[m=0] minecraft:resistance 10 10 true");
        UHCUtils.exeCmd("gamemode 2 @a[m=0]");
        if (teamMode) {
            Map<String, Object> conds = getConds();
            teamsRemaining = (int) conds.get("numTeams");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
        main.getLogger().info("Game Initiate Time - " + sdf.format(new Date(initT)));
        active = true;
        freezePlayers = new PlayerMoveListener(main);
        Bukkit.getServer().getPluginManager().registerEvents(freezePlayers, main);
        loadChunksCDID = (new LoadingChunksCountdown(main, 5)).schedule();
        return;
    }

    public void release() {
        Bukkit.getScheduler().cancelTask(loadChunksCDID);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerDeathListener(main), main);
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
        startT = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
        main.getLogger().info("Game Stop Time - " + sdf.format(new Date(stopT)));
        main.getLogger().info("Time Elapsed: " + timeElapsed / 3600000 + " hours "
                + (timeElapsed / 60000) % 60 + " minutes "
                + (timeElapsed / 1000) % 60 + " seconds");
        active = false;
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
            Map<String, Object> conds = getConds();
            if ((boolean) conds.get("oneTeamRemains")) {
                win();
            } else if (teamsRemaining != (int) conds.get("numTeams")) {
                UHCUtils.broadcastMessage(this, ChatColor.DARK_RED.toString() + ChatColor.BOLD + "\nA team has been eliminated! " + ChatColor.RESET + "\n" + conds.get("numTeams") + " teams remain!");
                teamsRemaining = (int) conds.get("numTeams");
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
        long hours = timeElapsed / 3600;
        long mins = (timeElapsed / 60) % 60;
        long secs = timeElapsed % 60;
        if (teamMode) {
            if (livePlayers.size() == 0) {
                UHCUtils.broadcastMessage(this, ChatColor.RED + "\nWait... what? The game ended in a tie!",
                        ChatColor.DARK_RED.toString() + ChatColor.BOLD + "DRAW", ChatColor.YELLOW + "Game ended in a tie!", 0, 80, 40);
                return;
            }
            Team t = livePlayers.stream().findFirst().map(u -> Bukkit.getPlayer(u).getScoreboard().getPlayerTeam(Bukkit.getPlayer(u))).orElse(null);
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
            UHCUtils.broadcastMessage(this, "\n" + t.getName() + ChatColor.GREEN + " has emerged victorious!\nMembers: " + ChatColor.RESET + winners,
                    t.getName() + ChatColor.GREEN + " wins!", "Winners: " + winners, 0 , 80, 40);
            UHCUtils.broadcastMessage(this, ChatColor.AQUA + "\nTime Elapsed: " + ChatColor.RESET + hours + " Hours " + mins + " Minutes " + secs + " Seconds");
        } else {
            if (livePlayers.size() == 1) {
                Player winner = livePlayers.stream().findFirst().map(Bukkit::getPlayer).orElse(null);
                if (winner == null) {
                    main.getLogger().severe("Unable to find winner. This is probably a bug.");
                    return;
                }
                UHCUtils.broadcastMessage(this, "\n" + ChatColor.GREEN + winner.getDisplayName() + ChatColor.WHITE + " wins!\n"
                        + ChatColor.AQUA + "\nTime Elapsed: " + ChatColor.RESET + hours + " Hours " + mins + " Minutes " + secs + " Seconds",
                        winner.getDisplayName(), "Wins!", 0, 80, 40);
            } else if (livePlayers.size() == 0) {
                UHCUtils.broadcastMessage(this, ChatColor.RED + "\nWait... what? The game ended in a tie!", ChatColor.DARK_RED.toString() + ChatColor.BOLD + "DRAW", ChatColor.YELLOW + "Game ended in a tie!", 0, 80, 40);
            } else {
                logStatus();
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

    public void logStatus() {
        Logger log = Bukkit.getServer().getLogger();
        String worldName;
        if (world != null) {
            worldName = world.getName();
        } else {
            worldName = "NOT BOUND";
        }
        log.info("World: " + worldName);
        log.info("Event Name: " + main.getConfig().getString("event-name"));
        log.info("Active: " + active);
        if (DEBUG) { log.info("DEBUG MODE ACTIVE"); }
        if (active) {
            log.info("Start Time: " + startT);
        }
        log.info("All Players (UUIDs)");
        if (regPlayers.isEmpty()) {
            log.info("  " + "NONE");
        }
        for (UUID u : regPlayers) {
            String online;
            if (Bukkit.getPlayer(u) == null) {
                online = "Offline";
            } else {
                online = "Online";
            }
            log.info("  " + u.toString() + " - " + online);
        }
        log.info("Logged-In Players:");
        if (activePlayers.isEmpty()) {
            log.info("  " + "NONE");
        }
        for (UUID u : activePlayers) {
            log.info("  " + Bukkit.getPlayer(u).getName());
        }
        log.info("Alive Players:");
        if (livePlayers.isEmpty()) {
            log.info("  " + "NONE");
        }
        for (UUID u : livePlayers) {
            log.info("  " + Bukkit.getPlayer(u).getName());
        }
        log.info("Unregistered Players (Blacklisted):");
        if (blacklistPlayers.isEmpty()) {
            log.info("  " + "NONE");
        }
        for (UUID u : blacklistPlayers) {
            log.info("  " + u.toString());
        }
        log.info("Team Mode: " + teamMode);
        log.info("Initial Size: " + initSize);
        log.info("Final Size: " + finalSize);
        log.info("Mins to Border Shrink: " + minsToShrink);
        log.info("Episode Marker Interval: " + epLength + " mins");
        log.info("Border Shrinking: " + borderShrinking);
    }

    @SuppressWarnings("deprecation")
    public Map<String, Object> getConds() {
        Map<String, Object> resultMap = new HashMap<>();
        List<Team> teams = new ArrayList<>();
        for (UUID u : livePlayers) {
            Player p = Bukkit.getPlayer(u);
            teams.add(p.getScoreboard().getPlayerTeam(p));
        }
        if (teams.size() == 1) {
            resultMap.put("oneTeamRemains", true);
            resultMap.put("winningTeam", teams.get(0));
        } else {
            resultMap.put("oneTeamRemains", false);
            resultMap.put("numTeams", teams.size());
        }
        return resultMap;
    }

    private int calcBorderShrinkTime() {
        return (initSize - finalSize) / 2;
    }

    public int getInitSize() {
        return initSize;
    }

    public int getFinalSize() {
        return finalSize;
    }

    public World getWorld() {
        return world;
    }

    public Set<UUID> getLivePlayers() {
        return livePlayers;
    }

    public Set<UUID> getActivePlayers() {
        return activePlayers;
    }

    public Set<UUID> getRegPlayers() {
        return regPlayers;
    }

    public Set<UUID> getBlacklistPlayers () { return blacklistPlayers; }

    public boolean isActive() {
        return active;
    }

    public void registerPlayer(Player p) {
        regPlayers.add(p.getUniqueId());
        activePlayers.add(p.getUniqueId());
        if (p.getGameMode() == GameMode.SURVIVAL) {
            livePlayers.add(p.getUniqueId());
            UHCUtils.exeCmd("effect " + p.getName() + " minecraft:weakness 1000000 255 true");
        }
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

    void setActive(boolean a) {
        active = a;
    }

    void setEpLength(int el) {
        epLength = el;
    }

    void setGameWorld(String worldname) {
        world = UHCUtils.getWorldFromString(main, Bukkit.getServer(), worldname);
    }

    void setInitSize(int s) {
        initSize = s;
        UHCUtils.exeCmd("worldborder set " + s);
    }

    void setFinalSize(int s) {
        finalSize = s;
    }

    void setTimeToShrink(int minutes) {
        minsToShrink = minutes;
    }

    void setTeamMode(boolean b) {
        teamMode = b;
    }

    void setSpreadDistance(int sd) {
        spreadDistance = sd;
    }

    void setRespectTeams(boolean rt) {
        respectTeams = rt;
    }

    void setUHCMode(boolean um) {
        uhcMode = um;
        UHCUtils.exeCmd("gamerule naturalRegeneration " + !um);
    }
}
