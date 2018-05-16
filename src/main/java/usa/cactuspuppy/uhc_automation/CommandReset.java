package usa.cactuspuppy.uhc_automation;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.logging.Level;

public class CommandReset implements CommandExecutor {
    private Main main;

    public CommandReset(Main m) {
        main = m;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (main.gi.isActive()) {
            commandSender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Stopping game!");
            main.getLogger().log(Level.INFO, commandSender.getName() + " initiated command to halt game");
            main.gi.stop();
        } else {
            commandSender.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Resetting game!");
            main.getLogger().log(Level.INFO, commandSender.getName() + " initiated command to reset the game");
        }
        main.gi.prep();
        return true;
    }
}
