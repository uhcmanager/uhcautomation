package usa.cactuspuppy.uhc_automation.game.tasks.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

import java.util.Objects;

public class UHC_PreStartFreeze extends ListenerTask implements Listener {
    public UHC_PreStartFreeze(GameInstance gameInstance) {
        super(gameInstance);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getTo() == null) {
            return;
        }
        if (!Objects.equals(e.getFrom().getWorld(), e.getTo().getWorld())) {
            return;
        }
        if (!getGameInstance().getAlivePlayers().contains(e.getPlayer().getUniqueId())) {
            return;
        }
        if (e.getPlayer().getVelocity().getY() > 0) { //Jumping
            cancel(e);
        }
        if (e.getPlayer().isOnGround() && (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ())) { //Moving on ground
            cancel(e);
        }
    }

    private void cancel(PlayerMoveEvent e) {
        e.setCancelled(true);
        e.getPlayer().teleport(e.getFrom());
        e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Please remain still until the game beings!"));
    }
}
