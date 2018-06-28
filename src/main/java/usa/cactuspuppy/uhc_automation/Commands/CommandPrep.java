package usa.cactuspuppy.uhc_automation.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import usa.cactuspuppy.uhc_automation.Main;

public class CommandPrep implements CommandExecutor {
    private Main m;
    public CommandPrep(Main main) {
        m = main;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (m.gi.isActive()) {
            commandSender.sendMessage(ChatColor.RED + "Game is currently active, use /uhcstop to stop the game or wait until the current game is finished before attempt to prep the world.");
        } else {
            m.gi.prep();
        }
        return true;
    }
}
