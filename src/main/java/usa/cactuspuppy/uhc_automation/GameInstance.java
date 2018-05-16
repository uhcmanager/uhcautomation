package usa.cactuspuppy.uhc_automation;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public class GameInstance {
    private Main main;
    private long startT;
    private boolean active;
    private Set<UUID> livePlayers;
    private Set<UUID> allPlayers;
    private World world;

    private int minsToShrink;
    private int initSize;
    private int finalSize;
    private int spreadDistance;
    private int epLength;
    private boolean teamMode;
    private boolean respectTeams;
    private boolean uhcMode;
    
    private int borderCountdown;
    private boolean borderShrinking;
    private PlayerMoveListener freezePlayers;
    private int loadChunksCDID;

    public static final boolean DEBUG = true;

    protected GameInstance(Main p) {
        main = p;
        startT = 0;
        minsToShrink = p.getConfig().getInt("game.minds-to-shrink");
        initSize = p.getConfig().getInt("game.init-size");
        finalSize = p.getConfig().getInt("game.final-size");
        teamMode = p.getConfig().getBoolean("game.team-mode");
        respectTeams = p.getConfig().getBoolean("game.respect-teams");
        spreadDistance = p.getConfig().getInt("game.spread-distance");
        teamMode = p.getConfig().getBoolean("game.team-mode");
        uhcMode = p.getConfig().getBoolean("game.uhc-mode");
        epLength = p.getConfig().getInt("game.episode-length");
        world = UHCUtils.getWorldFromString(main, Bukkit.getServer(), p.getConfig().getString("world"));
        livePlayers = new HashSet<>();
        allPlayers = new HashSet<>();
        borderShrinking = false;
        active = false;
    }

    public void prep() {
        UHCUtils.exeCmd(Bukkit.getServer(), world, "fill -10 200 -10 10 202 10 barrier 0 hollow");
        UHCUtils.exeCmd(Bukkit.getServer(), world, "fill -9 202 -9 9 202 9 air");
        UHCUtils.exeCmd(Bukkit.getServer(), world, "setworldspawn 0 201 0");
        UHCUtils.exeCmd("gamerule spawnRadius 0");
        UHCUtils.exeCmd("gamerule doDaylightCycle false");
        UHCUtils.exeCmd("gamerule doWeatherCycle false");
        UHCUtils.exeCmd("time set 0");
        UHCUtils.exeCmd("tp @a 0 201 0");
        UHCUtils.exeCmd("worldborder set " + initSize);
    }

    public boolean start() {
        if (livePlayers.size() == 1 && !DEBUG) {
            main.getLogger().warning(ChatColor.RED + "Only one player is in the UHC!");
            Bukkit.broadcastMessage(ChatColor.RED + "UHC aborted! Only one player is in the UHC!");
        }
        long initT = System.currentTimeMillis();
        Bukkit.broadcastMessage(ChatColor.GREEN + "Game starting!");
        allPlayers = UHCUtils.getWorldPlayers(world);
        livePlayers = UHCUtils.getWorldLivePlayers(world, allPlayers);
        UHCUtils.exeCmd("fill -10 200 -10 10 202 10 air");
        UHCUtils.exeCmd("effect @a[m=0] minecraft:resistance 10 10 true");
        UHCUtils.exeCmd("gamemode 2 @a[m=0]");
        UHCUtils.exeCmd(Bukkit.getServer(), world, "spreadplayers 0 0 " + spreadDistance + " " + initSize / 2 + " " + respectTeams + " @a[m=2]");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
        main.getLogger().info("Game Initiate Time - " + sdf.format(new Date(initT)));
        active = true;
        freezePlayers = new PlayerMoveListener(main);
        Bukkit.getServer().getPluginManager().registerEvents(freezePlayers, main);
        loadChunksCDID = (new LoadingChunksCountdown(main, 5)).schedule();
        return true;
    }

    public void release() {
        Bukkit.getScheduler().cancelTask(loadChunksCDID);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerDeathListener(main), main);
        startT = System.currentTimeMillis();
        UHCUtils.exeCmd("gamemode 0 @a[m=2]");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
        main.getLogger().info("Game Start Time - " + sdf.format(new Date(startT)));
        UHCUtils.exeCmd("gamerule doDaylightCycle true");
        UHCUtils.exeCmd("gamerule doWeatherCycle true");
        borderCountdown = (new BorderCountdown(main, minsToShrink * 60, startT)).schedule();
        (new EpisodeAnnouncer(main, epLength, startT)).schedule();
        HandlerList.unregisterAll(freezePlayers);
        allPlayers.forEach(this::startPlayer);
    }

    public void stop() {
        if (!active) {
            return;
        }
        (new DelayedReset(main)).schedule();
        long stopT = System.currentTimeMillis();
        long timeElapsed = stopT - startT;
        startT = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
        main.getLogger().info("Game Stop Time - " + sdf.format(new Date(stopT)));
        main.getLogger().info("Time Elapsed: " + timeElapsed / 3600000 + " hours "
                + (timeElapsed / 60000) % 60 + " minutes "
                + (timeElapsed / 1000) % 60 + " seconds");
        active = false;
    }

    protected void startBorderShrink() {
        borderShrinking = true;
        UHCUtils.exeCmd(Bukkit.getServer(), world,
                "worldborder set " + finalSize + " " + calcBorderShrinkTime());
        main.getLogger().info("Game border shrinking from " + initSize + " " + " to " + finalSize
                + "over " + calcBorderShrinkTime() + " secs");
        Bukkit.getScheduler().cancelTask(borderCountdown);
    }

    public void checkForWin() {
        if (teamMode) {
            //TODO: team check
        } else {
            if (livePlayers.size() == 1) {
                Player winner = null;
                for (UUID u : livePlayers) {
                    winner = Bukkit.getPlayer(u);
                    break;
                }
                for (UUID u : allPlayers) {
                    Player p = Bukkit.getPlayer(u);
                    int timeElapsed = (int) (System.currentTimeMillis() - startT) / 1000;
                    int hours = timeElapsed / 3600;
                    int mins = timeElapsed / 60;
                    int secs = timeElapsed % 60;
                    p.sendMessage("\n" + ChatColor.GREEN + winner.getName() + ChatColor.WHITE + " wins!");
                    p.sendMessage(ChatColor.AQUA + "\nTime Elapsed: " + ChatColor.RESET + hours + " Hours " + mins + " Minutes " + secs + " Seconds");
                    p.sendTitle(winner.getName(), "Wins!", 0, 80, 40);
                }
            } else if (livePlayers.size() == 0) {
                for (UUID u : allPlayers) {
                    Player p = Bukkit.getPlayer(u);
                    p.sendMessage(ChatColor.RED + "\nWait... what? The game ended in a tie!");
                }
            } else {
                return;
            }
            stop();
        }
    }

    /**
     *  Helper/Access methods
     */

    public void startPlayer(UUID u) {
        Player p = Bukkit.getPlayer(u);
        p.sendTitle(ChatColor.BOLD + "" + ChatColor.GREEN + "GO!", UHCUtils.randomStartMSG(), 0, 80, 40);
        p.playSound(p.getLocation(), "minecraft:block.note.pling", 1F, 1F);
        p.playSound(p.getLocation(), "minecraft:entity.enderdragon.growl", 1F, 1F);
        p.setFoodLevel(20);
        p.setSaturation(5);
        p.setHealth(20);
    }

    public boolean validate() {
        boolean valid;
        valid = initSize > finalSize;
        valid = valid && spreadDistance < initSize;
        return valid;
    }

    public void logStatus() {
        Logger log = Bukkit.getServer().getLogger();
        String worldName;
        if (world != null) {
            worldName = world.getName();
        } else {
            worldName = "NONE";
        }
        log.info("World: " + worldName);
        log.info("Active: " + active);
        if (active) {
            log.info("Start Time: " + startT);
        }
    }

    private int calcBorderShrinkTime() {
        return (initSize - finalSize) * 2;
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

    public Set<UUID> getAllPlayers() {
        return allPlayers;
    }

    public void registerPlayer(Player p) {
        allPlayers.add(p.getUniqueId());
        if (p.getGameMode() == GameMode.SURVIVAL) {
            livePlayers.add(p.getUniqueId());
        }
    }

    public void removePlayerFromLive(Player p) {
        livePlayers.remove(p.getUniqueId());
    }

    public void unRegisterPlayer(Player p) {
        allPlayers.remove(p.getUniqueId());
        livePlayers.remove(p.getUniqueId());
    }

    protected void setEpLength(int el) {
        epLength = el;
    }

    protected void setGameWorld(String worldname) {
        world = UHCUtils.getWorldFromString(main, Bukkit.getServer(), worldname);
    }

    protected void setInitSize(int s) {
        initSize = s;
    }

    protected void setFinalSize(int s) {
        finalSize = s;
    }

    protected void setTimeToShrink(int minutes) {
        minsToShrink = minutes;
    }

    protected void setTeamMode(boolean b) {
        teamMode = b;
    }

    protected void setSpreadDistance(int sd) {
        spreadDistance = sd;
    }

    protected void setRespectTeams(boolean rt) {
        respectTeams = rt;
    }

    protected void setUHCMode(boolean um) {
        uhcMode = um;
    }
}
