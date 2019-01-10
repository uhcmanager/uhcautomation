package usa.cactuspuppy.uhc_automation.game.games;

import org.bukkit.Bukkit;
import usa.cactuspuppy.uhc_automation.game.tasks.eventlisteners.PlayerFreezer;
import usa.cactuspuppy.uhc_automation.utils.Logger;

public class PvN extends UHC {
    public PvN(GameInfo gameInfo) {
        super(gameInfo);
        gameInfo.setName("Pirates-vs-Ninjas-" + gameInfo.getGameID());
        gameInfo.setDisplayName("Pirates vs. Ninjas UHC");
        gameInfo.setGameType(this.getClass().getSimpleName());
    }

    @Override
    public boolean init() {
        if (!validate()) return false;
        //TODO: Finalize player statuses
        //TODO: Set player conditions
        //TODO: Spreadplayers
        //TODO: Freeze players

        //TODO: Create countdown to start
        return true;
    }

    @Override
    public boolean validate() {
        if (Logger.isDebug()) return true;
        if (getGameInfo().getAlivePlayers().size() < 2){
            announceFailToInit("Not enough players");
            return false;
        }
        if (getGameInfo().getSepDistance() < 0.0D) {
            announceFailToInit("Separation distance is too small");
            return false;
        }
        if (getGameInfo().getInitialRadius() < getGameInfo().getSepDistance() + 1.0) {
            announceFailToInit("Play area is too small for required separation distance");
            return false;
        }
        if (Bukkit.getWorld(gameInfo.getWorldID()) == null) {
            announceFailToInit("No main world specified");
            return false;
        }
        //TODO: Check for other validations
        return true;
    }

    @Override
    public boolean start() {
        long now = System.currentTimeMillis();
        getGameInfo().setStartTime(now);
        //TODO: Unfreeze players
        PlayerFreezer.unfreezeGame(this);
        //TODO: Broadcast to players to start
        //TODO: Set players to correct gamemode
        //TODO: Set gamerules
        //TODO: Set weather
        //TODO: Set border
        //TODO: Give boats
        gameInfo.setCurrEp(1);
        //TODO: Launch episode announcer
        return true;
    }
}
