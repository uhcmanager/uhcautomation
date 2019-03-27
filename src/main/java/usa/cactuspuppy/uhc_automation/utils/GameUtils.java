package usa.cactuspuppy.uhc_automation.utils;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

import java.util.Objects;

@AllArgsConstructor
public final class GameUtils {
    private GameInstance instance;

    /**
     * Teleport specified player(s) to the specified location while maintaining the player's orientation
     * @param players Player(s) to teleport relatively
     * @param destination Location to teleport to
     */
    public static void relativeTeleport(@NonNull Location destination, Player... players) {
        for (Player p : players) {
            destination.setYaw(p.getLocation().getYaw());
            destination.setPitch(p.getLocation().getPitch());
            p.teleport(destination);
        }
    }

    //BROADCASTING
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

    //LOGGING
    public void log(Logger.Level level, Class c, String msg, Exception e) {
        Logger.log(level, c, " [" + instance.getName() + "](ID: " + instance.getGameID() + ") " + msg, e);
    }

    public void log(Logger.Level level, Class c, String msg) {
        Logger.log(level, c, " [" + instance.getName() + "](ID: " + instance.getGameID() + ") " + msg, null);
    }
}
