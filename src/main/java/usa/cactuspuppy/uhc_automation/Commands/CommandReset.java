package usa.cactuspuppy.uhc_automation.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.Tasks.PreGameCountdown;
import usa.cactuspuppy.uhc_automation.UHCUtils;

import java.util.logging.Level;

public class CommandReset {
    public static void onCommand(CommandSender commandSender) {
        if (Main.getInstance().getGameInstance().isActive()) {
            UHCUtils.broadcastMessage(Main.getInstance().getGameInstance(), ChatColor.RED.toString() + ChatColor.BOLD + "Stopping game!");
            Main.getInstance().getLogger().info(commandSender.getName() + " initiated command to halt game");
            Main.getInstance().getGameInstance().stop();
        } else if (PreGameCountdown.getInstance() != null) {
            PreGameCountdown.stop();
            UHCUtils.broadcastMessage(ChatColor.RED + "Countdown halted!");
            Main.getInstance().getLogger().info(commandSender.getName() + " halted game countdown");
        } else {
            UHCUtils.broadcastMessage(Main.getInstance().getGameInstance(),ChatColor.YELLOW.toString() + ChatColor.BOLD + "Resetting game!");
            Main.getInstance().getLogger().log(Level.INFO, commandSender.getName() + " initiated command to reset the game");
        }
        Main.getInstance().getGameInstance().prep();
    }
}
