package usa.cactuspuppy.uhc_automation.event;

import usa.cactuspuppy.uhc_automation.event.game.border.BorderBeginShrinkEvent;
import usa.cactuspuppy.uhc_automation.event.game.border.BorderSpeedUpEvent;
import usa.cactuspuppy.uhc_automation.event.game.group.*;
import usa.cactuspuppy.uhc_automation.event.game.team.TeamAddGroupsEvent;
import usa.cactuspuppy.uhc_automation.event.game.team.TeamCreateEvent;
import usa.cactuspuppy.uhc_automation.event.game.team.TeamDeleteEvent;
import usa.cactuspuppy.uhc_automation.event.game.team.TeamRemoveGroupsEvent;
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

    //PLAYER
    public void onPlayerJoin(PlayerJoinEvent e) {}

    public void onPlayerLeave(PlayerLeaveEvent e) {}

    public void onPlayerDeath(PlayerDeathEvent e) {}

    //BORDER
    public void onBorderBeginShrink(BorderBeginShrinkEvent e) {}

    public void onBorderSpeedUp(BorderSpeedUpEvent e) {}

    //GROUP
    public void onGroupCreate(GroupCreateEvent e) {}

    public void onGroupAddPlayers(GroupAddPlayersEvent e) {}

    public void onGroupRemovePlayers(GroupRemovePlayersEvent e) {}

    public void onGroupSetTeam(GroupSetTeamEvent e) {}

    public void onGroupDelete(GroupDeleteEvent e) {}

    //TEAM
    public void onTeamCreate(TeamCreateEvent e) {}

    public void onTeamDelete(TeamDeleteEvent e) {}

    public void onTeamAddGroups(TeamAddGroupsEvent e) {}

    public void onTeamRemoveGroups(TeamRemoveGroupsEvent e) {}

    //UPDATE
    public void onEpisodeMark(EpisodeMarkEvent e) {}

    public void onGameStart(GameStartEvent e) {}

    public void onGameEnd(GameEndEvent e) {}

    public void onGamePause(GamePauseEvent e) {}
}
