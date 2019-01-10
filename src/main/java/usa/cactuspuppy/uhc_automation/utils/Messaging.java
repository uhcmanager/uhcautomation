package usa.cactuspuppy.uhc_automation.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public final class Messaging {
    public static void sendActionBar(Player player, String message){
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }
}
