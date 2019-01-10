package usa.cactuspuppy.uhc_automation.game.games;

public class PvN extends UHC {
    public PvN(GameInfo gameInfo) {
        super(gameInfo);
        gameInfo.setName("Pirates-vs-Ninjas-" + gameInfo.getGameID());
        gameInfo.setDisplayName("Pirates vs. Ninjas UHC");
        gameInfo.setGameType(this.getClass().getSimpleName());
    }

    @Override
    public boolean start() {
        //TODO: Validate game conditions
        //TODO: Broadcast game starting
        //TODO: Set players to correct gamemode
        //TODO:
    }
}
