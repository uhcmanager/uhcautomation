package usa.cactuspuppy.uhc_automation.event;

import usa.cactuspuppy.uhc_automation.event.game.border.BorderBeginShrinkEvent;
import usa.cactuspuppy.uhc_automation.event.game.group.GroupCreateEvent;
import usa.cactuspuppy.uhc_automation.event.game.group.GroupMergeEvent;
import usa.cactuspuppy.uhc_automation.event.game.team.TeamCreateEvent;
import usa.cactuspuppy.uhc_automation.event.game.team.TeamDeleteEvent;
import usa.cactuspuppy.uhc_automation.event.game.update.EpisodeMarkEvent;
import usa.cactuspuppy.uhc_automation.event.game.update.GameEndEvent;
import usa.cactuspuppy.uhc_automation.event.game.update.GamePauseEvent;
import usa.cactuspuppy.uhc_automation.event.game.update.GameStartEvent;
import usa.cactuspuppy.uhc_automation.event.game.player.PlayerDeathEvent;
import usa.cactuspuppy.uhc_automation.event.game.player.PlayerJoinEvent;
import usa.cactuspuppy.uhc_automation.event.game.player.PlayerLeaveEvent;

public abstract class EventListener {

    public EventListener() {
        EventDistributor.addListener(this);
    }

    /** Called when the plugin is enabled */
    public void onEnable() {}

    public void onPlayerJoin(PlayerJoinEvent e) {}

    public void onPlayerLeave(PlayerLeaveEvent e) {}

    public void onPlayerDeath(PlayerDeathEvent e) {}

    public void onBorderBeginShrink(BorderBeginShrinkEvent e) {}

    public void onGroupCreate(GroupCreateEvent e) {}

    public void onGroupMerge(GroupMergeEvent e) {}

    public void onTeamCreate(TeamCreateEvent e) {}

    public void onTeamDelete(TeamDeleteEvent e) {}

    public void onEpisodeMark(EpisodeMarkEvent e) {}

    public void onGameStart(GameStartEvent e) {}

    public void onGameEnd(GameEndEvent e) {}

    public void onGamePause(GamePauseEvent e) {}
}
