package usa.cactuspuppy.uhc_automation.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

import java.util.logging.Level;

public class CommandReset implements CommandExecutor {
    private Main main;

    public CommandReset(Main m) {
        main = m;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if (main.gi.isActive()) {
            UHCUtils.broadcastMessage(main.gi, ChatColor.RED.toString() + ChatColor.BOLD + "Stopping game!");
            main.getLogger().log(Level.INFO, commandSender.getName() + " initiated command to halt game");
            main.gi.stop();
        } else {
            UHCUtils.broadcastMessage(main.gi,ChatColor.YELLOW.toString() + ChatColor.BOLD + "Resetting game!");
            main.getLogger().log(Level.INFO, commandSender.getName() + " initiated command to reset the game");
        }
        main.gi.prep();
        return true;
    }
}
