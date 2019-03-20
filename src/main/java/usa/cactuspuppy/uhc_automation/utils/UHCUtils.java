package usa.cactuspuppy.uhc_automation.utils;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
public final class UHCUtils {
    private GameInstance instance;

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
}
