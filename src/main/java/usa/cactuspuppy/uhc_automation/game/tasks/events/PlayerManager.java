package usa.cactuspuppy.uhc_automation.game.tasks.events;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.GameManager;

import java.util.UUID;

public class PlayerManager {
    public static void onPlayerJoin(PlayerJoinEvent e) {
        GameInstance instance = GameManager.getWorldGameID(e.getPlayer().getLocation().getWorld().getUID());
        if (instance == null) return;
        UUID playerUID = e.getPlayer().getUniqueId();
        if (instance.getGameInfo().isInitiated()) {
            if (!instance.getGameInfo().getAllPlayers().contains(playerUID)) {
                instance.getGameInfo().addSpectator(playerUID);
                GameManager.playerJoinGame(playerUID, instance.getGameID());
            }
            e.getPlayer().setGameMode(GameMode.SPECTATOR);
        } else if (!instance.getGameInfo().getAllPlayers().contains(playerUID)) {
            GameMode mode = e.getPlayer().getGameMode();
            if (mode.equals(GameMode.CREATIVE) || mode.equals(GameMode.SPECTATOR)) {
                instance.getGameInfo().addSpectator(playerUID);
                return;
            } else {
                instance.getGameInfo().addAlivePlayer(playerUID);
            }
            GameManager.playerJoinGame(playerUID, instance.getGameID());
        }
    }

    public static void onWorldChange(PlayerTeleportEvent e, World from, World to) {
        GameInstance fromGame = GameManager.getWorldGameID(from.getUID());
        GameInstance toGame = GameManager.getWorldGameID(to.getUID());
        if (fromGame == null || fromGame.equals(toGame)) return;
        UUID playerUID = e.getPlayer().getUniqueId();
        //TODO: Implement world changing
    }
}
