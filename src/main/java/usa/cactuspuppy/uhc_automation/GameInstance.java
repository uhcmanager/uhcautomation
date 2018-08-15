package usa.cactuspuppy.uhc_automation;

import lombok.Getter;
import lombok.Setter;
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
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import usa.cactuspuppy.uhc_automation.Listeners.GameModeChangeListener;
import usa.cactuspuppy.uhc_automation.Listeners.PlayerMoveListener;
import usa.cactuspuppy.uhc_automation.Tasks.BorderAnnouncer;
import usa.cactuspuppy.uhc_automation.Tasks.BorderCountdown;
import usa.cactuspuppy.uhc_automation.Tasks.DelayReactivate;
import usa.cactuspuppy.uhc_automation.Tasks.EpisodeAnnouncer;
import usa.cactuspuppy.uhc_automation.Tasks.LoadingChunksCountdown;
import usa.cactuspuppy.uhc_automation.Tasks.RestartTasks;
import usa.cactuspuppy.uhc_automation.Tasks.TimeAnnouncer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class GameInstance {
    @Getter private Main main;
    @Getter @Setter private long startT;
    @Getter @Setter private boolean teamMode;
    @Getter @Setter private boolean active;
    @Getter @Setter private World world;
    @Getter private Set<UUID> livePlayers;
    @Getter private Set<UUID> activePlayers;
    @Getter @Setter private Set<UUID> regPlayers;
    @Getter @Setter private Set<UUID> blacklistPlayers;
    @Getter @Setter private int minsToShrink;
    @Getter private int initSize;
    @Getter @Setter private int finalSize;
    @Getter @Setter private int spreadDistance;
    @Getter @Setter private int epLength;
    @Getter @Setter private boolean respectTeams;
    private boolean uhcMode;
    @Getter private Scoreboard scoreboard;
    @Setter private int borderCountdown;
    private boolean borderShrinking;
    @Getter private PlayerMoveListener freezePlayers;
    @Getter @Setter private TimeAnnouncer timeAnnouncer;
    @Getter @Setter private Set<Player> giveBoats;
    private int loadChunksCDID;
    private int teamsRemaining;

    public static final boolean DEBUG = true;

    public GameInstance(Main p) {
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
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        if (scoreboard.getObjective("Health") == null) {
            scoreboard.registerNewObjective("Health", "health", ChatColor.RED + "Health").setDisplaySlot(DisplaySlot.PLAYER_LIST);
        } else if (!scoreboard.getObjective("Health").getCriteria().equals("health")) {
            scoreboard.getObjective("Health").unregister();
            scoreboard.registerNewObjective("Health", "health", ChatColor.RED + "Health").setDisplaySlot(DisplaySlot.PLAYER_LIST);
        }
    }

    public void prep() {
        if (UHCUtils.isWorldData(main)) {
            UHCUtils.clearWorldData(main);
        }
        //Wall Pair 1
        for (int x = -10; x <= 10; x++) {
            for (int y = 253; y <= 255; y++) {
                world.getBlockAt(x, y, -10).setType(Material.BARRIER);
                world.getBlockAt(x, y, 10).setType(Material.BARRIER);
            }
        }
        //Wall Pair 2
        for (int z = -10; z <= 10; z++) {
            for (int y = 253; y <= 255; y++) {
                world.getBlockAt(-10, y, z).setType(Material.BARRIER);
                world.getBlockAt(10, y, z).setType(Material.BARRIER);
            }
        }
        //Floor
        for (int x = -10; x <= 10; x++) {
            for (int z = -10; z <= 10; z++) {
                world.getBlockAt(x, 253, z).setType(Material.BARRIER);
            }
        }
        world.setSpawnLocation(0, 254, 0);
        Location spawn = new Location(world, 0, 254, 0);
        for (Player p : activePlayers.stream().map(Bukkit::getPlayer).collect(Collectors.toList())) {
            p.teleport(spawn);
            p.setGameMode(GameMode.SURVIVAL);
        }
        world.setGameRuleValue("spawnRadius", "0");
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("doWeatherCycle", "false");
        world.setTime(0L);
        world.setStorm(false);
        world.setPVP(false);
        world.getWorldBorder().setCenter(0D, 0D);
        world.getWorldBorder().setSize(initSize);
        world.setGameRuleValue("naturalRegenration", String.valueOf(!uhcMode));
    }

    public void start(CommandSender s) {
        if (!checkNumPlayers() && !DEBUG) {
            main.getLogger().warning("Not enough players are in the UHC!");
            s.sendMessage(ChatColor.RED + "UHC aborted! Not enough players in the UHC!");
            UHCUtils.broadcastMessage(this, ChatColor.RED.toString() + ChatColor.BOLD + "Could not start UHC.");
            return;
        }
        long initT = System.currentTimeMillis();
        UHCUtils.broadcastMessage(this, ChatColor.GREEN + "Game starting!");
        HandlerList.unregisterAll(GameModeChangeListener.getInstance());
        livePlayers.stream().map(Bukkit::getPlayer).forEach(this::prepPlayer);
        for (int x = -10; x <= 10; x++) {
            for (int y = 253; y <= 255; y++) {
                for (int z = -10; z <= 10; z++) {
                    world.getBlockAt(x, y, z).setType(Material.AIR);
                }
            }
        }
        boolean spread = UHCUtils.spreadplayers(this);
        if (!spread) {
            s.sendMessage(ChatColor.RED + "Unable to spread this many players within specified gamespace! Consider decreasing the spread distance between players or increasing the initial size of the border with /uhcoptions. UHC aborted.");
            UHCUtils.broadcastMessage(this, ChatColor.RED.toString() + ChatColor.BOLD + "Could not start UHC.");
            prep();
            return;
        }
        UHCUtils.saveWorldPlayers(main);
        if (teamMode) {
            teamsRemaining = getNumTeams();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
        main.getLogger().info("Game Initiate Time - " + sdf.format(new Date(initT)));
        active = true;
        freezePlayers = new PlayerMoveListener();
        Bukkit.getServer().getPluginManager().registerEvents(freezePlayers, main);
        loadChunksCDID = (new LoadingChunksCountdown(main, 5)).schedule();
    }

    private boolean checkNumPlayers() {
        if (teamMode) {
            return getNumTeams() >= 2;
        }
        return livePlayers.size() >= 2;
    }

    private void prepPlayer(Player p) {
        p.getInventory().clear();
        p.getActivePotionEffects().forEach(e -> p.removePotionEffect(e.getType()));
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "effect " + p.getName() + " minecraft:resistance 10 10 true");
        p.setGameMode(GameMode.ADVENTURE);
    }

    public void release() {
        Bukkit.getScheduler().cancelTask(loadChunksCDID);
        startT = System.currentTimeMillis();
        UHCUtils.saveAuxData(main);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
        main.getLogger().info("Game Start Time - " + sdf.format(new Date(startT)));
        world.setGameRuleValue("doDaylightCycle", "true");
        world.setGameRuleValue("doWeatherCycle", "true");
        world.setPVP(true);
        world.setStorm(false);
        world.setWeatherDuration((new Random()).nextInt(48000) + 24000);
        if (minsToShrink > 0) {
            borderCountdown = (new BorderCountdown(minsToShrink * 60, startT)).schedule();
        } else if (minsToShrink == 0) {
            borderShrinking = true;
            world.getWorldBorder().setSize(finalSize, calcBorderShrinkTime());
            main.getLogger().info("Game border shrinking from " + initSize + " to " + finalSize
                    + " over " + calcBorderShrinkTime() + " secs");
            (new BorderAnnouncer()).schedule();
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("doWeatherCycle", "false");
            world.setTime(6000L);
        }
        if (epLength != 0) {
            (new EpisodeAnnouncer(epLength, startT)).schedule();
        }
        HandlerList.unregisterAll(freezePlayers);
        activePlayers.forEach(this::startPlayer);
        giveBoats = null;
        timeAnnouncer.schedule();
        timeAnnouncer.showBoard();
    }

    public void stop() {
        (new RestartTasks()).schedule();
        timeAnnouncer.clearBoard();
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
        blacklistPlayers.clear();
    }

    public void startBorderShrink() {
        borderShrinking = true;
        world.getWorldBorder().setSize(finalSize, calcBorderShrinkTime());
        main.getLogger().info("Game border shrinking from " + initSize + " to " + finalSize
                + " over " + calcBorderShrinkTime() + " secs");
        Bukkit.getScheduler().cancelTask(borderCountdown);
        for (UUID u : activePlayers) {
            alertPlayerBorder(u);
        }
        (new BorderAnnouncer()).schedule();
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("doWeatherCycle", "false");
        world.setTime(6000L);
        world.setStorm(false);
    }

    @SuppressWarnings("deprecation")
    public void win() {
        int timeElapsed = UHCUtils.getSecsElapsed(main);
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
                UHCUtils.broadcastMessage(this, ChatColor.AQUA + "\nTime Elapsed: " + ChatColor.RESET + WordUtils.capitalize(UHCUtils.secsToFormatString(timeElapsed)));
            }
        } else {
            if (livePlayers.size() == 1) {
                Player winner = livePlayers.stream().findFirst().map(Bukkit::getPlayer).orElse(null);
                assert winner != null;
                UHCUtils.broadcastMessagewithTitle(this, "\n" + ChatColor.GREEN + winner.getDisplayName() + ChatColor.WHITE + " wins!\n"
                        + ChatColor.AQUA + "\nTime Elapsed: " + ChatColor.RESET + WordUtils.capitalize(UHCUtils.secsToFormatString((int) timeElapsed)),
                        winner.getDisplayName(), "Wins!", 0, 80, 40);
            } else if (livePlayers.size() == 0) {
                UHCUtils.broadcastMessagewithTitle(this, ChatColor.RED + "\nWait... what? The game ended in a tie!", ChatColor.DARK_RED.toString() + ChatColor.BOLD + "DRAW", ChatColor.YELLOW + "Game ended in a tie!", 0, 80, 40);
            } else {
                logStatus(Bukkit.getConsoleSender());
                main.getLogger().severe("Win condition called early for game instance running on world " + world.getName() + ". Game status dumped to log.");
                return;
            }
        }
        stop();
    }

    /**
     *  Helper/Access methods
     */

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

    public void startPlayer(UUID u) {
        Player p = Bukkit.getPlayer(u);
        p.sendTitle(ChatColor.GREEN.toString() + ChatColor.BOLD + "GO!", UHCUtils.randomStartMSG(), 0, 80, 40);
        p.playSound(p.getLocation(), "minecraft:block.note.pling", 1F, 1.18F);
        p.playSound(p.getLocation(), "minecraft:entity.enderdragon.growl", 1F, 1F);
        p.getActivePotionEffects().forEach(e -> p.removePotionEffect(e.getType()));
        p.setFoodLevel(20);
        p.setSaturation(5);
        p.setHealth(20);
        p.setGameMode(GameMode.SURVIVAL);
        if (giveBoats.contains(p)) {
            p.getInventory().addItem(new ItemStack(Material.OAK_BOAT, 1));
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
            s.sendMessage("Time Elapsed: " + UHCUtils.secsToFormatString(UHCUtils.getSecsElapsed(main)));
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
        s.sendMessage("Dead/Blacklisted Players (UUIDs):");
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
        Set<Team> teams = new HashSet<>();
        for (UUID u : livePlayers) {
            Player p = Bukkit.getPlayer(u);
            teams.add(Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(p));
        }
        return teams.size();
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
        double slowFactor = 1.5;
        return (int) ((initSize - finalSize) * slowFactor);
    }

    public void bindPlayertoScoreboard(Player p) {
        p.setScoreboard(scoreboard);
    }

    public boolean isStarted() {
        return startT != 0;
    }

    public void registerPlayer(Player p) {
        registerPlayerSilent(p);
        if (p.getGameMode() == GameMode.SURVIVAL) {
            UHCUtils.announcePlayerJoin(p);
        } else {
            UHCUtils.announcePlayerSpectate(p);
        }
    }

    public void registerPlayerSilent(Player p) {
        regPlayers.add(p.getUniqueId());
        activePlayers.add(p.getUniqueId());
        if (p.getGameMode() == GameMode.SURVIVAL) {
            livePlayers.add(p.getUniqueId());
            if (teamMode && active) {
                teamsRemaining = getNumTeams();
            }
        }
        if (InfoModeCache.getInstance().getPlayerPref(p.getUniqueId()) == null) {
            InfoModeCache.getInstance().storePlayerPref(p.getUniqueId(), InfoDisplayMode.CHAT);
        }
    }

    public void addPlayerToLive(Player p) {
        livePlayers.add(p.getUniqueId());
        UHCUtils.announcePlayerJoin(p);
    }

    public void removePlayerFromLive(Player p) {
        livePlayers.remove(p.getUniqueId());
        blacklistPlayers.add(p.getUniqueId());
        UHCUtils.announcePlayerSpectate(p);
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

    public void setInitSize(int s) {
        initSize = s;
        world.getWorldBorder().setSize(initSize);
    }

    public void setUHCMode(boolean um) {
        uhcMode = um;
        world.setGameRuleValue("naturalRegeneration", String.valueOf(!um));
    }
}
