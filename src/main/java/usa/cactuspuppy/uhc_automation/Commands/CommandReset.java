package usa.cactuspuppy.uhc_automation.Commands;

import lombok.NoArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

import java.util.logging.Level;

@NoArgsConstructor
public class CommandReset implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if (Main.getInstance().getGameInstance().isActive()) {
            UHCUtils.broadcastMessage(Main.getInstance().getGameInstance(), ChatColor.RED.toString() + ChatColor.BOLD + "Stopping game!");
            Main.getInstance().getLogger().log(Level.INFO, commandSender.getName() + " initiated command to halt game");
            Main.getInstance().getGameInstance().stop();
        } else {
            UHCUtils.broadcastMessage(Main.getInstance().getGameInstance(),ChatColor.YELLOW.toString() + ChatColor.BOLD + "Resetting game!");
            Main.getInstance().getLogger().log(Level.INFO, commandSender.getName() + " initiated command to reset the game");
        }
        Main.getInstance().getGameInstance().prep();
        return true;
    }
}
