package usa.cactuspuppy.uhc_automation.utils;

import com.sun.istack.internal.NotNull;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
public final class GameUtils {
    private GameInstance instance;

    /**
     * Teleport specified player(s) to the specified location while maintaining the player's orientation
     * @param players Player(s) to teleport relatively
     * @param destination Location to teleport to
     */
    public static void relativeTeleport(@NotNull Location destination, Player... players) {
        for (Player p : players) {
            destination.setYaw(p.getLocation().getYaw());
            destination.setPitch(p.getLocation().getPitch());
            p.teleport(destination);
        }
    }

    //TODO: Broadcast methods
    public void broadcastChatMessage(String chatMessage) {
        instance.getAllPlayers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(
                p -> p.sendMessage(chatMessage)
        );
    }

    public void broadcastSound(Sound sound, float pitch) {
        instance.getAllPlayers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(
                p -> p.playSound(p.getLocation(), sound, 10.0F, pitch)
        );
    }

    public void broadcastTitle(String title, String subtitle, int in, int stay, int out) {
        instance.getAllPlayers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(
                p -> p.sendTitle(title, subtitle, in, stay, out)
        );
    }

    public void log(Logger.Level level, Class c, String msg, Exception e) {
        Logger.log(level, c, " [" + instance.getName() + "](ID: " + instance.getGameID() + ") " + msg, e);
    }

    public void log(Logger.Level level, Class c, String msg) {
        Logger.log(level, c, " [" + instance.getName() + "](ID: " + instance.getGameID() + ") " + msg, null);
    }
}
