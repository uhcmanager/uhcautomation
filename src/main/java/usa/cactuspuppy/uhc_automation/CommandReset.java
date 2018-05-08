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
        commandSender.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Stopping game!");
        main.getLogger().log(Level.FINE, commandSender.getName() + " initiated command to halt game");
        main.gi.stop();
        return true;
    }
}
