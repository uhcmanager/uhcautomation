package usa.cactuspuppy.uhc_automation.game.tasks.eventlisteners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.GameManager;
import usa.cactuspuppy.uhc_automation.utils.Messaging;

import java.util.HashSet;
import java.util.Set;

public class PlayerFreezer implements Listener {
    private static Set<Long> frozenGames = new HashSet<>();

    public static void addFrozenGame(GameInstance gameInstance) {
        frozenGames.add(gameInstance.getGameID());
    }

    public static void unfreezeGame(GameInstance gameInstance) {
        frozenGames.remove(gameInstance.getGameID());
    }

    @EventHandler
    public static void onPlayerMove(PlayerMoveEvent e) {
        GameInstance check = GameManager.getPlayerGame(e.getPlayer().getUniqueId());
        if (check == null) return;
        if (!frozenGames.contains(check.getGameID())) return;
        if (!check.getGameInfo().getAlivePlayers().contains(e.getPlayer().getUniqueId())) return;
        if (e.getFrom().getY() == e.getTo().getY()) return;
        e.setCancelled(true);
        e.getPlayer().teleport(e.getFrom());
        Messaging.sendActionBar(e.getPlayer(), ChatColor.RED + "Please remain calm until the game beings");
    }
}
