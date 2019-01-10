package usa.cactuspuppy.uhc_automation.game.tasks.eventlisteners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

import java.util.HashSet;
import java.util.Set;

public class PlayerFreezer implements Listener {
    private static Set<Long> frozenGames = new HashSet<>();

    public static void addFrozenGame(GameInstance gameInstance) {
        frozenGames.add(gameInstance.getGameID());
    }

    @EventHandler
    public static void onPlayerMove(PlayerMoveEvent e) {

    }
}
