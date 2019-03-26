package usa.cactuspuppy.uhc_automation.entity.tasks.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

public class UHC_PreStartFreeze extends ListenerTask implements Listener {
    public UHC_PreStartFreeze(GameInstance gameInstance) {
        super(gameInstance);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (!gameInstance.getAlivePlayers().contains(e.getPlayer().getUniqueId())) { return; }
        if (e.getPlayer().getVelocity().getY() > 0) { //Jumping
            e.setCancelled(true);
            e.getPlayer().teleport(e.getFrom());
            e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Please remain still until the game beings!"));
        }
    }
}
