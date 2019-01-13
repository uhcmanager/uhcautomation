package usa.cactuspuppy.uhc_automation.game.games;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import usa.cactuspuppy.uhc_automation.game.tasks.countdowns.LoadChunksCountdown;
import usa.cactuspuppy.uhc_automation.game.tasks.events.PlayerFreezer;
import usa.cactuspuppy.uhc_automation.utils.Logger;
import usa.cactuspuppy.uhc_automation.utils.Messaging;

public class PvN extends TeamGameInstance {
    public PvN(GameInfo gameInfo) {
        super(gameInfo);
        gameInfo.setName("Pirates-vs-Ninjas-" + gameInfo.getGameID());
        gameInfo.setDisplayName("Pirates vs. Ninjas UHC");
        gameInfo.setGameType(this.getClass().getSimpleName());
    }

    @Override
    public boolean init() {
        if (!validate()) return false;
        //TODO: Spreadplayers
        UHCUtils.spreadplayers(this);
        //TODO: Freeze players
        PlayerFreezer.addFrozenGame(this);
        //TODO: Create countdown to start
        new LoadChunksCountdown(this);
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
        Messaging.broadcastMessage(this, ChatColor.GREEN + "Begin!");
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
