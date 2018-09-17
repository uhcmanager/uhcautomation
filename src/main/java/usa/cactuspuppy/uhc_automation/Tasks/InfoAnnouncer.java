package usa.cactuspuppy.uhc_automation.Tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import usa.cactuspuppy.uhc_automation.InfoDisplayMode;
import usa.cactuspuppy.uhc_automation.InfoModeCache;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class InfoAnnouncer implements Runnable {
    private Set<UUID> objectivePlayerSet = new HashSet<>();
    private Scoreboard timeScoreboard;
    private Objective obj;
    private Team TimeDisplay;
    private Team PlayersDisplay;
    private Team WBDisplay;
    private Team PVPDisplay;


    private static final String TIME_TEAM_ID = ChatColor.BLACK.toString() + ChatColor.WHITE.toString();
    private static final String OPP_REMAIN_ID = ChatColor.WHITE.toString() + ChatColor.AQUA.toString();
    private static final String WORLD_BORDER_ID = ChatColor.WHITE.toString() + ChatColor.BLACK.toString();
    private static final String PVP_ID = ChatColor.WHITE.toString() + ChatColor.RED.toString();

    public InfoAnnouncer() {
        timeScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        if (timeScoreboard.getObjective("InfoDisplay") == null) {
            timeScoreboard.registerNewObjective("InfoDisplay", "dummy", "Game Info");
        } else if (!timeScoreboard.getObjective("InfoDisplay").getCriteria().equals("dummy")) {
            timeScoreboard.getObjective("InfoDisplay").unregister();
            timeScoreboard.registerNewObjective("InfoDisplay", "dummy", "Game Info");
        }
        if (timeScoreboard.getObjective("Health2") == null) {
            timeScoreboard.registerNewObjective("Health2", "health", "Health").setDisplaySlot(DisplaySlot.PLAYER_LIST);
        } else if (!timeScoreboard.getObjective("Health2").getCriteria().equals("health")) {
            timeScoreboard.getObjective("Health2").unregister();
            timeScoreboard.registerNewObjective("Health2", "health", "Health").setDisplaySlot(DisplaySlot.PLAYER_LIST);
        }
        obj = timeScoreboard.getObjective("InfoDisplay");
        obj.setDisplayName(ChatColor.GOLD + Main.getInstance().getConfig().getString("event-name", "Game Info"));
        TimeDisplay = timeScoreboard.registerNewTeam("Time");
        PlayersDisplay = timeScoreboard.registerNewTeam("Players");
        WBDisplay = timeScoreboard.registerNewTeam("Worldborder");
        PVPDisplay = timeScoreboard.registerNewTeam("PVP");
        TimeDisplay.addEntry(TIME_TEAM_ID);
        PlayersDisplay.addEntry(OPP_REMAIN_ID);
        WBDisplay.addEntry(WORLD_BORDER_ID);
        PVPDisplay.addEntry(PVP_ID);
        obj.getScore(ChatColor.AQUA + "» Time Elapsed:").setScore(15);
        obj.getScore(TIME_TEAM_ID).setScore(14);
        obj.getScore(ChatColor.GREEN + "» " + (Main.getInstance().getGameInstance().isTeamMode() ? "Teams" : "Players") +" Remaining:").setScore(13);
        obj.getScore(OPP_REMAIN_ID).setScore(12);
        obj.getScore(ChatColor.YELLOW + "» World Border:").setScore(11);
        obj.getScore(WORLD_BORDER_ID).setScore(10);
        obj.getScore(ChatColor.RED + "» PVP:").setScore(9);
        obj.getScore(PVP_ID).setScore(8);
    }

    public void removePlayerFromObjectiveSet(Player p) {
        Main.getInstance().getGameInstance().bindPlayertoScoreboard(p);
        objectivePlayerSet.remove(p.getUniqueId());
    }

    @Override
    public void run() {
        TimeDisplay.setPrefix(ChatColor.WHITE + "  " + UHCUtils.secsToFormatString2(UHCUtils.getSecsElapsed(Main.getInstance())));
        PlayersDisplay.setPrefix(ChatColor.WHITE + "  " + String.valueOf(Main.getInstance().getGameInstance().isTeamMode() ? Main.getInstance().getGameInstance().getNumTeams() : Main.getInstance().getGameInstance().getLivePlayers().size()));
        WBDisplay.setPrefix(ChatColor.WHITE + (Main.getInstance().getGameInstance().isBorderShrinking() ?
                "Shrinking to ±" + (Main.getInstance().getGameInstance().getFinalSize() / 2) :
                "Shrinks in " + UHCUtils.secsToFormatString2((Main.getInstance().getGameInstance().getMinsToShrink() * 60) - UHCUtils.getSecsElapsed(Main.getInstance()))));
        PVPDisplay.setPrefix(ChatColor.WHITE + (Main.getInstance().getGameInstance().getWorld().getPVP() ?
                "Enabled" :
                "Enabled in " + UHCUtils.secsToFormatString2(((int) Main.getInstance().getGameInstance().getSecsToPVP()) - UHCUtils.getSecsElapsed(Main.getInstance()))));
        Main.getInstance().getGameInstance().getActivePlayers().stream().map(Bukkit::getPlayer).forEach(this::showInfoToPlayer);
    }

    private void showInfoToPlayer(Player player) {
        InfoDisplayMode tdm = InfoModeCache.getInstance().getPlayerPref(player.getUniqueId());
        if (tdm == null) {
            Bukkit.getLogger().warning(player.getName() + " possess an invalid InfoDisplayMode. Setting to default (SCOREBOARD)...");
            InfoModeCache.getInstance().storePlayerPref(player.getUniqueId(), InfoDisplayMode.SCOREBOARD);
        }
        if (tdm == InfoDisplayMode.SCOREBOARD) {
            if (!objectivePlayerSet.contains(player.getUniqueId())) {
                player.setScoreboard(timeScoreboard);
                objectivePlayerSet.add(player.getUniqueId());
            }
        }
    }

    public void schedule() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), this, 0L, 2L);
    }

    public void showBoard() {
        if (timeScoreboard.getObjective("FILL") != null) {
            timeScoreboard.getObjective("FILL").unregister();
        }
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void clearBoard() {
        if (timeScoreboard.getObjective("FILL") != null) {
            timeScoreboard.getObjective("FILL").unregister();
        }
        timeScoreboard.registerNewObjective("FILL", "dummy", "FILL").setDisplaySlot(DisplaySlot.SIDEBAR);
    }
}
