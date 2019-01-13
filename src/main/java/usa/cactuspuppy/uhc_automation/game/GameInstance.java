package usa.cactuspuppy.uhc_automation.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import usa.cactuspuppy.uhc_automation.entity.unique.Team;
import usa.cactuspuppy.uhc_automation.game.games.GameInfo;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
public abstract class GameInstance {
    @Getter protected GameInfo gameInfo;

    public long getGameID() {
        return gameInfo.getGameID();
    }

    /** Checks whether current gameInfo is valid to start game.
     * This method is responsible for reporting */
    public boolean validate() { return true; }

    /** Preps all worlds under the purview of this game for play.
     * Tasks executed by this method should include things such as
     * creating the waiting lobby, setting appropriate gamerules
     * for waiting, and preparing worlds if necessary.
     * @return whether prep was successful
      */
    public boolean prep() { return false; }

    /** Initiates the game. This method differs from {@code start} as it is called to perform pre-gameplay tasks (i.e. spread players, prepare starting locations, etc.). 
     * @return Whether the game was successfully initiated
     */
    public boolean init() { return false; }

    /**
     *
     * @return  whether the game was started successfully
     */
    public boolean start() { return false; }
    /** Returns whether the game was paused successfully */
    public boolean pause() { return false; }

    /** Called to terminate the game for any reason */
    public void end() {}

    /** Called when a victory condition is met */
    public void win() {}

    public void broadcastAndLog(String msg, Logger.Level level) {
        Logger.log(level, this.getClass(), "[" + getGameInfo().getName() + "(" + getGameInfo().getGameID() + ")]" + msg);
        getGameInfo().getAllPlayers().stream().map(Bukkit::getPlayer).forEach(p -> p.sendMessage(ChatColor.RED + msg));
    }

    public void broadcastAndLog(String msg) {
        broadcastAndLog(msg, Logger.Level.INFO);
    }

    public void announceFailToInit(String msg) {
        broadcastAndLog("Failed to start game. Reason: " + msg);
    }
}
