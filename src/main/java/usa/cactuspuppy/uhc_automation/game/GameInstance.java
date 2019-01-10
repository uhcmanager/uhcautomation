package usa.cactuspuppy.uhc_automation.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import usa.cactuspuppy.uhc_automation.game.games.GameInfo;

@AllArgsConstructor
public abstract class GameInstance {
    @Getter private GameInfo gameInfo;

    /** Preps all worlds under the purview of this game for play.
     * @return whether prep was successful
      */
    public boolean prep() { return false; }

    /** Returns whether the game was successfully initiated.
     * This method differs from {@code start} as it is called to perform pre-gameplay tasks (i.e. spread players, prepare starting locations, etc.)
      */
    public boolean init() { return false; }
    /** Returns whether the game was started successfully */
    public boolean start() { return false; }
    /** Returns whether the game was paused successfully */
    public boolean pause() { return false; }

    /** Called to terminate the game for any reason */
    public void end() {}

    /** Called when a victory condition is met */
    public void win() {}
}
