package usa.cactuspuppy.uhc_automation.task;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.*;
import usa.cactuspuppy.uhc_automation.InfoDisplayMode;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;
import usa.cactuspuppy.uhc_automation.commands.CommandInfo;

public class InfoAnnouncer implements Runnable {
    @Getter private Scoreboard timeScoreboard;
    @Getter private Objective obj;
    private Team TimeDisplay;
    private Team PlayersDisplay;
    private Team WBDisplay;
    private Team PVPDisplay;
    private Integer id;

    @Getter private static InfoAnnouncer instance;

    private static final String TIME_TEAM_ID = ChatColor.BLACK.toString() + ChatColor.WHITE.toString();
    private static final String OPP_REMAIN_ID = ChatColor.WHITE.toString() + ChatColor.AQUA.toString();
    private static final String WORLD_BORDER_ID = ChatColor.WHITE.toString() + ChatColor.BLACK.toString();
    private static final String PVP_ID = ChatColor.WHITE.toString() + ChatColor.RED.toString();
    private static final String BREAK_TOGGLE_INFO = ChatColor.YELLOW.toString() + ChatColor.BLACK.toString();

    public InfoAnnouncer() {
        instance = this;
        timeScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Main.getInstance().getGameInstance().setScoreboard(timeScoreboard);
        if (timeScoreboard.getObjective("Health2") == null) {
            timeScoreboard.registerNewObjective("Health2", "health", ChatColor.RED + "Health", RenderType.HEARTS).setDisplaySlot(DisplaySlot.PLAYER_LIST);
        } else if (!timeScoreboard.getObjective("Health2").getCriteria().equals("health")) {
            timeScoreboard.getObjective("Health2").unregister();
            timeScoreboard.registerNewObjective("Health2", "health", ChatColor.RED + "Health", RenderType.HEARTS).setDisplaySlot(DisplaySlot.PLAYER_LIST);
        }
        Objective healthOBJ = timeScoreboard.getObjective("Health2");
        if (healthOBJ != null) {
            healthOBJ.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        }
        TimeDisplay = timeScoreboard.registerNewTeam("Time");
        PlayersDisplay = timeScoreboard.registerNewTeam("Players");
        WBDisplay = timeScoreboard.registerNewTeam("Worldborder");
        PVPDisplay = timeScoreboard.registerNewTeam("PVP");
        regenerateObjective();
    }

    public void regenerateObjective() {
        if (timeScoreboard.getObjective("InfoDisplay") != null) {
            timeScoreboard.getObjective("InfoDisplay").unregister();
        }
        obj = timeScoreboard.registerNewObjective("InfoDisplay", "dummy", "Game Info");
        obj = timeScoreboard.getObjective("InfoDisplay");
        obj.setDisplayName(ChatColor.GOLD + Main.getInstance().getConfig().getString("event-name", "Game Info"));
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
        obj.getScore(BREAK_TOGGLE_INFO).setScore(7);
        obj.getScore(ChatColor.GRAY + "/uhc info toggle").setScore(6);
    }

    @Override
    public void run() {
        TimeDisplay.setPrefix(ChatColor.WHITE + "  " + UHCUtils.secsToFormatString2(UHCUtils.getSecsElapsed(Main.getInstance())));
        PlayersDisplay.setPrefix(ChatColor.WHITE + "  " + (Main.getInstance().getGameInstance().isTeamMode() ? Main.getInstance().getGameInstance().getNumTeams() : Main.getInstance().getGameInstance().getLivePlayers().size()));
        WBDisplay.setPrefix(ChatColor.WHITE + ((Main.getInstance().getGameInstance().getMinsToShrink() == -1) ? "  ±" + (Main.getInstance().getGameInstance().getInitSize() / 2) : (Main.getInstance().getGameInstance().isBorderShrinking() ?
                "  Shrinking to ±" + (Main.getInstance().getGameInstance().getFinalSize() / 2) :
                "  Shrinks in " + UHCUtils.secsToFormatString2((Main.getInstance().getGameInstance().getMinsToShrink() * 60) - UHCUtils.getSecsElapsed(Main.getInstance())))));
        PVPDisplay.setPrefix(ChatColor.WHITE + (Main.getInstance().getGameInstance().getWorld().getPVP() ?
                "  Enabled" :
                "  Enabled in " + UHCUtils.secsToFormatString2(((int) Main.getInstance().getGameInstance().getSecsToPVP()) - UHCUtils.getSecsElapsed(Main.getInstance()))));
        showInfo(CommandInfo.getMode());
    }

    private void showInfo(InfoDisplayMode mode) {
        if (mode == InfoDisplayMode.SCOREBOARD) {
            if (timeScoreboard.getObjective(DisplaySlot.SIDEBAR).equals(obj)) {
                return;
            }
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        } else {
            if (timeScoreboard.getObjective(DisplaySlot.SIDEBAR) == null) {
                return;
            }
            timeScoreboard.clearSlot(DisplaySlot.SIDEBAR);
        }
    }

    public void schedule() {
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), this, 0L, 2L);
    }

    public void showBoard() {
        Main.getInstance().getLogger().info("Showing board...");
        new InfoAnnouncer().schedule();
    }

    public void clearBoard() {
        Main.getInstance().getLogger().info("Clearing board...");
        if (id != null) {
            Bukkit.getScheduler().cancelTask(id);
        }
        timeScoreboard.clearSlot(DisplaySlot.SIDEBAR);
    }
}
