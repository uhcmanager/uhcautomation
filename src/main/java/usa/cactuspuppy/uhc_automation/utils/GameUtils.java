package usa.cactuspuppy.uhc_automation.utils;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public final class GameUtils {
    private GameInstance instance;

    public void msgManagers(String chatMessage) {
        instance.getAllPlayers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).filter(p -> p.hasPermission("uhc.manager")).forEach(
                player -> player.sendMessage(chatMessage)
        );
    }

    //BROADCASTING
    public void broadcastChat(String chatMessage) {
        instance.getAllPlayers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(
                p -> p.sendMessage(chatMessage)
        );
    }

    public void broadcastSound(Sound sound, float pitch) {
        instance.getAllPlayers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(
                p -> p.playSound(p.getLocation(), sound, 10.0F, pitch)
        );
    }

    public void broadcastSound(String sound, float pitch) {
        instance.getAllPlayers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(
                p -> p.playSound(p.getLocation(), sound, 10.0F, pitch)
        );
    }

    public void broadcastTitle(String title, String subtitle, int in, int stay, int out) {
        instance.getAllPlayers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(
                p -> p.sendTitle(title, subtitle, in, stay, out)
        );
    }

    public void broadcastChatSound(String chatMessage, Sound sound, float pitch) {
        Set<Player> playerSet = instance.getAllPlayers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toSet());
        for (Player p : playerSet) {
            p.sendMessage(chatMessage);
            p.playSound(p.getLocation(), sound, 10F, pitch);
        }
    }

    public void broadcastChatSound(String chatMessage, String sound, float pitch) {
        Set<Player> playerSet = instance.getAllPlayers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toSet());
        for (Player p : playerSet) {
            p.sendMessage(chatMessage);
            p.playSound(p.getLocation(), sound, 10F, pitch);
        }
    }

    public void broadcastChatTitle(String chatMessage, String title, String subtitle, int in, int stay, int out) {
        Set<Player> playerSet = instance.getAllPlayers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toSet());
        for (Player p : playerSet) {
            p.sendMessage(chatMessage);
            p.sendTitle(title, subtitle, in, stay, out);
        }
    }

    public void broadcastSoundTitle(Sound sound, float pitch, String title, String subtitle, int in, int stay, int out) {
        Set<Player> playerSet = instance.getAllPlayers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toSet());
        for (Player p : playerSet) {
            p.playSound(p.getLocation(), sound, 10F, pitch);
            p.sendTitle(title, subtitle, in, stay, out);
        }
    }

    public void broadcastSoundTitle(String sound, float pitch, String title, String subtitle, int in, int stay, int out) {
        Set<Player> playerSet = instance.getAllPlayers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toSet());
        for (Player p : playerSet) {
            p.playSound(p.getLocation(), sound, 10F, pitch);
            p.sendTitle(title, subtitle, in, stay, out);
        }
    }

    public void broadcastChatSoundTitle(String chatMessage, Sound sound, float pitch, String title, String subtitle, int in, int stay, int out) {
        Set<Player> playerSet = instance.getAllPlayers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toSet());
        for (Player p : playerSet) {
            p.sendMessage(chatMessage);
            p.playSound(p.getLocation(), sound, 10F, pitch);
            p.sendTitle(title, subtitle, in, stay, out);
        }
    }

    public void broadcastChatSoundTitle(String chatMessage, String sound, float pitch, String title, String subtitle, int in, int stay, int out) {
        Set<Player> playerSet = instance.getAllPlayers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toSet());
        for (Player p : playerSet) {
            p.sendMessage(chatMessage);
            p.playSound(p.getLocation(), sound, 10F, pitch);
            p.sendTitle(title, subtitle, in, stay, out);
        }
    }



    //LOGGING
    public void log(Logger.Level level, Class c, String msg, Exception e) {
        Logger.log(level, c, " [" + instance.getName() + "](GID: " + instance.getGameID() + ") " + msg, e);
    }

    public void log(Logger.Level level, Class c, String msg) {
        Logger.log(level, c, " [" + instance.getName() + "](GID: " + instance.getGameID() + ") " + msg, null);
    }
}
